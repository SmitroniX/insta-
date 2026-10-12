package com.smitronix.instamod.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup

object ViewUtils {

    fun dpToPx(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun createFloatingPillDrawable(context: Context, bgColor: Int = Color.parseColor("#1C1C1E"), radiusDp: Float = 26f): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dpToPx(context, radiusDp).toFloat()
            setColor(bgColor)
            setStroke(dpToPx(context, 1f), Color.parseColor("#33FFFFFF"))
        }
    }

    /**
     * Traverses a view hierarchy to find a view whose class name matches the given substring.
     */
    fun findViewByClassName(root: View, classSubstring: String): View? {
        if (root.javaClass.name.contains(classSubstring, ignoreCase = true)) {
            return root
        }
        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                val child = root.getChildAt(i)
                val found = findViewByClassName(child, classSubstring)
                if (found != null) return found
            }
        }
        return null
    }
}
