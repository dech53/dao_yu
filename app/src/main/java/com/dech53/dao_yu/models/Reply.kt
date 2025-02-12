package com.dech53.dao_yu.models

import androidx.compose.runtime.Stable


@Stable
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
){
    fun toQuoteRef(): QuoteRef {
        return QuoteRef(
            id = this.id.toLong(),
            img = this.img,
            ext = this.ext,
            now = this.now,
            user_hash = this.user_hash,
            name = this.name,
            title = this.title,
            content = this.content,
            sage = this.sage ?: 0,
            status = "",
            admin = this.admin,
            isThread = false
        )
    }
}