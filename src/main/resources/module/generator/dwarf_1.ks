
# AnyTextEditor - Free Online Text Editor © 2022 AnyTextEditor
# Edit your texts for free online, improve them and create new ones

# https://anytexteditor.com

set nm4 to array [ "A" "Ara" "Alfo" "Bari" "Be" "Bo" "Bha" "Bu" "Ba" "Bra" "Bro" "Brou" "Bru" "Da" "Dalo" "Dare" "De" "Dhu" "Dho" "Do" "Dora" "Dwo" "Dou" "Duri" "Du" "El" "Eri" "Fi" "Fo" "Fo" "Ga" "Gi" "Gla" "Glori" "Go" "Gra" "Gro" "Groo" "Gru" "Grou" "Ha" "Ha" "He" "He" "Ho" "Hou" "Hu" "Ja" "Jo" "Ka" "Khe" "Khu" "Khou" "Ko" "Ku" "Ki" "Kra" "Kro" "Lo" "Lu" "Lo" "Ma" "Mo" "Mu" "Na" "No" "Nu" "Nora" "Nura" "Ne" "No" "O" "Ori" "Rei" "Ra" "Ru" "Sa" "Si" "Sna" "Sko" "Ska" "Stro" "The" "Thi" "Tho" "Thra" "Tha" "Tore" "Tha" "Thra" "Thro" "Thu" "Tu" "U" "Umi" "Va" "Vo" "Whu" "We" "Wera" "Yu" "Yo" "Ya" ]
set nm5 to array [ "b" "br" "dd" "d" "dr" "dm" "dgr" "dw" "f" "fr" "gr" "gg" "gh" "gn" "k" "kh" "kgr" "kdr" "kk" "kw" "kh" "kr" "l" "lg" "lgr" "ldr" "lm" "md" "mw" "mn" "m" *"mm" "mr" "n" "nd" "ndr" "nw" "ngr" "nm" "r" "rr" "rgr" "rdr" "rb" "rg" "rn" "rh" "rd" "rm" "rs" "rf" "s" "ss" "sdr" "sgr" "st" "str" "t" "tr" "tm" "th" "tdr" "tgr" "v" "vr" "w" *"z" "zm" "zn" "zz" ]
set nm6 to array [ "abelle" "aebelle" "ebelle" "ibelle" "obelle" "ubelle" "alyn" "aelyn" "elyn" "ealyn" "ilyn" "olyn" "oulyn" "ulyn" "uilyn" "alynn" "aelynn" "elynn" "ealynn" "ilynn" "olynn" "oulynn" "ulynn" "uilynn" "abelyn" "aebelyn" "ebelyn" "eabelyn" "ibelyn" "obelyn" "oubelyn" "ubelyn" "uibelyn" "abelynn" "aebelynn" "ebelynn" "eabelynn" "ibelynn" "obelynn" "oubelynn" "ubelynn" "uibelyn" "anelyn" "aenelyn" "enelyn" "eanelyn" "inelyn" "onelyn" "ounelyn" "unelyn" "uinelyn" "anelynn" "aenelynn" "enelynn" "eanelynn" "inelynn" "onelynn" "ounelynn" "unelynn" "uinelynn" "agit" "aegit" "egit" "eagit" "igit" "ogit" "ugit" "uigit" "agith" "aegith" "egith" "eagith" "igith" "ogith" "ugith" "uigith" "irgit" "irgith" "uirgit" "uirgith" "airgit" "airgith" "arika" "aerika" "erika" "earika" "irika" "orika" "urika" "atain" "aetain" "etain" "eatain" "itain" "otain" "utain" "ataine" "aetaine" "etaine" "eataine" "itaine" "otaine" "utaine" "ahilda" "aehilda" "ehilda" "eahilda" "ohilda" "ihilda" "uhilda" "ahulda" "aehulda" "ehulda" "eahulda" "ohulda" "ihulda" "uhulda" "agar" "aegar" "egar" "eagar" "igar" "ogar" "ugar" "agaer" "egaer" "igaer" "ogaer" "ugaer" "atrud" "aetrud" "etrud" "eatrud" "itrud" "otrud" "utrud" "atrude" "aetrude" "etrude" "eatrude" "itrude" "otrude" "utrude" "ada" "aeda" "eda" "eada" "ida" "oda" "uda" "alda" "aelda" "elda" "ealda" "ilda" "olda" "oulda" "ulda" "alin" "aelin" "elin" "ealin" "ilin" "olin" "oulin" "ulin" "aline" "aeline" "eline" "ealine" "iline" "oline" "ouline" "uline" "atalin" "aetalin" "etalin" "eatalin" "italin" "otalin" "outalin" "utalin" "atalyn" "aetalyn" "etalyn" "eatalyn" "italyn" "otalyn" "outalyn" "utalyn" "atelin" "aetelin" "etelin" "eatelin" "itelin" "otelin" "outelin" "utelin" "atelyn" "aetelyn" "etelyn" "eatelyn" "itelyn" "otelyn" "outelyn" "utelyn" "angrid" "aengrid" "engrid" "eangrid" "ingrid" "ongrid" "oungrid" "ungrid" "ani" "aeni" "eni" "eani" "ini" "oni" "ouni" "uni" "ana" "aena" "ena" "eana" "ina" "ona" "ouna" "una" "alsia" "aelsia" "elsia" "ealsia" "ilsia" "olsia" "oulsia" "ulsia" "ala" "aela" "ela" "eala" "ila" "ola" "oula" "ula" "abella" "aebella" "ebella" "eabella" "ibella" "obella" "oubella" "ubella" "abela" "aebela" "ebela" "eabela" "ibela" "obela" "oubela" "ubela" "astr" "aestr" "estr" "eastr" "istr" "ostr" "oustr" "ustr" "abo" "aebo" "ebo" "eabo" "ibo" "obo" "oubo" "ubo" "abena" "aebena" "ebena" "eabena" "ibena" "obena" "oubena" "ubena" "abera" "aebera" "ebera" "eabera" "ibera" "obera" "oubera" "ubera" "adeth" "aedeth" "edeth" "eadeth" "ideth" "odeth" "oudeth" "udeth" "adrid" "aedrid" "edrid" "eadrid" "idrid" "odrid" "oudrid" "udrid" "abyrn" "aebyrn" "ebyrn" "eabyrn" "ibyrn" "obyrn" "oubyrn" "ubyrn" "agrett" "aegrett" "egrett" "eagrett" "igrett" "ogrett" "ougrett" "ugrett" "agret" "aegret" "egret" "eagret" "igret" "ogret" "ougret" "ugret" "asli" "aesli" "esli" "easli" "isli" "osli" "ousli" "usli" "ahilda" "aehilda" "ehilda" "eahilda" "ihilda" "ohilda" "ouhilda" "uhilda" "ahilde" "aehilde" "ehilde" "eahilde" "ihilde" "ohilde" "ouhilde" "uhilde" "aginn" "aeginn" "eginn" "eaginn" "iginn" "oginn" "ouginn" "uginn" "amora" "aemora" "emora" "eamora" "imora" "omora" "oumora" "umora" "alydd" "aelydd" "elydd" "ealydd" "ilydd" "olydd" "oulydd" "ulydd" "akara" "aekara" "ekara" "eakara" "ikara" "okara" "oukara" "ukara" "aren" "aeren" "eren" "earen" "iren" "oren" "ouren" "uren" "arra" "aerra" "erra" "earra" "irra" "orra" "ourra" "urra" "are" "aere" "ere" "eare" "ire" "ore" "oure" "ure" "awynn" "aewynn" "ewynn" "eawynn" "iwynn" "owynn" "ouwynn" "uwynn" "atryd" "aetryd" "etryd" "eatryd" "itryd" "otryd" "outryd" "utryd" "athra" "aethra" "ethra" "eathra" "ithra" "othra" "outhra" "uthra" "aserd" "aeserd" "eserd" "easerd" "iserd" "oserd" "ouserd" "userd" "tryd" ]
set nm7 to array [ "Ale" "Amber" "Anvil" "Ash" "Axe" "Barbed" "Barrel" "Battle" "Beast" "Bone" "Beryl" "Bitter" "Black" "Blazing" "Blessed" "Blood" "Blunt" "Bone" "Bottle" "Boulder" "Brew" "Brick" "Bright" "Bristle" "Broad" "Bronze" "Brown" "Cave" "Cask" "Chain" "Crag" "Chaos" "Coal" "Coin" "Copper" "Dark" "Deep" "Dim" "Dragon" "Drake" "Dusk" "Earth" "Ember" "Fiery" "Flint" "Flask" "Flint" "Flat" "Forge" "Frost" "Giant" "Gold" "Golden" "Granite" "Gravel" "Gray" "Great" "Grey" "Grim" "Grumble" "Hammer" "Hard" "Heavy" "Hill" "Honor" "Horn" "Ice" "Ingot" "Iron" "Jade" "Keg" "Kobold" "Krag" "Lead" "Large" "Lava" "Leather" "Light" "Long" "Marble" "Magma" "Merry" "Metal" "Mithril" "Mine" "Mountain" "Mud" "Night" "Noble" "Oak" "Oaken" "Onyx" "Opal" "Ore" "Orc" "Plate" "Pebble" "Red" "Rune" "Ruby" "Sapphire" "Shadow" "Shatter" "Smelt" "Silver" "Snow" "Steel" "Storm" "Strong" "Troll" "Thunder" "Twilight" "Treasure" "Under" "War" "Warm" "Whit" "Wind" "Wold" "Wraith" "Wyvern" ]
set nm8 to array [ "arm" "armour" "axe" "back" "bane" "beard" "basher" "belly" "belt" "bender" "blade" "born" "bow" "braid" "braids" "branch" "brand" "breaker" "brew" "brewer" "bringer" "brow" "buckle" "buster" "chest" "chin" "cloak" "coat" "delver" "digger" "foot" "fall" "fury" "finger" "flayer" "feet" "forge" "forged" "grog" "grip" "guard" "gut" "granite" "hand" "head" "heart" "helm" "hide" "hood" "horn" "jaw" "mace" "mail" "maker" "mantle" "mane" "master" "maul" "miner" "pike" "rock" "river" "shield" "shaper" "sword" "shoulder" "stone" "spine" "sunder" "thane" "toe" "tank" "view" ]

join [ random &nm4 random &nm5 random &nm6 " " random &nm7 random &nm8 ] by pass