package com.technoholicdeveloper.kwizzapp

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.games.Games
import com.google.android.gms.games.InvitationsClient
import com.google.android.gms.games.RealTimeMultiplayerClient
import com.technoholicdeveloper.kwizzapp.dashboard.DashboardFragment
import com.technoholicdeveloper.kwizzapp.helper.Constants
import com.technoholicdeveloper.kwizzapp.login.LoginFragment
import com.technoholicdeveloper.kwizzapp.play.PlayMenuFragment
import com.technoholicdeveloper.kwizzapp.userInfo.UserInformationFragment
import com.technoholicdeveloper.kwizzapp.wallet.WalletFragment
import net.rmitsolutions.mfexpert.lms.helpers.*

class MainActivity : BaseActivity(), LoginFragment.OnFragmentInteractionListener,
        DashboardFragment.OnFragmentInteractionListener,
        UserInformationFragment.OnFragmentInteractionListener,
        WalletFragment.OnFragmentInteractionListener,
        PlayMenuFragment.OnFragmentInteractionListener{



    internal var mSignedInAccount: GoogleSignInAccount? = null
    // Client used to interact with the real time multiplayer system.
    private var mRealTimeMultiplayerClient: RealTimeMultiplayerClient? = null
    //Client used to interact with the Invitation system.
    private var mInvitationsClient: InvitationsClient? = null
    private var mPlayerId: String? = null
//    private lateinit var playGameLib: PlayGameLib

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val loginFrag = LoginFragment()
        switchToFragment(loginFrag)

    }


    override fun onResume() {
        super.onResume()
        signInSilently()
    }

    private fun signInSilently() {
        val signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
        signInClient.silentSignIn().addOnCompleteListener(this
        ) { task ->
            if (task.isSuccessful) {
                // The signed in account is stored in the task's result.
                val signedInAccount = task.result
                logD("Task Successful")
                onConnected(signedInAccount)
            } else {
                // Player will need to sign-in explicitly using via UI
                logD("Unable to sign in silently")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // The signed in account is stored in the result.
                val signedInAccount = result.signInAccount
                logD( "Result Successful")
                onConnected(signedInAccount!!)

            } else {
                var message = result.status.statusMessage
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error)
                }
                logE("Error : ${result.status}")
                AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show()
            }
        }
    }


    private fun onConnected(signedInAccount: GoogleSignInAccount) {
        if (mSignedInAccount != signedInAccount) {

            mSignedInAccount = signedInAccount
            val gameClient = Games.getGamesClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            gameClient.setViewForPopups(findViewById(android.R.id.content))
//            Games.getGamesClient(this, signedInAccount).setViewForPopups()
            // update the clients
            mRealTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(this, signedInAccount)
            mInvitationsClient = Games.getInvitationsClient(this@MainActivity, signedInAccount)

            // get the playerId from the PlayersClient
            val playersClient = Games.getPlayersClient(this, signedInAccount)
            playersClient.currentPlayer.addOnSuccessListener { player ->
                mPlayerId = player.playerId
                Log.d("Player ID", mPlayerId)
                Log.d("Display Name", player.displayName)
                putPref(SharedPrefKeys.PLAYER_ID, mPlayerId!!)
                putPref( SharedPrefKeys.DISPLAY_NAME, player.displayName)
                //val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                val nameArray = player.name.split(" ")
                logD("Player Name - ${player.name}")
                putPref( SharedPrefKeys.FIRST_NAME, nameArray[0])
                putPref( SharedPrefKeys.LAST_NAME, nameArray[1])
                //startActivity(intent)
                logD("${getPref(SharedPrefKeys.FIRST_NAME, "")}")
                val email = getPref(SharedPrefKeys.EMAIL, "")
                val mobileNumber = getPref(SharedPrefKeys.MOBILE_NUMBER, "")

                logD("Email - $email MobileNumber - $mobileNumber")
                if (email=="" && mobileNumber == ""){
                    val userInfoFragment = UserInformationFragment()
                    switchToFragment(userInfoFragment)

                }else{
                    val dashboardFrag = DashboardFragment()
                    switchToFragment(dashboardFrag)
                }

            }

        }

    }

    private fun switchToFragment(newFrag: Fragment) {
        supportFragmentManager?.beginTransaction()?.replace(R.id.main_content, newFrag)?.commit()
    }

    override fun onFragmentInteraction(uri: Uri) {

    }


}
