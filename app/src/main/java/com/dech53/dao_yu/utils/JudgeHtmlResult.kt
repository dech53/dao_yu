package com.dech53.dao_yu.utils

object JudgeHtmlResult {
    val SuccessRegex = Regex("<p class=\"success\">回复成功</p>")
    fun isSuccess(htmlContent: String): Boolean {
        return SuccessRegex.containsMatchIn(htmlContent)
    }
}