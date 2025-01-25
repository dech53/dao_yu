package com.dech53.dao_yu.static

import androidx.compose.runtime.State
import com.dech53.dao_yu.models.Cookie

data class CookieState(
    val cookies: List<Cookie> = emptyList(),
    val isToVerify: Int = 1,
    val name: String = "",
    val cookie: String = "",
)
