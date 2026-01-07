package com.thundernet.admin.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AdminUser::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun adminUserDao(): AdminUserDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun init(context: Context) {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "thundenet.db"
                    ).build()
                }
            }
        }

        fun get(): AppDatabase = INSTANCE
            ?: throw IllegalStateException("AppDatabase not initialized")
    }
}