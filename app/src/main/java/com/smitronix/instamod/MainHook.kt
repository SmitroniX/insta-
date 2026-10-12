package com.smitronix.instamod

import com.smitronix.instamod.hooks.FloatingNavHook
import com.smitronix.instamod.hooks.PostsDownloaderHook
import com.smitronix.instamod.hooks.ReelsDownloaderHook
import com.smitronix.instamod.hooks.StoriesDownloaderHook
import com.smitronix.instamod.utils.PrefsHelper
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MainHook : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != TARGET_PACKAGE) return

        XposedBridge.log("[InstaMod] Target process detected: ${lpparam.packageName}")

        try {
            // Initialize XSharedPreferences
            PrefsHelper.init()

            val classLoader = lpparam.classLoader

            // 1. Floating Bottom Navigation Bar
            FloatingNavHook().init(classLoader)

            // 2. Reels Media Downloader
            ReelsDownloaderHook().init(classLoader)

            // 3. Stories Media Downloader
            StoriesDownloaderHook().init(classLoader)

            // 4. Feed & Carousel Posts Downloader
            PostsDownloaderHook().init(classLoader)

            XposedBridge.log("[InstaMod] All hooks initialized successfully for Instagram.")
        } catch (t: Throwable) {
            XposedBridge.log("[InstaMod] Failed to initialize hooks: ${t.message}")
        }
    }

    companion object {
        const val TARGET_PACKAGE = "com.instagram.android"
    }
}
