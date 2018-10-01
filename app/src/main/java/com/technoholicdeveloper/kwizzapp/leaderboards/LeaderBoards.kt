package com.technoholicdeveloper.kwizzapp.leaderboards

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.google.android.gms.games.LeaderboardsClient
import com.technoholicdeveloper.kwizzapp.R
import com.google.android.gms.tasks.OnFailureListener
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.content.Intent
import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener



class LeaderBoards(private val activity : Activity) {

    private lateinit var leaderBoardClient : LeaderboardsClient

    fun submitScore(leaderBoardId: Int, score : Long){
//        Games.getLeaderboardsClient(activity, GoogleSignIn.getLastSignedInAccount(activity)!!)
//                .submitScore(activity.getString(R.string.global), score);

        leaderBoardClient = Games.getLeaderboardsClient(activity, GoogleSignIn.getLastSignedInAccount(activity)!!)

//        Games.getLeaderboardsClient(activity, GoogleSignIn.getLastSignedInAccount(activity)!!)
//                .submitScoreImmediate("CgkIv7-g2sIWEAIQCg", score)

        leaderBoardClient.submitScoreImmediate("CgkIv7-g2sIWEAIQCg", score)

    }
}