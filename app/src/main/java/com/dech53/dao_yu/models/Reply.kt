package com.dech53.dao_yu.models

data class Reply(
    val id: Int,
    val fid: Int?,
    val ReplyCount: Int?,
    val img: String,
    val ext: String,
    val now: String,
    val user_hash: String,
    val name: String,
    val title: String,
    val content: String,
    val sage: Int?,
    val admin: Int,
    val Hide: Int?
)