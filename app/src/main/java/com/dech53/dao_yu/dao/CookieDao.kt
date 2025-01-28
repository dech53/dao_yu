package com.dech53.dao_yu.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.dech53.dao_yu.models.Cookie
import kotlinx.coroutines.flow.Flow

@Dao
interface CookieDao {
    @Query("SELECT * FROM cookie")
    fun getAll(): Flow<List<Cookie>>//auto notify data change

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(cookie: Cookie)

    @Delete
    suspend fun delete(cookie: Cookie)

    @Query("SELECT * FROM cookie WHERE isToVerify = 1")
    suspend fun getHashToVerify(): Cookie

    @Query("UPDATE cookie SET isToVerify = CASE WHEN cookie = :cookieId THEN 1 ELSE 0 END")
    suspend fun setVerifyCookie(cookieId: String)
}
