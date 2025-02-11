package com.dech53.dao_yu.models

data class QuoteRef(
    val id: Long,
    val img: String,
    val ext: String,
    val now: String,
    val user_hash: String,
    val name: String,
    val title: String,
    var content: String,
    val sage: Int,
    val status: String,
    val admin: Int,
    val isThread: Boolean = false
)
fun emptyQuoteRefWithContent(content: String,id: Long): QuoteRef {
    return QuoteRef(
        id = id,
        img = "",
        ext = "",
        now = "",
        user_hash = "",
        name = "",
        title = "",
        content = content,
        sage = 0,
        status = "",
        admin = 0,
    )
}