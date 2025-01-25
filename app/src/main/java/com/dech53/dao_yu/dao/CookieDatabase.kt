package com.dech53.dao_yu.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dech53.dao_yu.models.Cookie


@Database(entities = [Cookie::class], version = 1, exportSchema = false)
abstract class CookieDatabase : RoomDatabase() {
    abstract val cookieDao: CookieDao


    companion object {
        @Volatile
        private var INSTANCE: CookieDatabase? = null
        fun getDatabase(context: Context): CookieDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CookieDatabase::class.java,
                    "cookie.db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}