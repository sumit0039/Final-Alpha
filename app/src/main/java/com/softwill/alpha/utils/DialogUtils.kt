package com.softwill.alpha.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.softwill.alpha.R

object DialogUtils {
    fun showAlert(context: Context, title: String, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(context, R.style.CalenderViewCustom,)
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
