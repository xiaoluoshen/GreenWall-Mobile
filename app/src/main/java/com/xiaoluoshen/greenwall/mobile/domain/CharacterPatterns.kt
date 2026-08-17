package com.xiaoluoshen.greenwall.mobile.domain

object CharacterPatterns {
    val uppercase: Map<String, Array<IntArray>> = mapOf(
        "A" to arrayOf(intArrayOf(0, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 1, 1, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1)),
        "B" to arrayOf(intArrayOf(1, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 1, 1, 0)),
        "C" to arrayOf(intArrayOf(0, 1, 1, 1), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(0, 1, 1, 1)),
        "D" to arrayOf(intArrayOf(1, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 1, 1, 0)),
        "E" to arrayOf(intArrayOf(1, 1, 1, 1), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 1, 1, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 1, 1, 1)),
        "F" to arrayOf(intArrayOf(1, 1, 1, 1), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 1, 1, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0)),
        "G" to arrayOf(intArrayOf(0, 1, 1, 1), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 1, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 1)),
        "H" to arrayOf(intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 1, 1, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1)),
        "I" to arrayOf(intArrayOf(1, 1, 1), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), intArrayOf(1, 1, 1)),
        "J" to arrayOf(intArrayOf(0, 0, 1, 1), intArrayOf(0, 0, 0, 1), intArrayOf(0, 0, 0, 1), intArrayOf(0, 0, 0, 1), intArrayOf(0, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 0)),
        "K" to arrayOf(intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 1, 0), intArrayOf(1, 1, 0, 0), intArrayOf(1, 1, 0, 0), intArrayOf(1, 0, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1)),
        "L" to arrayOf(intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 1, 1, 1)),
        "M" to arrayOf(intArrayOf(1, 0, 0, 0, 1), intArrayOf(1, 1, 0, 1, 1), intArrayOf(1, 0, 1, 0, 1), intArrayOf(1, 0, 0, 0, 1), intArrayOf(1, 0, 0, 0, 1), intArrayOf(1, 0, 0, 0, 1), intArrayOf(1, 0, 0, 0, 1)),
        "N" to arrayOf(intArrayOf(1, 0, 0, 1), intArrayOf(1, 1, 0, 1), intArrayOf(1, 1, 0, 1), intArrayOf(1, 0, 1, 1), intArrayOf(1, 0, 1, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1)),
        "O" to arrayOf(intArrayOf(0, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 0)),
        "P" to arrayOf(intArrayOf(1, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 1, 1, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0)),
        "Q" to arrayOf(intArrayOf(0, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 1, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 1)),
        "R" to arrayOf(intArrayOf(1, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 1, 1, 0), intArrayOf(1, 0, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1)),
        "S" to arrayOf(intArrayOf(0, 1, 1, 1), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(0, 1, 1, 0), intArrayOf(0, 0, 0, 1), intArrayOf(0, 0, 0, 1), intArrayOf(1, 1, 1, 0)),
        "T" to arrayOf(intArrayOf(1, 1, 1, 1, 1), intArrayOf(0, 0, 1, 0, 0), intArrayOf(0, 0, 1, 0, 0), intArrayOf(0, 0, 1, 0, 0), intArrayOf(0, 0, 1, 0, 0), intArrayOf(0, 0, 1, 0, 0), intArrayOf(0, 0, 1, 0, 0)),
        "U" to arrayOf(intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 0)),
        "V" to arrayOf(intArrayOf(1, 0, 0, 0, 1), intArrayOf(1, 0, 0, 0, 1), intArrayOf(1, 0, 0, 0, 1), intArrayOf(0, 1, 0, 1, 0), intArrayOf(0, 1, 0, 1, 0), intArrayOf(0, 0, 1, 0, 0), intArrayOf(0, 0, 1, 0, 0)),
        "W" to arrayOf(intArrayOf(1, 0, 0, 0, 1), intArrayOf(1, 0, 0, 0, 1), intArrayOf(1, 0, 0, 0, 1), intArrayOf(1, 0, 1, 0, 1), intArrayOf(1, 0, 1, 0, 1), intArrayOf(1, 1, 0, 1, 1), intArrayOf(1, 0, 0, 0, 1)),
        "X" to arrayOf(intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 0), intArrayOf(0, 1, 1, 0), intArrayOf(0, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1)),
        "Y" to arrayOf(intArrayOf(1, 0, 0, 0, 1), intArrayOf(0, 1, 0, 1, 0), intArrayOf(0, 0, 1, 0, 0), intArrayOf(0, 0, 1, 0, 0), intArrayOf(0, 0, 1, 0, 0), intArrayOf(0, 0, 1, 0, 0), intArrayOf(0, 0, 1, 0, 0)),
        "Z" to arrayOf(intArrayOf(1, 1, 1, 1), intArrayOf(0, 0, 0, 1), intArrayOf(0, 0, 1, 0), intArrayOf(0, 1, 0, 0), intArrayOf(0, 1, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 1, 1, 1))
    )

    val lowercase: Map<String, Array<IntArray>> = mapOf(
        "a" to arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0), intArrayOf(0, 1, 1, 0), intArrayOf(0, 0, 0, 1), intArrayOf(0, 1, 1, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 1)),
        "b" to arrayOf(intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 1, 1, 0)),
        "c" to arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0), intArrayOf(0, 1, 1, 1), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(0, 1, 1, 1)),
        "d" to arrayOf(intArrayOf(0, 0, 0, 1), intArrayOf(0, 0, 0, 1), intArrayOf(0, 1, 1, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 1)),
        "e" to arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0), intArrayOf(0, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 1, 1, 1), intArrayOf(1, 0, 0, 0), intArrayOf(0, 1, 1, 0)),
        "f" to arrayOf(intArrayOf(0, 0, 1, 1), intArrayOf(0, 1, 0, 0), intArrayOf(1, 1, 1, 0), intArrayOf(0, 1, 0, 0), intArrayOf(0, 1, 0, 0), intArrayOf(0, 1, 0, 0), intArrayOf(0, 1, 0, 0)),
        "g" to arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 1, 1, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 1), intArrayOf(0, 0, 0, 1), intArrayOf(0, 1, 1, 0)),
        "h" to arrayOf(intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1)),
        "i" to arrayOf(intArrayOf(0, 1, 0), intArrayOf(0, 0, 0), intArrayOf(1, 1, 0), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), intArrayOf(1, 1, 1)),
        "j" to arrayOf(intArrayOf(0, 0, 1), intArrayOf(0, 0, 0), intArrayOf(0, 0, 1), intArrayOf(0, 0, 1), intArrayOf(0, 0, 1), intArrayOf(1, 0, 1), intArrayOf(0, 1, 0)),
        "k" to arrayOf(intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 1, 0), intArrayOf(1, 1, 0, 0), intArrayOf(1, 1, 0, 0), intArrayOf(1, 0, 1, 0), intArrayOf(1, 0, 0, 1)),
        "l" to arrayOf(intArrayOf(1, 1, 0), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), intArrayOf(1, 1, 1)),
        "m" to arrayOf(intArrayOf(0, 0, 0, 0, 0), intArrayOf(0, 0, 0, 0, 0), intArrayOf(1, 1, 0, 1, 0), intArrayOf(1, 0, 1, 0, 1), intArrayOf(1, 0, 1, 0, 1), intArrayOf(1, 0, 0, 0, 1), intArrayOf(1, 0, 0, 0, 1)),
        "n" to arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0), intArrayOf(1, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1)),
        "o" to arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0), intArrayOf(0, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 0)),
        "p" to arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0), intArrayOf(1, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 1, 1, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0)),
        "q" to arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0), intArrayOf(0, 1, 1, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 1), intArrayOf(0, 0, 0, 1), intArrayOf(0, 0, 0, 1)),
        "r" to arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0), intArrayOf(1, 0, 1, 1), intArrayOf(1, 1, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0)),
        "s" to arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0), intArrayOf(0, 1, 1, 1), intArrayOf(1, 0, 0, 0), intArrayOf(0, 1, 1, 0), intArrayOf(0, 0, 0, 1), intArrayOf(1, 1, 1, 0)),
        "t" to arrayOf(intArrayOf(0, 1, 0, 0), intArrayOf(0, 1, 0, 0), intArrayOf(1, 1, 1, 0), intArrayOf(0, 1, 0, 0), intArrayOf(0, 1, 0, 0), intArrayOf(0, 1, 0, 0), intArrayOf(0, 0, 1, 1)),
        "u" to arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 1)),
        "v" to arrayOf(intArrayOf(0, 0, 0, 0, 0), intArrayOf(0, 0, 0, 0, 0), intArrayOf(1, 0, 0, 0, 1), intArrayOf(1, 0, 0, 0, 1), intArrayOf(0, 1, 0, 1, 0), intArrayOf(0, 1, 0, 1, 0), intArrayOf(0, 0, 1, 0, 0)),
        "w" to arrayOf(intArrayOf(0, 0, 0, 0, 0), intArrayOf(0, 0, 0, 0, 0), intArrayOf(1, 0, 0, 0, 1), intArrayOf(1, 0, 0, 0, 1), intArrayOf(1, 0, 1, 0, 1), intArrayOf(1, 0, 1, 0, 1), intArrayOf(0, 1, 0, 1, 0)),
        "x" to arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 0), intArrayOf(0, 1, 1, 0), intArrayOf(0, 1, 1, 0), intArrayOf(1, 0, 0, 1)),
        "y" to arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 1), intArrayOf(0, 0, 0, 1), intArrayOf(0, 1, 1, 0)),
        "z" to arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0), intArrayOf(1, 1, 1, 1), intArrayOf(0, 0, 1, 0), intArrayOf(0, 1, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 1, 1, 1))
    )

    val numbers: Map<String, Array<IntArray>> = mapOf(
        "0" to arrayOf(intArrayOf(0, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 0)),
        "1" to arrayOf(intArrayOf(0, 1, 0), intArrayOf(1, 1, 0), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), intArrayOf(1, 1, 1)),
        "2" to arrayOf(intArrayOf(0, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(0, 0, 0, 1), intArrayOf(0, 0, 1, 0), intArrayOf(0, 1, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 1, 1, 1)),
        "3" to arrayOf(intArrayOf(0, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(0, 0, 0, 1), intArrayOf(0, 1, 1, 0), intArrayOf(0, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 0)),
        "4" to arrayOf(intArrayOf(0, 0, 1, 0), intArrayOf(0, 1, 1, 0), intArrayOf(1, 0, 1, 0), intArrayOf(1, 0, 1, 0), intArrayOf(1, 1, 1, 1), intArrayOf(0, 0, 1, 0), intArrayOf(0, 0, 1, 0)),
        "5" to arrayOf(intArrayOf(1, 1, 1, 1), intArrayOf(1, 0, 0, 0), intArrayOf(1, 1, 1, 0), intArrayOf(0, 0, 0, 1), intArrayOf(0, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 0)),
        "6" to arrayOf(intArrayOf(0, 1, 1, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(1, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 0)),
        "7" to arrayOf(intArrayOf(1, 1, 1, 1), intArrayOf(0, 0, 0, 1), intArrayOf(0, 0, 1, 0), intArrayOf(0, 0, 1, 0), intArrayOf(0, 1, 0, 0), intArrayOf(0, 1, 0, 0), intArrayOf(0, 1, 0, 0)),
        "8" to arrayOf(intArrayOf(0, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 0)),
        "9" to arrayOf(intArrayOf(0, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 1, 1), intArrayOf(0, 0, 0, 1), intArrayOf(0, 0, 0, 1), intArrayOf(0, 1, 1, 0))
    )

    val symbols: Map<String, Array<IntArray>> = mapOf(
        "!" to arrayOf(intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), intArrayOf(0, 0, 0), intArrayOf(0, 1, 0)),
        "?" to arrayOf(intArrayOf(0, 1, 1, 0), intArrayOf(1, 0, 0, 1), intArrayOf(0, 0, 0, 1), intArrayOf(0, 0, 1, 0), intArrayOf(0, 1, 0, 0), intArrayOf(0, 0, 0, 0), intArrayOf(0, 1, 0, 0)),
        "#" to arrayOf(intArrayOf(0, 1, 0, 1, 0), intArrayOf(0, 1, 0, 1, 0), intArrayOf(1, 1, 1, 1, 1), intArrayOf(0, 1, 0, 1, 0), intArrayOf(1, 1, 1, 1, 1), intArrayOf(0, 1, 0, 1, 0), intArrayOf(0, 1, 0, 1, 0)),
        "+" to arrayOf(intArrayOf(0, 0, 0, 0, 0), intArrayOf(0, 0, 1, 0, 0), intArrayOf(0, 0, 1, 0, 0), intArrayOf(1, 1, 1, 1, 1), intArrayOf(0, 0, 1, 0, 0), intArrayOf(0, 0, 1, 0, 0), intArrayOf(0, 0, 0, 0, 0)),
        "-" to arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0), intArrayOf(1, 1, 1, 1), intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0)),
        "*" to arrayOf(intArrayOf(0, 0, 0, 0, 0), intArrayOf(0, 0, 1, 0, 0), intArrayOf(1, 0, 1, 0, 1), intArrayOf(0, 1, 1, 1, 0), intArrayOf(1, 0, 1, 0, 1), intArrayOf(0, 0, 1, 0, 0), intArrayOf(0, 0, 0, 0, 0)),
        "<" to arrayOf(intArrayOf(0, 0, 0, 1), intArrayOf(0, 0, 1, 0), intArrayOf(0, 1, 0, 0), intArrayOf(1, 0, 0, 0), intArrayOf(0, 1, 0, 0), intArrayOf(0, 0, 1, 0), intArrayOf(0, 0, 0, 1)),
        ">" to arrayOf(intArrayOf(1, 0, 0, 0), intArrayOf(0, 1, 0, 0), intArrayOf(0, 0, 1, 0), intArrayOf(0, 0, 0, 1), intArrayOf(0, 0, 1, 0), intArrayOf(0, 1, 0, 0), intArrayOf(1, 0, 0, 0))
    )

    val categories: Map<String, Map<String, Array<IntArray>>> = mapOf(
        "uppercase" to uppercase,
        "lowercase" to lowercase,
        "numbers" to numbers,
        "symbols" to symbols
    )
}
