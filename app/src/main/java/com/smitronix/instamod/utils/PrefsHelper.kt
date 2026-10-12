package com.smitronix.instamod.utils

import de.robv.android.xposed.XSharedPreferences

object PrefsHelper {
    private const val PACKAGE_NAME = "com.smitronix.instamod"
    private const val PREFS_NAME = "instamod_prefs"

    private var xPrefs: XSharedPreferences? = null

    fun init() {
        try {
            xPrefs = XSharedPreferences(PACKAGE_NAME, PREFS_NAME).apply {
                try { makeWorldReadable() } catch (_: Throwable) {}
                reload()
            }
        } catch (_: Throwable) {
            xPrefs = null
        }
    }

    fun isFloatingNavEnabled(): Boolean {
        xPrefs?.reload()
        return xPrefs?.getBoolean("enable_floating_nav", true) ?: true
    }

    fun isReelsDownloadEnabled(): Boolean {
        xPrefs?.reload()
        return xPrefs?.getBoolean("enable_reels_download", true) ?: true
    }

    fun isStoriesDownloadEnabled(): Boolean {
        xPrefs?.reload()
        return xPrefs?.getBoolean("enable_stories_download", true) ?: true
    }

    fun isPostsDownloadEnabled(): Boolean {
        xPrefs?.reload()
        return xPrefs?.getBoolean("enable_posts_download", true) ?: true
    }
}
