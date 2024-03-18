package com.softwill.alpha.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog

object DialogUtils {
    fun showAlert(context: Context, title: String, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                // Positive button click action, if needed
                dialog.dismiss()
            }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
