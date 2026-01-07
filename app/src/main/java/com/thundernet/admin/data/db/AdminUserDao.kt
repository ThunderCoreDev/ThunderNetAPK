package com.thundernet.admin.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AdminUserDao {
    @Query("SELECT * FROM admin_user WHERE username = :username LIMIT 1")
    suspend fun get(username: String): AdminUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: AdminUser)

    @Query("DELETE FROM admin_user")
    suspend fun clearAll()
}