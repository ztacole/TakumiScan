package com.zetta.takumiscan.util.core

import android.content.Context
import android.util.TypedValue

object CoreFunction {
    fun Context.dpToPx(dp: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    ).toInt()
}