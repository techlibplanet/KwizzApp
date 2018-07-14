package net.rmitsolutions.mfexpert.lms.helpers

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.technoholicdeveloper.kwizzapp.R
import org.jetbrains.anko.toast
import timber.log.Timber

/**
 * Created by Madhu on 24-Apr-2018.
 */



fun Fragment.toast(message: String?) = activity!!.toast("$message")

fun Fragment.showProgress() = activity!!.showProgress()

fun Fragment.hideProgress() {
    activity!!.hideProgress()
}

//fun Fragment.showStatus(message: String? = null) = activity!!.showStatus(message)

//fun Fragment.hideStatus() = activity!!.hideStatus()

//check network
fun Fragment.isNetConnected(showStatus: Boolean = true) = activity!!.isNetConnected(showStatus)

//logging extensions

fun Fragment.logE(t: Throwable, message: String?) = Timber.e(t, message)

fun Fragment.logE(message: String?) = Timber.e(message)

fun Fragment.logD(t: Throwable, message: String?) = Timber.d(t, message)

fun Fragment.logD(message: String?) = Timber.d(message)

fun Fragment.showDialog(activity: Activity, title: String, message: String){
    activity.showDialog(activity, title, message)
}

fun Fragment.switchToFragment(activity: Activity,newFrag: Fragment) {
    val manager = (activity as FragmentActivity).supportFragmentManager
    if (activity.isFinishing){
        manager.beginTransaction().replace(R.id.main_content, newFrag)
                .commit()
    }else{
        manager.beginTransaction().replace(R.id.main_content, newFrag)
                .commit()
    }
}