package com.thundenet.admin

import android.app.Application
import com.thundenet.admin.data.db.AppDatabase

class ThundeNetApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.init(this)
    }
}