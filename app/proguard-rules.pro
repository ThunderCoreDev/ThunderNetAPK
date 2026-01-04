# Keep - Library class keep which is only accessed by dynamic feature
-keep class com.thundernet.admin.** { *; }

# Keep - WebView JavaScript Interface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep - MySQL Driver
-keep class com.mysql.** { *; }

# Keep - JSON classes
-keep class org.json.** { *; }

# Keep - Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }

# Don't warn about SQL classes
-dontwarn java.sql.**
-dontwarn javax.sql.**