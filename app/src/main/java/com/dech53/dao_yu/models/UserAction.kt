package com.dech53.dao_yu.models

import androidx.compose.runtime.Stable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
@Stable
data class PostAction(
    @PrimaryKey(autoGenerate = true) val id: Int?=null,
    val userId: String,
    val timestamp: Long,
    val content: String? = null
)

@Entity
@Stable
data class ReplyAction(
    @PrimaryKey(autoGenerate = true) val id: Int?=null,
    val userId: String,
    val postId: String,
    val timestamp: Long,
    val content: String? = null
)