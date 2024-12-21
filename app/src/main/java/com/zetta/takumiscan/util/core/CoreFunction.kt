package com.zetta.takumiscan.util.core

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.TypedValue

object CoreFunction {
    fun Context.dpToPx(dp: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    ).toInt()

    fun Context.showDialog(
        title: String,
        message: String,
        positiveButtonText: String,
        onPositiveButtonClick: DialogInterface.OnClickListener,
        negativeButtonText: String? = null,
        onNegativeButtonClick: DialogInterface.OnClickListener? = null,
        cancellable: Boolean = true
    ){
        val dialogBuilder = AlertDialog.Builder(this)
            .setCancelable(cancellable)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText, onPositiveButtonClick)

        negativeButtonText?.let { text ->
            dialogBuilder.setNegativeButton(text, onNegativeButtonClick)
        }

        dialogBuilder.show()
    }
}