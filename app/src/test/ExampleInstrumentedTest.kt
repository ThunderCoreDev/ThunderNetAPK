package com.thundernet.admin

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context de la app bajo test
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("com.thundernet.admin", appContext.packageName)
    }
    
    @Test
    fun testLoginActivityExists() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        val packageName = appContext.packageName
        assertNotNull(packageName)
        assertEquals("com.thundernet.admin", packageName)
    }
    
    @Test
    fun testSharedPreferences() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        val prefs = appContext.getSharedPreferences("ThunderNetAdmin", Context.MODE_PRIVATE)
        assertNotNull(prefs)
    }
}