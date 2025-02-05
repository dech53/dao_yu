package com.dech53.dao_yu.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Favorite(
    @PrimaryKey val id: String,
    val content: String,
    val img: String,
)
