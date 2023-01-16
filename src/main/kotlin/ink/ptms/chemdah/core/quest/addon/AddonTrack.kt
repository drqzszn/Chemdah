package ink.ptms.chemdah.core.quest.addon

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.Hologram
import ink.ptms.chemdah.api.ChemdahAPI
import ink.ptms.chemdah.api.ChemdahAPI.chemdahProfile
import ink.ptms.chemdah.api.ChemdahAPI.nonChemdahProfileLoaded
import ink.ptms.chemdah.api.event.collect.PlayerEvents
import ink.ptms.chemdah.core.PlayerProfile
import ink.ptms.chemdah.core.quest.*
import ink.ptms.chemdah.core.quest.addon.AddonDepend.Companion.isQuestDependCompleted
import ink.ptms.chemdah.core.quest.addon.AddonOptional.Companion.isOptional
import ink.ptms.chemdah.core.quest.addon.AddonUI.Companion.ui
import ink.ptms.chemdah.core.quest.addon.data.*
import ink.ptms.chemdah.core.quest.meta.MetaName.Companion.displayName
import ink.ptms.chemdah.util.conf
import ink.ptms.chemdah.util.namespaceQuestUI
import ink.ptms.chemdah.util.replace
import ink.ptms.chemdah.util.splitBy
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.function.*
import taboolib.common.util.asList
import taboolib.common.util.resettableLazy
import taboolib.common.util.unsafeLazy
import taboolib.common5.Baffle
import taboolib.common5.format
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.chat.colored
import taboolib.module.kether.KetherFunction
import taboolib.module.kether.KetherShell
import taboolib.module.nms.sendScoreboard
import java.util.concurrent.ConcurrentHashMap

/**
 * Chemdah
 * ink.ptms.chemdah.core.quest.addon.AddonTrack
 *
 * @author sky
 * @since 2021/3/11 9:05 上午
 */
@Suppress("DuplicatedCode")
@Id("track")
@Option(Option.Type.SECTION)
class AddonTrack(config: ConfigurationSection, questContainer: QuestContainer) : Addon(config, questContainer) {

    /**
     * 引导的目的地
     */
    val center by unsafeLazy {
        val center = config.getString("center")
        when {
            // 无追踪
            center.isNullOrEmpty() -> NullTrackCenter
            // 追踪单位
            center.startsWith("adyeshach") -> AdyeshachTrackCenter(center.substringAfter("adyeshach").trim())
            // 追踪坐标
            else -> LocationTrackCenter(center)
        }
    }

    /**
     * 引导开启的提示消息
     * 使用 $ 指向语言文件节点
     */
    val message = config["message"]?.asList()?.colored() ?: conf.getString("default-track.message")?.asList()?.colored()

    /** 追踪名称 */
    val name = config.getString("name")?.colored()

    /** 追踪描述 */
    val description = config["description"]?.asList()?.flatMap { it.lines() }?.colored()

    /** 信标配置 */
    val beacon = TrackBeacon(config, conf.getConfigurationSection("default-track.beacon") ?: error("default-track.beacon not found"))

    /** 地标配置 */
    val landmark = TrackLandmark(config, conf.getConfigurationSection("default-track.landmark") ?: error("default-track.landmark not found"))

    /** 引导配置 */
    val navigation = TrackNavigation(config, conf.getConfigurationSection("default-track.navigation") ?: error("default-track.navigation not found"))

    /** 记分板配置 */
    val scoreboard = TrackScoreboard(config, conf.getConfigurationSection("default-track.scoreboard") ?: error("default-track.scoreboard not found"))

    /** 使用 Kether 格式化描述 */
    fun formatDesc(sender: CommandSender, questSelected: String, def: List<String>): List<String> {
        return KetherFunction.parse(
            description ?: def,
            namespace = namespaceQuestUI,
            sender = adaptCommandSender(sender),
            vars = KetherShell.VariableMap("@QuestSelected" to questSelected)
        )
    }

    companion object {

        /** 地标全息缓存 */
        internal val landmarkHologramMap = ConcurrentHashMap<String, MutableMap<String, Hologram<*>>>()

        /** 需要在玩家离线时释放的阻断容器 */
        internal val releaseCounterMap = ConcurrentHashMap<String, MutableMap<String, Baffle>>()

        /** 记分板前缀 */
        internal val uniqueChars = (1..50).map { '黑' + it }

        /** 默认记分板内容 */
        internal val defaultContent by resettableLazy {
            conf.getList("default-track.scoreboard.content")!!.filterNotNull().map { TrackScoreboard.Line(it.asList().colored()) }
        }

        /** 默认追踪开始信息 */
        internal val defaultMessage by resettableLazy {
            conf["default-track.message"]?.asList()?.colored() ?: emptyList()
        }

        /** 默认记分板单行长度 */
        internal val defaultLength by resettableLazy {
            conf.getInt("default-track.scoreboard.length")
        }

        /** 判断任务允许被追踪 */
        fun QuestContainer.allowTracked() = when (this) {
            is Template -> track() != null || taskMap.values.any { it.track() != null }
            is Task -> track() != null
            else -> error("out of case")
        }

        /** 获取任务追踪组件 */
        fun QuestContainer.track(): AddonTrack? {
            return when (this) {
                is Template -> addon("track")
                is Task -> addon("track") ?: template.track()
                else -> error("out of case")
            }
        }

        /**
         * 获取玩家当前追踪的任务
         * 在追踪任务时允许借助事件系统修改最终结果，但在获取时不会触发事件
         */
        var PlayerProfile.trackQuest: Template?
            set(value) {
                // 当任务不允许追踪时跳过
                if (value != null && !value.allowTracked()) {
                    warning("Quest(${value.path}) not allowed to tracked.")
                    return
                }
                // 唤起事件供外部调用
                if (PlayerEvents.Track(player, this, value ?: trackQuest, value == null).call()) {
                    if (value != null) {
                        persistentDataContainer["quest.track"] = value.id
                    } else {
                        persistentDataContainer.remove("quest.track")
                    }
                }
            }
            get() = persistentDataContainer["quest.track"]?.let { ChemdahAPI.getQuestTemplate(it.toString()) }

        /**
         * 发送信标追踪器
         * 因为由粒子效果构成，无需托管，发送后不会产生资源占用
         */
        fun Player.sendBeaconTracker(trackAddon: AddonTrack) {
            // 更新周期
            if (trackAddon.beacon.period.hasNext(name)) {
                // 获取追踪中心
                val center = trackAddon.center.getLocation(this) ?: return
                //  启用信标追踪器 && 在相同世界
                if (trackAddon.beacon.enable && center.world != null && center.world!!.name == world.name) {
                    // 是否在播放范围内
                    val distance = center.distance(location)
                    if (distance > trackAddon.beacon.distance) {
                        trackAddon.beacon.display(this, center)
                        saveCounter("${trackAddon.questContainer.path}.landmark", trackAddon.beacon.period)
                    }
                }
            }
        }

        /**
         * 发送导航追踪器
         * 因为由粒子效果构成，无需托管，发送后不会产生资源占用
         */
        fun Player.sendNavigationTracker(trackAddon: AddonTrack) {
            val nav = trackAddon.navigation
            if (nav.pointPeriod.hasNext(name)) {
                // 获取追踪中心
                val center = trackAddon.center.getLocation(this) ?: return
                // 启用信标追踪器 && 相同世界 && 可见范围内
                if (nav.enable && center.world?.name == world.name && center.distance(location) < nav.distance) {
                    when (nav.type) {
                        // 点
                        "POINT" -> {
                            nav.displayPoint(this, center)
                            saveCounter("${trackAddon.questContainer.path}.navigation.point", nav.pointPeriod)
                        }
                        // 箭头
                        "ARROW" -> {
                            nav.displayArrow(this, center)
                            saveCounter("${trackAddon.questContainer.path}.navigation.arrow", nav.arrowPeriod)
                        }
                    }
                }
            }
        }

        /**
         * 发送地标追踪器
         * 由全息构成，因此需要托管，发送后会产生资源占用
         */
        fun Player.updateLandmarkTracker() {
            if (nonChemdahProfileLoaded) {
                return
            }
            // 获取追踪任务
            val trackQuest = chemdahProfile.trackQuest ?: return
            // 未接受任务 -> 指向任务本体
            if (chemdahProfile.getQuestById(trackQuest.id) == null) {
                // 创建追踪器
                updateLandmarkTracker(trackQuest.track() ?: return, trackQuest.path)
            } else {
                // 已接受任务 -> 指向任务所有条目
                trackQuest.taskMap.filter { (_, task) -> task.track() != null }.forEach { (_, task) ->
                    // 条目未完成 && 条目依赖已完成
                    if (!task.isCompleted(chemdahProfile) && task.isQuestDependCompleted(this)) {
                        // 创建追踪器
                        updateLandmarkTracker(task.track()!!, task.path)
                    }
                }
            }
        }

        /**
         * 发送地标追踪器
         * 由全息构成，因此需要托管，发送后会产生资源占用
         */
        fun Player.updateLandmarkTracker(trackAddon: AddonTrack, landmarkId: String) {
            val trackCenter = trackAddon.center.getLocation(this) ?: return
            val hologramMap = landmarkHologramMap.computeIfAbsent(name) { ConcurrentHashMap() }
            // 启用追踪器 && 相同世界
            if (trackAddon.landmark.enable && trackCenter.world?.name == world.name) {
                // 获取任务名称
                val name = trackAddon.name ?: trackAddon.questContainer.displayName()
                // 显示距离
                val distance = trackCenter.distance(location)
                // 显示方向
                val direction = trackCenter.toVector().subtract(location.toVector()).normalize()
                // 获取全息显示坐标
                val pos = if (distance < trackAddon.landmark.distance) trackCenter else location.add(direction.multiply(trackAddon.landmark.distance))
                // 高度修正
                pos.y = pos.y.coerceAtLeast(location.y + 0.5)
                // 创建全息内容
                val content = trackAddon.landmark.content.map { it.replace("name" to name, "distance" to distance.format()) }
                // 如果全息存在 -> 更新全息
                if (hologramMap.containsKey(landmarkId)) {
                    hologramMap[landmarkId]!!.also { holo ->
                        holo.teleport(pos)
                        holo.update(content)
                    }
                } else {
                    // 如果全息不存在 -> 创建全息
                    hologramMap.put(landmarkId, AdyeshachAPI.createHologram(this, pos, content))?.delete()
                }
            } else {
                // 删除全息
                hologramMap.remove(landmarkId)?.delete()
            }
        }

        /**
         * 取消地标效果
         */
        fun Player.removeLandmarkTracker() {
            landmarkHologramMap.remove(name)?.forEach { it.value.delete() }
        }

        /**
         * 发送记分板任务追踪器
         */
        fun Player.sendScoreboardTracker() {
            if (nonChemdahProfileLoaded) {
                return
            }
            // 获取追踪任务
            val quest = chemdahProfile.trackQuest ?: return
            // 获取追踪组建
            val questTrack = quest.track()
            // 是否启用记分板
            if (questTrack?.scoreboard?.enable == true) {
                // 任务描述
                val questDesc = quest.ui()?.description ?: emptyList()
                // 单行描述长度
                val length = questTrack.scoreboard.length
                // 尚未接受任务，显示任务总信息
                val content = if (chemdahProfile.getQuestById(quest.id) == null) {
                    // 格式化
                    questTrack.scoreboard.content.flatMap { line ->
                        // 任务信息
                        if (line.isQuestFormat) {
                            // 格式化任务信息
                            line.content.flatMap { cl ->
                                // 任务描述
                                if (cl.contains("description")) {
                                    // 处理描述
                                    questTrack.formatDesc(this, quest.node, questDesc).splitBy(length).map { cl.replace("description" to it) }
                                } else {
                                    // 任务名称
                                    cl.replace("name" to (questTrack.name ?: quest.displayName())).asList()
                                }
                            }
                        } else {
                            // 其他内容
                            line.content
                        }
                    }
                } else {
                    // 格式化
                    questTrack.scoreboard.content.flatMap { line ->
                        // 任务信息
                        if (line.isQuestFormat) {
                            // 获取条目
                            quest.taskMap.flatMap { (_, task) ->
                                // 获取条目追踪
                                val taskTrack = task.track()
                                // 条目尚未完成
                                if (taskTrack != null && !task.isCompleted(chemdahProfile) && task.isQuestDependCompleted(this) && !task.isOptional()) {
                                    // 格式化条目信息
                                    line.content.flatMap { cl ->
                                        // 条目描述
                                        if (cl.contains("description")) {
                                            // 处理描述
                                            taskTrack.formatDesc(this, quest.node, questDesc).splitBy(length).map { cl.replace("description" to it) }
                                        } else {
                                            // 条目名称
                                            cl.replace("name" to (taskTrack.name ?: task.displayName())).asList()
                                        }
                                    }
                                } else {
                                    emptyList()
                                }
                            }
                        } else {
                            line.content
                        }
                    }
                }
                if (content.size > 2) {
                    sendScoreboard(*content.colored().mapIndexed { index, s -> "§${uniqueChars[index]}$s" }.toTypedArray())
                } else {
                    removeScoreboardTracker(quest)
                }
            }
        }

        /**
         * 删除任务追踪
         * 将任务作为参数的原因是要判断这个任务追踪是否启用了记分板
         */
        fun Player.removeScoreboardTracker(quest: Template?) {
            if (nonChemdahProfileLoaded || quest == null) {
                return
            }
            // 任务本体活任意子条目启用 Scoreboard 追踪
            if (quest.track()?.scoreboard?.enable == true || quest.taskMap.any { it.value.track()?.scoreboard?.enable == true }) {
                sendScoreboard("")
            }
        }

        /**
         * 记录玩家的 Baffle 对象，用于在离线时释放缓存
         */
        internal fun Player.saveCounter(node: String, baffle: Baffle) {
            val map = releaseCounterMap.computeIfAbsent(name) { ConcurrentHashMap() }
            if (!map.containsKey(node)) {
                map[node] = baffle
            }
        }
    }
}