package com.technoholicdeveloper.kwizzapp

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import org.jetbrains.anko.find
import timber.log.Timber

open class BaseActivity: AppCompatActivity() {

    companion object {
        private var backPressedTime: Long = 0
    }
    private var mainContent: View? = null
    private val mainContentFadeOutDuration = 100
    private val mainContentFadeInDuration = 200
    private val navDrawerLaunchDelay = 250

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mainContent = findViewById(R.id.main_content)
        if (mainContent != null) {
            mainContent!!.alpha = 0f
            mainContent!!.animate().alpha(1f).duration = mainContentFadeInDuration.toLong()
        } else {
            Timber.d("No view with ID main_content to fade in.")
        }
    }

    override fun onBackPressed() {
        val fm = supportFragmentManager
        if (fm.backStackEntryCount >0){
            fm.popBackStack()
        }else{
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                super.onBackPressed()
            } else {
                Snackbar.make(mainContent!!, "Tap back again to exit", Snackbar.LENGTH_SHORT).show()
            }

            backPressedTime = System.currentTimeMillis()
        }
    }
}