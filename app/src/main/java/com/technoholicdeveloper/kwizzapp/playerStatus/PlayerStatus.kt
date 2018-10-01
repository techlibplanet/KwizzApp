package com.technoholicdeveloper.kwizzapp.playerStatus

import android.app.Activity
import android.support.annotation.NonNull
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import android.util.Log
import com.google.android.gms.games.stats.PlayerStats
import com.google.android.gms.games.AnnotatedData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.google.android.gms.tasks.Task


class PlayerStatus(private val activity: Activity) {

    private val TAG = PlayerStatus::class.java.simpleName

    fun checkPlayerStats() {
        Games.getPlayerStatsClient(activity, GoogleSignIn.getLastSignedInAccount(activity)!!)
                .loadPlayerStats(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Check for cached data.
                        if (task.result.isStale()) {
                            Log.d(TAG, "using cached data")
                        }
                        val stats = task.result.get()
                        if (stats != null) {
                            Log.d(TAG, "Player stats loaded")
                            if (stats!!.daysSinceLastPlayed > 7) {
                                Log.d(TAG, "It's been longer than a week")
                            }
                            if (stats!!.numberOfSessions > 1000) {
                                Log.d(TAG, "Veteran player")
                            }
                            if (stats!!.getChurnProbability() == 1f) {
                                Log.d(TAG, "Player is at high risk of churn")
                            }

                        }
                    } else {
                        var status = CommonStatusCodes.DEVELOPER_ERROR
                        if (task.exception is ApiException) {
                            status = (task.exception as ApiException).statusCode
                        }
                        Log.d(TAG, "Failed to fetch Stats Data status: "
                                + status + ": " + task.getException())
                    }
                }
    }
}