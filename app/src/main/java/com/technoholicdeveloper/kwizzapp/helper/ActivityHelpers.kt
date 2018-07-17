package net.rmitsolutions.mfexpert.lms.helpers

import android.app.Activity
import android.content.Context
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.madhuteja.checknet.CheckConnection
import com.technoholicdeveloper.kwizzapp.R
import com.technoholicdeveloper.kwizzapp.viewhelper.ProgressDialog
import com.technoholicdeveloper.kwizzapp.viewhelper.ShowDialog
import timber.log.Timber


/**
 * Created by Madhu on 24-Apr-2018.
 */

val Context.isExternalStorageWritable: Boolean
    get() = isExternalStorageAvail()

fun Activity.finishNoAnim() {
    finish()
    overridePendingTransition(0, 0)
}


val progressDialog = ProgressDialog()

fun Activity.showProgress() {
    progressDialog.showProgressDialog(this)
}

fun Activity.hideProgress() {
    progressDialog.hideProgressDialog()
}




//check network
fun Activity.isNetConnected(showStatus: Boolean = true): Boolean {
    if (CheckConnection.with(this).isConnected) {
//        hideStatus()
        return true
    } else {
        if (showStatus) {
//            showStatus(getString(R.string.you_are_offline))
        }
        return false
    }
}

fun Context.isNetConnected() = CheckConnection.with(this).isConnected

//snack bar
fun Activity.snackBar(view: View, message: String?, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(view, "$message", duration).show()
}

fun Activity.snackBar(view: View, message: String?, duration: Int = Snackbar.LENGTH_INDEFINITE,
                      actionText: String = "Ok", action: () -> Unit) {
    Snackbar.make(view, "$message", duration)
            .setAction(actionText,
                    {
                        action()
                    }
            ).show()
}

// logging extensions
fun Activity.logE(t: Throwable, message: String?) = Timber.e(t, message)

fun Activity.logE(message: String?) = Log.e("KwizzApp",message)

fun Activity.logD(t: Throwable, message: String?) = Timber.d(t, message)

fun Activity.logD(message: String?) = Log.d("KwizzApp",message)

//external storage check
val Activity.isExternalStorageWritable: Boolean
    get() = isExternalStorageAvail()

private fun isExternalStorageAvail() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()

val showDialog = ShowDialog()
fun Activity.showDialog(activity: Activity, title: String, message: String){
    showDialog.dialog(activity, title, message)
}


fun Activity.switchToFragment(newFrag: Fragment) {
    val manager = (this as AppCompatActivity).supportFragmentManager
    if (this.isFinishing){
        manager.beginTransaction().replace(R.id.main_content, newFrag)
                .commit()
    }else{
        manager.beginTransaction().replace(R.id.main_content, newFrag)
                .commit()
    }
}
