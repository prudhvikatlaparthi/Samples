package com.pru.printlib.utilities

class UniCode864Mapping {
    var Base: Short = 0
    var Start: Short = 2
    var End: Short = 1
    var Middle: Short = 3
    var RIGHT_JOIN: Short = 2
    var RIGHT_LEFT_JOIN: Short = 4
    var NOT_JOIN: Short = 0
    var ArabicLetterOffset = 1569
    var ARABIC = false
    var ENGLISH = true
    var ArabicLetterPreOrder: Short = 0
    var CurrentLang = ENGLISH
    var AddJointCharacter = false
    var ArabicLetterIndex = arrayOf(
        intArrayOf(193, 193, 198, 198),
        intArrayOf(194, 0, 162, 0),
        intArrayOf(195, 0, 165, 0),
        intArrayOf(196, 0, 196, 0),
        intArrayOf(199, 0, 168, 0),
        intArrayOf(233, 233, 198, 198),
        intArrayOf(199, 0, 168, 0),
        intArrayOf(169, 169, 200, 200),
        intArrayOf(201, 0, 201, 0),
        intArrayOf(170, 170, 202, 202),
        intArrayOf(171, 171, 203, 203),
        intArrayOf(173, 173, 204, 204),
        intArrayOf(174, 174, 205, 205),
        intArrayOf(175, 175, 206, 206),
        intArrayOf(207, 0, 207, 0),
        intArrayOf(208, 0, 208, 0),
        intArrayOf(209, 0, 209, 0),
        intArrayOf(210, 0, 210, 0),
        intArrayOf(188, 188, 211, 211),
        intArrayOf(189, 189, 212, 212),
        intArrayOf(190, 190, 213, 213),
        intArrayOf(235, 235, 214, 214),
        intArrayOf(215, 215, 215, 215),
        intArrayOf(216, 216, 216, 216),
        intArrayOf(223, 197, 217, 236),
        intArrayOf(238, 237, 218, 247),
        intArrayOf(224, 224, 224, 224),
        intArrayOf(186, 186, 225, 225),
        intArrayOf(248, 248, 226, 226),
        intArrayOf(252, 252, 227, 227),
        intArrayOf(251, 251, 228, 228),
        intArrayOf(239, 239, 229, 229),
        intArrayOf(242, 242, 230, 230),
        intArrayOf(243, 243, 231, 244),
        intArrayOf(232, 0, 232, 0),
        intArrayOf(233, 0, 245, 0),
        intArrayOf(253, 246, 234, 234),
        intArrayOf(249, 0, 250, 0),
        intArrayOf(153, 0, 154, 0),
        intArrayOf(157, 0, 158, 0),
        intArrayOf(157, 0, 158, 0)
    )

    private fun getArabicFontLetterIndex(letter: Int): Int {
        return (if (letter > 1599 && letter < 1615) letter - 5 else letter) - ArabicLetterOffset // because there are cut in letter sequance (1594-1601) in windows mobile code page
    }

    private fun ArabicLetterMapping(letter: Int, pos: Int): Int {
        if (letter < 1569 || letter > 1614) // charecter out of the rang
            return 0
        val tmp: Int
        tmp = ArabicLetterIndex[getArabicFontLetterIndex(letter)][pos]
        return tmp
    }

    private fun getArabicLetterForm(LetterIndex: Int): Short {
        if (LetterIndex == 0) // hamza
            return NOT_JOIN else if (ArabicLetterIndex[LetterIndex][End.toInt()] == 0) return RIGHT_JOIN
        return RIGHT_LEFT_JOIN
    }



    private fun getSpaceType(str: String, pos: Int): Boolean {
        val len = str.length
        if (pos < len - 1 && pos > 0) {
            val letterPre = Integer.valueOf(str[pos - 1].code)
            val letterAfr = Integer.valueOf(str[pos + 1].code)
            val tmp = Integer.valueOf(str[pos].code)
            if (tmp == 58) return ENGLISH // For : symbole
            if (letterPre == 32) {
                var i = pos
                while (i < len - 1 && str[i++] == ' ');
                return if (Integer.valueOf(str[i].code) > 128) ARABIC else ENGLISH
            } else if (letterPre < 65 && letterAfr < 65) return CurrentLang else if (letterPre > 128 && letterAfr > 128) // 128 for English letter
                return ARABIC else if (letterPre > 128 && letterAfr < 65) // 65 for space and other symbols
                return ARABIC else if (letterPre > 128 && letterAfr < 128) return ENGLISH else if (letterPre < 128 && letterAfr > 128) return ARABIC
        }
        return CurrentLang
    }
}