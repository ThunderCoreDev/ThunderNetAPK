package com.thundernet.admin.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_user")
data class AdminUser(
    @PrimaryKey val username: String,
    val passwordHash: String
)