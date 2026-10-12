package com.smitronix.instamod.hooks

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.smitronix.instamod.utils.PrefsHelper
import com.smitronix.instamod.utils.ViewUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

class FloatingNavHook {

    fun init(classLoader: ClassLoader) {
        try {
            // Hook Activity.onCreate to catch the Instagram main activity
            XposedHelpers.findAndHookMethod(
                "android.app.Activity",
                classLoader,
                "onCreate",
                Bundle::class.java,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val activity = param.thisObject as? Activity ?: return
                        if (activity.javaClass.name.contains("InstagramMainActivity") ||
                            activity.javaClass.name.contains("MainActivity")
                        ) {
                            hookMainBottomNavigation(activity)
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            XposedBridge.log("[InstaMod] Failed to hook FloatingNav: ${t.message}")
        }
    }

    private fun hookMainBottomNavigation(activity: Activity) {
        activity.window.decorView.post {
            try {
                if (!PrefsHelper.isFloatingNavEnabled()) return@post

                val decorView = activity.window.decorView as? ViewGroup ?: return@post
                val navBar = findBottomTabBar(decorView) ?: return@post

                applyFloatingStyle(navBar)
            } catch (t: Throwable) {
                XposedBridge.log("[InstaMod] Error styling floating nav bar: ${t.message}")
            }
        }
    }

    private fun findBottomTabBar(root: ViewGroup): View? {
        // Search for Instagram's TabBar or bottom navigation view by common naming patterns
        for (i in 0 until root.childCount) {
            val child = root.getChildAt(i)
            val name = child.javaClass.name
            if (name.contains("TabBar", ignoreCase = true) ||
                name.contains("BottomNav", ignoreCase = true) ||
                name.contains("TabHost", ignoreCase = true)
            ) {
                return child
            }
            if (child is ViewGroup) {
                val found = findBottomTabBar(child)
                if (found != null) return found
            }
        }
        return null
    }

    private fun applyFloatingStyle(navView: View) {
        val context = navView.context
        val lp = navView.layoutParams as? ViewGroup.MarginLayoutParams ?: return

        // Configure floating margins (left, right, bottom)
        val horizontalMargin = ViewUtils.dpToPx(context, 16f)
        val bottomMargin = ViewUtils.dpToPx(context, 12f)

        lp.setMargins(horizontalMargin, 0, horizontalMargin, bottomMargin)
        navView.layoutParams = lp

        // Apply floating pill background & elevation
        navView.background = ViewUtils.createFloatingPillDrawable(context, radiusDp = 24f)
        navView.elevation = ViewUtils.dpToPx(context, 8f).toFloat()
        navView.clipToOutline = true

        XposedBridge.log("[InstaMod] Floating Navigation Bar applied successfully.")
    }
}
