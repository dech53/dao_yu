package com.dech53.dao_yu.dao

import com.dech53.dao_yu.models.Cookie
import com.dech53.dao_yu.models.Favorite

class DataBaseRepository(private val db: CookieDatabase) {
    fun getFlowOfCookie() = db.cookieDao.getAll()

    suspend fun insertCookie(cookie: Cookie) {
        db.cookieDao.insert(cookie)
    }

    suspend fun deleteCookie(cookie: Cookie) {
        db.cookieDao.delete(cookie)
    }

    suspend fun getHashToVerify() = db.cookieDao.getHashToVerify()

    suspend fun setVerifyCookie(cookieId: String) {
        db.cookieDao.setVerifyCookie(cookieId)
    }

    fun getFlowOfFav() = db.favDao.getAll()

    suspend fun insertFav(favorite: Favorite) {
        db.favDao.insert(favorite)
    }

    suspend fun deleteFav(favorite: Favorite) {
        db.favDao.delete(favorite)
    }
}