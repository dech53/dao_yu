package com.dech53.dao_yu.dao

sealed interface CookieEvent {
    object SaveCookie : CookieEvent
    data class SetCookie(val cookie: String) : CookieEvent
    data class SetIsToVerify(val isToVerify: Int) : CookieEvent
    data class SetName(val name: String) : CookieEvent
    object GetCookies : CookieEvent
    data class DeleteCookie(val cookie: String) : CookieEvent
    object GetHashToVerify : CookieEvent
    data class SetVerifyCookie(val cookie: String) : CookieEvent
}