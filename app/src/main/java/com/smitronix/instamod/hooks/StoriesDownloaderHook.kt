package com.smitronix.instamod.hooks

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.smitronix.instamod.utils.DownloadHelper
import com.smitronix.instamod.utils.PrefsHelper
import com.smitronix.instamod.utils.ViewUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

class StoriesDownloaderHook {

    fun init(classLoader: ClassLoader) {
        try {
            XposedHelpers.findAndHookMethod(
                View::class.java,
                "onAttachedToWindow",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val view = param.thisObject as? View ?: return
                        if (!PrefsHelper.isStoriesDownloadEnabled()) return

                        val className = view.javaClass.name
                        if (className.contains("ReelViewerHeader", ignoreCase = true) ||
                            className.contains("StoryHeader", ignoreCase = true) ||
                            className.contains("ReelViewerFragment", ignoreCase = true)
                        ) {
                            if (view is ViewGroup) {
                                injectStoryDownloadButton(view)
                            }
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            XposedBridge.log("[InstaMod] Failed to hook Stories downloader: ${t.message}")
        }
    }

    private fun injectStoryDownloadButton(headerContainer: ViewGroup) {
        headerContainer.post {
            try {
                if (headerContainer.findViewWithTag<View>(TAG_STORY_DOWNLOAD) != null) return@post

                val context = headerContainer.context
                val iconSize = ViewUtils.dpToPx(context, 26f)
                val margin = ViewUtils.dpToPx(context, 6f)

                val downloadButton = ImageView(context).apply {
                    tag = TAG_STORY_DOWNLOAD
                    setImageResource(android.R.drawable.stat_sys_download)
                    layoutParams = LinearLayout.LayoutParams(iconSize, iconSize).apply {
                        rightMargin = margin
                        leftMargin = margin
                    }
                    contentDescription = "Download Story"
                    setOnClickListener {
                        val mediaUrl = resolveActiveStoryUrl(headerContainer)
                        if (!mediaUrl.isNullOrEmpty()) {
                            DownloadHelper.downloadMedia(context, mediaUrl, "Story")
                        } else {
                            DownloadHelper.downloadMedia(
                                context,
                                "https://instagram.com",
                                "Story"
                            )
                        }
                    }
                }

                headerContainer.addView(downloadButton, 0)
                XposedBridge.log("[InstaMod] Story download button injected successfully.")
            } catch (t: Throwable) {
                XposedBridge.log("[InstaMod] Error injecting Story button: ${t.message}")
            }
        }
    }

    private fun resolveActiveStoryUrl(view: View): String? {
        try {
            val tag = view.tag
            if (tag != null && tag is String && tag.startsWith("http")) {
                return tag
            }
            var parent = view.parent
            while (parent is View) {
                val pTag = parent.tag
                if (pTag != null && pTag is String && pTag.startsWith("http")) {
                    return pTag
                }
                parent = parent.parent
            }
        } catch (e: Exception) {
            // Handled gracefully
        }
        return null
    }

    companion object {
        private const val TAG_STORY_DOWNLOAD = "instamod_story_download_btn"
    }
}
