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

class PostsDownloaderHook {

    fun init(classLoader: ClassLoader) {
        try {
            XposedHelpers.findAndHookMethod(
                View::class.java,
                "onAttachedToWindow",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val view = param.thisObject as? View ?: return
                        if (!PrefsHelper.isPostsDownloadEnabled()) return

                        val className = view.javaClass.name
                        if (className.contains("MediaActionsView", ignoreCase = true) ||
                            className.contains("RowFeedMediaAction", ignoreCase = true) ||
                            className.contains("MediaFeedbackView", ignoreCase = true)
                        ) {
                            if (view is ViewGroup) {
                                injectPostDownloadButton(view)
                            }
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            XposedBridge.log("[InstaMod] Failed to hook Posts downloader: ${t.message}")
        }
    }

    private fun injectPostDownloadButton(container: ViewGroup) {
        container.post {
            try {
                if (container.findViewWithTag<View>(TAG_POST_DOWNLOAD) != null) return@post

                val context = container.context
                val iconSize = ViewUtils.dpToPx(context, 24f)
                val margin = ViewUtils.dpToPx(context, 8f)

                val downloadButton = ImageView(context).apply {
                    tag = TAG_POST_DOWNLOAD
                    setImageResource(android.R.drawable.stat_sys_download)
                    layoutParams = LinearLayout.LayoutParams(iconSize, iconSize).apply {
                        leftMargin = margin
                        rightMargin = margin
                    }
                    contentDescription = "Download Post"
                    setOnClickListener {
                        val mediaUrl = resolveActivePostUrl(container)
                        if (!mediaUrl.isNullOrEmpty()) {
                            DownloadHelper.downloadMedia(context, mediaUrl, "Post")
                        } else {
                            DownloadHelper.downloadMedia(
                                context,
                                "https://instagram.com",
                                "Post"
                            )
                        }
                    }
                }

                container.addView(downloadButton)
                XposedBridge.log("[InstaMod] Post download button injected successfully.")
            } catch (t: Throwable) {
                XposedBridge.log("[InstaMod] Error injecting Post button: ${t.message}")
            }
        }
    }

    private fun resolveActivePostUrl(view: View): String? {
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
        private const val TAG_POST_DOWNLOAD = "instamod_post_download_btn"
    }
}
