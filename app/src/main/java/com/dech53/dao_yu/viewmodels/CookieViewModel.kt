package com.dech53.dao_yu.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dech53.dao_yu.dao.CookieDao
import com.dech53.dao_yu.dao.CookieEvent
import com.dech53.dao_yu.models.Cookie
import com.dech53.dao_yu.static.CookieState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get


class CookieViewModel(
    private val cookieDao: CookieDao = Injekt.get()
) : ViewModel() {
    val state = MutableStateFlow(CookieState())

    fun onEvent(event: CookieEvent) {
        when (event) {
            is CookieEvent.SaveCookie -> {
                val cookie = state.value.cookie
                val name = state.value.name
                val isToVerify = state.value.isToVerify
                if (cookie.isBlank() || name.isBlank()) {
                    return
                }
                val newCookie = Cookie(
                    cookie = cookie,
                    name = name,
                    isToVerify = isToVerify
                )
                viewModelScope.launch {
                    cookieDao.insert(newCookie)
                }
            }

            CookieEvent.GetCookies -> {
                viewModelScope.launch {
                    cookieDao.getAll().collect { cookies ->
                        state.update {
                            it.copy(
                                cookies = cookies
                            )
                        }
                    }
                }
            }

            is CookieEvent.SetCookie -> {
                state.update {
                    it.copy(
                        cookie = event.cookie
                    )
                }
            }

            is CookieEvent.SetName -> {
                state.update {
                    it.copy(
                        name = event.name
                    )
                }
            }

            is CookieEvent.DeleteCookie -> {
                viewModelScope.launch {
                    cookieDao.delete(Cookie(0, event.cookie, ""))
                }
            }

            CookieEvent.GetHashToVerify -> {
                viewModelScope.launch {
                    val cookie = cookieDao.getHashToVerify()
                    state.update {
                        it.copy(
                            isToVerify = cookie.isToVerify,
                            cookie = cookie.cookie,
                            name = cookie.name
                        )
                    }
                }
            }

            is CookieEvent.SetIsToVerify -> {
                state.update {
                    it.copy(
                        isToVerify = event.isToVerify
                    )
                }
            }

            is CookieEvent.SetVerifyCookie -> {
                viewModelScope.launch {
                    cookieDao.setVerifyCookie(event.cookie)
                }
            }
        }
    }
}

