package com.technoholicdeveloper.kwizzapp.achievements

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.technoholicdeveloper.kwizzapp.R

class Achievements(private val activity: Activity) {

    fun unlockAchievement(achievementId: Int){
        Games.getAchievementsClient(activity, GoogleSignIn.getLastSignedInAccount(activity)!!)
                .unlock(activity.getString(achievementId));
    }

    fun unlockIncrementalAchievement(achievementId: Int, numberOfSteps : Int){
        Games.getAchievementsClient(activity, GoogleSignIn.getLastSignedInAccount(activity)!!)
                .increment(activity.getString(achievementId), numberOfSteps);
    }
}