package com.dech53.dao_yu.dao

import com.dech53.dao_yu.models.Cookie
import com.dech53.dao_yu.models.Favorite
import com.dech53.dao_yu.models.PostAction
import com.dech53.dao_yu.models.ReplyAction

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

    suspend fun insertPost(post: PostAction) {
        db.postDao.insertPost(post)
    }

    fun getFlowOfPosted(userId: String) = db.postDao.getPostsByUserId(userId)

    suspend fun insertReply(reply: ReplyAction){
        db.replyDao.insertReply(reply)
    }

    fun getFlowOfReplied(userId: String) = db.replyDao.getRepliesByUserId(userId)
}