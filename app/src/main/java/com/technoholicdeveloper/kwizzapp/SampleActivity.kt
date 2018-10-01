package com.technoholicdeveloper.kwizzapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.google.android.gms.games.LeaderboardsClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.technoholicdeveloper.kwizzapp.achievements.Achievements
import com.technoholicdeveloper.kwizzapp.leaderboards.LeaderBoards
import com.technoholicdeveloper.kwizzapp.viewmodels.ResultViewModel
import com.technoholicdeveloper.kwizzapp.viewmodels.TestViewModel
import net.rmitsolutions.mfexpert.lms.helpers.logD
import net.rmitsolutions.mfexpert.lms.helpers.logE
import org.jetbrains.anko.find

class SampleActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var buttonUnlock : Button
    private var int : Int = 0
    private lateinit var achievement: Achievements
    private lateinit var leaderBoards: LeaderBoards
    private val RC_UNUSED = 5001
    private lateinit var leaderboardsClient: LeaderboardsClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        achievement = Achievements(this)
        leaderBoards = LeaderBoards(this)
        leaderboardsClient = Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)

        buttonUnlock = find(R.id.buttonUnlock)
        buttonUnlock.setOnClickListener(this)
    }

    lateinit var modelList: MutableList<TestViewModel>

    override fun onClick(v: View?) {
        when(v?.id){
//            R.id.buttonUnlock -> {
//                int++
////
//                if (int == 1){
//                    logD("Int value = $int")
//                    achievement.unlockIncrementalAchievement(R.string.beginner, 10)
//                }
//                if (int == 2){
//                    logD("Int value - $int")
//                    val score = 1337
//                    leaderboardsClient.submitScoreImmediate(getString(R.string.global), score.toLong())
//                }
                // To open all leaderboards list
//                leaderboardsClient.allLeaderboardsIntent
//                        .addOnSuccessListener(OnSuccessListener<Intent> { intent -> startActivityForResult(intent, RC_UNUSED) })
//                        .addOnFailureListener(OnFailureListener { e -> logE("Error - $e")})
//            }
        }

        modelList = mutableListOf<TestViewModel>()
        modelList.add(TestViewModel("Mayank Sharma", 10))
        modelList.add(TestViewModel("Priyank Sharma", 2))
        modelList.add(TestViewModel("Mohit Wadhwami", 4))
        modelList.add(TestViewModel("Anil Thakur", 3))
        modelList.add(TestViewModel("Sudhansh Shrivastava", 2))
        modelList.add(TestViewModel("Manoj patidar", 7))
        modelList.add(TestViewModel("vikas parihar", 6))
        modelList.add(TestViewModel("Arjun Bhosle", 8))


        for(data in modelList){
            logD("Before = Name : ${data.playerName} Marks : ${data.rightAnswers}")
        }

        //bubbleSortResult(modelList)

        modelList.sortedByDescending {
            it.rightAnswers
        }

        for(data in modelList){
            logD("After = Name : ${data.playerName} Marks : ${data.rightAnswers}")
        }

    }



    private fun bubbleSortResult(modelList: MutableList<TestViewModel>): MutableList<TestViewModel> {
        var newList = TestViewModel("", 0)
        for (i in 0 until modelList.size){
            for (j in 0 until modelList.size){
                if (modelList[i].rightAnswers > modelList[j].rightAnswers){
                    newList = modelList[i]
                    modelList[i] = modelList[j]
                    modelList[j] = newList
                }
            }
        }
        return modelList
    }

}
