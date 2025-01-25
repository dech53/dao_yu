package com.dech53.dao_yu.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cookie(
    val isToVerify: Int = 1,
    @PrimaryKey
    @ColumnInfo val cookie: String,
    @ColumnInfo(name = "usr_hash") val name: String
)
