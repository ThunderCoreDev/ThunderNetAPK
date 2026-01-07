package com.thundernet.admin

import android.app.Application
import com.thundernet.admin.data.db.AppDatabase

class ThunderNetApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.init(this)
    }
}