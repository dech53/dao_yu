package com.dech53.dao_yu.models

data class Thread(
    val id: Int,
    val fid: Int,
    val ReplyCount: Int,
    val img: String?,
    val ext: String?,
    val now: String,
    val user_hash: String,
    val name: String,
    val title: String,
    val content: String,
    val sage: Int,
    val admin: Int,
    val Hide: Int,
    val Replies: List<Reply>,
    val RemainReplies: Int?
)

fun Thread.toReply(): Reply {
    return Reply(
        id = this.id,
        fid = this.fid,
        ReplyCount = this.ReplyCount,
        img = this.img ?: "",
        ext = this.ext ?: "",
        now = this.now,
        user_hash = this.user_hash,
        name = this.name,
        title = this.title,
        content = this.content,
        sage = this.sage,
        admin = this.admin,
        Hide = this.Hide
    )
}