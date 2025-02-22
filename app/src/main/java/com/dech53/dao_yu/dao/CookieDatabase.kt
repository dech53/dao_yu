package com.dech53.dao_yu.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dech53.dao_yu.models.Cookie
import com.dech53.dao_yu.models.Favorite


@Database(entities = [Cookie::class,Favorite::class], version = 1, exportSchema = false)
abstract class CookieDatabase : RoomDatabase() {
    abstract val cookieDao: CookieDao
    abstract val favDao: FavoriteDao
}