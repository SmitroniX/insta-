package com.smitronix.instamod.hooks

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.smitronix.instamod.R
import com.smitronix.instamod.utils.DownloadHelper
import com.smitronix.instamod.utils.PrefsHelper
import com.smitronix.instamod.utils.ViewUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

class ReelsDownloaderHook {

    fun init(classLoader: ClassLoader) {
        try {
            // Hook View.onAttachedToWindow to intercept Reels vertical action bars
            XposedHelpers.findAndHookMethod(
                View::class.java,
                "onAttachedToWindow",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val view = param.thisObject as? View ?: return
                        if (!PrefsHelper.isReelsDownloadEnabled()) return

                        val className = view.javaClass.name
                        if (className.contains("ClipsViewerAction", ignoreCase = true) ||
                            className.contains("ClipsInteractionBar", ignoreCase = true) ||
                            className.contains("ReelViewerAction", ignoreCase = true)
                        ) {
                            if (view is ViewGroup) {
                                injectDownloadButton(view)
                            }
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            XposedBridge.log("[InstaMod] Failed to hook Reels downloader: ${t.message}")
        }
    }

    private fun injectDownloadButton(actionBar: ViewGroup) {
        actionBar.post {
            try {
                // Prevent duplicate buttons when views are recycled
                if (actionBar.findViewWithTag<View>(TAG_REEL_DOWNLOAD) != null) return@post

                val context = actionBar.context
                val iconSize = ViewUtils.dpToPx(context, 28f)
                val margin = ViewUtils.dpToPx(context, 8f)

                val downloadButton = ImageView(context).apply {
                    tag = TAG_REEL_DOWNLOAD
                    setImageResource(android.R.drawable.stat_sys_download)
                    layoutParams = LinearLayout.LayoutParams(iconSize, iconSize).apply {
                        topMargin = margin
                        bottomMargin = margin
                    }
                    contentDescription = "Download Reel"
                    setOnClickListener {
                        val mediaUrl = resolveActiveReelUrl(actionBar)
                        if (!mediaUrl.isNullOrEmpty()) {
                            DownloadHelper.downloadMedia(context, mediaUrl, "Reel")
                        } else {
                            DownloadHelper.downloadMedia(
                                context,
                                "https://instagram.com", // Fallback notification
                                "Reel"
                            )
                        }
                    }
                }

                actionBar.addView(downloadButton)
                XposedBridge.log("[InstaMod] Reels download button injected successfully.")
            } catch (t: Throwable) {
                XposedBridge.log("[InstaMod] Error injecting Reels button: ${t.message}")
            }
        }
    }

    private fun resolveActiveReelUrl(view: View): String? {
        try {
            // Traverse view tags or parent clip models for video URL strings
            val tag = view.tag
            if (tag != null && tag is String && tag.startsWith("http")) {
                return tag
            }
            // Inspect parent view tags recursively
            var parent = view.parent
            while (parent is View) {
                val pTag = parent.tag
                if (pTag != null && pTag is String && pTag.startsWith("http")) {
                    return pTag
                }
                parent = parent.parent
            }
        } catch (e: Exception) {
            // Fallback handled gracefully
        }
        return null
    }

    companion object {
        private const val TAG_REEL_DOWNLOAD = "instamod_reels_download_btn"
    }
}
