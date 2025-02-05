package com.dech53.dao_yu.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dech53.dao_yu.models.Favorite


@Database(entities = [Favorite::class], version = 1, exportSchema = false)
abstract class FavoriteDataBase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao


    companion object {
        @Volatile
        private var INSTANCE: FavoriteDataBase? = null
        fun getDatabase(context: Context): FavoriteDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteDataBase::class.java,
                    "favorite.db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}