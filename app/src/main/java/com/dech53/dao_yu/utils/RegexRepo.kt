package com.dech53.dao_yu.utils

object RegexRepo {
    val dateRegex = Regex(pattern = "[^(]*|(?<=\\))[^)]*")
    val replace_ = Regex(pattern = "-")
    val time_ = Regex("""\d{2}:\d{2}:\d{2}$""")
}