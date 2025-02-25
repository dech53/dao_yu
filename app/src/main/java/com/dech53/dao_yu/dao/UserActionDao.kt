package com.dech53.dao_yu.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dech53.dao_yu.models.PostAction
import com.dech53.dao_yu.models.ReplyAction
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Insert
    suspend fun insertPost(post: PostAction)

    @Query("SELECT * FROM postaction WHERE userId = :userId")
    fun getPostsByUserId(userId: String): Flow<List<PostAction>>
}


@Dao
interface ReplyDao {
    @Insert
    suspend fun insertReply(reply: ReplyAction)

    @Query("SELECT * FROM replyaction WHERE userId = :userId")
    fun getRepliesByUserId(userId: String): Flow<List<ReplyAction>>
}