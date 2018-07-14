package com.technoholicdeveloper.kwizzapp.viewhelper

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.technoholicdeveloper.kwizzapp.R

class ShowDialog : View.OnClickListener {

    private lateinit var activity: Activity
    private lateinit var dialog: Dialog

    fun dialog(activity: Activity,title : String, message : String){
        this.activity = activity
        dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_layout)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val titleTextView =dialog.findViewById<TextView>(R.id.textViewTitle)
        val messageTextView = dialog.findViewById<TextView>(R.id.textViewMessage)

        titleTextView.text = title
        messageTextView.text = message

        val okay = dialog.findViewById<Button>(R.id.buttonOk)

        okay.setOnClickListener(this)

        dialog.show()
        dialog.window.setLayout(600,400)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.buttonOk -> {
                dialog.dismiss()
            }
        }
    }
}