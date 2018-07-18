package com.technoholicdeveloper.kwizzapp.play

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.mayank.googleplaygame.network.wallet.Itransaction
import com.example.mayank.googleplaygame.network.wallet.Transactions
import com.example.mayank.myplaygame.network.ApiClient

import com.technoholicdeveloper.kwizzapp.R
import com.technoholicdeveloper.kwizzapp.libplaygame.PlayGameLibrary
import com.technoholicdeveloper.kwizzapp.quiz.QuizFragment
import com.technoholicdeveloper.kwizzapp.viewmodels.ResultViewModel
import kotlinx.android.synthetic.main.input_game_layout.*
import net.rmitsolutions.mfexpert.lms.helpers.*
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.find
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalArgumentException
import java.util.*
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [GameDetailFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [GameDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class GameDetailFragment : Fragment(), View.OnClickListener {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var playGameLibrary: PlayGameLibrary
    private var i = -1
    private var j: Int = 0
    private var k = -1
    private var l: Int = 0
    private var subject: String? = null
    private var subCode: String? = null
    private var amount: String? = null
    private lateinit var amountList: Array<String>
    private lateinit var subjectList: Array<String>
    private lateinit var subjectCode: Array<String>
    private lateinit var textSwitcherCountdown: TextSwitcher
    private lateinit var textViewCount: TextView
    private val syncIntentFilter = IntentFilter(ACTION_MESSAGE_RECEIVED)
    private var timerStatus = TimerStatus.STOPPED
    private var progressBar: ProgressBar? = null
    private var textViewSeconds: TextView? = null
    private var countDownTimer: CountDownTimer? = null
    private var timeCountInMilliSeconds = (1 * 10000).toLong()
    private val CLICKABLES = intArrayOf(R.id.imageButtonNextAmount, R.id.imageButtonNextSubject,
            R.id.imageButtonPreviousAmount, R.id.imageButtonPreviousSubject)
    private var textLabel: TextView? = null
    private var subtract: Boolean = false

    private enum class TimerStatus {
        STARTED,
        STOPPED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        playGameLibrary = PlayGameLibrary(activity!!)
        subjectList = resources.getStringArray(R.array.subjectList)
        subjectCode = resources.getStringArray(R.array.subject_code)
        amountList = resources.getStringArray(R.array.amount)
        activity?.registerReceiver(messageBroadcastReceiver, syncIntentFilter);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_game_detail, container, false)
        progressBar = view.find(R.id.progressBar)
        textViewSeconds = view.find(R.id.textViewSeconds)
        textLabel = view.find(R.id.textViewLabel)
        view.find<Button>(R.id.buttonLeaveRoom).setOnClickListener(this)
        for (id in CLICKABLES) {
            view.findViewById<ImageButton>(id).setOnClickListener(this)
        }
        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageButtonNextAmount -> {
                nextAmount()
//                playGameLib?.broadcastScore(true)
                playGameLibrary.broadcastMessage('A', 0)
                reset()
            }

            R.id.imageButtonPreviousAmount -> {
                previousAmount()
                playGameLibrary.broadcastMessage('A', 1)
                reset()

            }
            R.id.imageButtonPreviousSubject -> {
                previousSubject()
                playGameLibrary.broadcastMessage('S', 1)
                reset()
            }

            R.id.imageButtonNextSubject -> {
                nextSubject()
                reset()
                playGameLibrary.broadcastMessage('S', 0)
            }

            R.id.buttonLeaveRoom -> {
                playGameLibrary.leaveRoom()
            }
        }
    }

    private fun start() {
        if (timerStatus == TimerStatus.STOPPED) {
            setTimerValues()
            setProgressBarValue()
            timerStatus = TimerStatus.STARTED
            startCountdownTimer()
        }
    }

    private fun setTimerValues() {
        var time = 1
        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = (time * 10 * 1000).toLong()
    }

    private fun startCountdownTimer() {
        countDownTimer = object : CountDownTimer(timeCountInMilliSeconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                textViewSeconds!!.text = secondsFormatter(millisUntilFinished)
                progressBar!!.progress = (millisUntilFinished / 1000).toInt()

            }

            override fun onFinish() {

                //textViewSeconds!!.text = "0"
                // call to initialize the progress bar values
                //setProgressBarValue()


                // changing the timer status to stopped
                timerStatus = TimerStatus.STOPPED

                if (amount == null) {
                    Toast.makeText(activity, "Select a valid Amount!", Toast.LENGTH_SHORT).show()
                } else if (subject == null) {
                    Toast.makeText(activity, "Select a valid subject!", Toast.LENGTH_SHORT).show()
                } else {
                    checkBalance()
                }
            }

        }.start()
        countDownTimer!!.start()
    }

    private fun checkBalance() {
        val mobileNumber = activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")
        if (!mobileNumber.isNullOrBlank()) {
            val apiClient = ApiClient()
            var retrofit = apiClient.getService<Itransaction>()
            retrofit.checkBalance(mobileNumber!!).enqueue(object : Callback<Transactions> {
                override fun onFailure(call: Call<Transactions>?, t: Throwable?) {
                    logD("Error - $t")
                    activity?.showDialog(activity!!, "Error", "$t")

                }

                override fun onResponse(call: Call<Transactions>?, response: Response<Transactions>?) {
                    if (response?.isSuccessful!!) {
                        val balance = response.body()?.balance

                        if (amount!! <= balance!!) {
                            if (activity != null) {
                                +
                                logD("Subtracting amount -$amount")
                                if (!subtract) {
                                    subtractBalance(PlayGameLibrary.GameConstants.displayName!!, amount?.toFloat(), Calendar.getInstance().time.toString())
                                    subtract = true
                                }
                            }

                        } else {
                            showDialog(activity!!, "Warning", "one of the opponent may have insufficient balance amount !.\nSelect lower amount")
                        }
                    }
                }
            })
        } else {
            logD("Mobile number is null")
        }
    }


    private fun subtractBalance(playerName: String, amount: Float?, timeStamp: String) {
        val apiClient = ApiClient()
        var retrofit = apiClient.getService<Itransaction>()
        retrofit.subtractResultBalance(playerName, amount!!, timeStamp).enqueue(object : Callback<Transactions> {
            override fun onFailure(call: Call<Transactions>?, t: Throwable?) {
                showDialog(activity!!, "Error", "Error : $t")
            }

            override fun onResponse(call: Call<Transactions>?, response: Response<Transactions>?) {
                if (response?.isSuccessful!!) {
//                    val balance = response.body()?.balance
                    if (countDownTimer != null) {
                        countDownTimer?.cancel()
                    }
                    val bundle = Bundle()
                    bundle.putString("Subject", subject)
                    bundle.putString("SubjectCode", subCode)
                    bundle.putFloat("Amount", amount.toFloat())
//                              val quizFragment = SinglePlayerQuizFragment()
//                              quizFragment.arguments = bundle
//                               playGameLib?.switchToFragment(quizFragment)
                    logD("Amount - $amount code -$subCode subject -$subject")
                    if (activity != null) {
                        val quizFragment = QuizFragment()
                        quizFragment.arguments = bundle
                        switchToFragment(activity!!, quizFragment)
                        unRegisterBroadcastReceiver()
                    } else {
                        logD("Activity is null")
                    }
                } else {
                    showDialog(activity!!, "Summary", "Error : ${response.body()?.error}")
                }
            }

        })
    }

    private fun stopCountdownTimer() {
        if (timerStatus == TimerStatus.STARTED) {
            countDownTimer?.cancel()
        }
    }

    private fun setProgressBarValue() {
        progressBar!!.max = timeCountInMilliSeconds.toInt() / 1000
        progressBar!!.progress = timeCountInMilliSeconds.toInt() * 1000
    }

    private fun reset() {
        textLabel?.visibility = View.VISIBLE
        if (timerStatus == TimerStatus.STOPPED) {
            setTimerValues()
            setProgressBarValue()
            timerStatus = TimerStatus.STARTED
            startCountdownTimer()
            return
        }
        stopCountdownTimer()
        startCountdownTimer()
    }

    private fun secondsFormatter(milliSeconds: Long): String {
        return String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)))
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GameDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                GameDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }

        const val ACTION_MESSAGE_RECEIVED = "com.technoholicdeveloper.kwizzapp.ACTION_MESSAGE_RECEIVED"
    }

    private val messageBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (ACTION_MESSAGE_RECEIVED == intent.action) {
                val state = intent.getCharExtra("state", 'Z')
                val value = intent.getIntExtra("value", -1)
                logD("State - $state")
                logD("Value - $value")
                if (state == 'A') {
                    if (value == 0) {
                        nextAmount()
                    } else if (value == 1) {
                        previousAmount()
                    }
                    //resetCountdownTimer(10000, 1000)
                    reset()
                } else if (state == 'S') {
                    if (value == 0) {
                        nextSubject()
                    } else if (value == 1) {
                        previousSubject()
                    }
                    //resetCountdownTimer(10000, 1000)
                    reset()
                }
            }
        }
    }

    private fun nextAmount() {
        if (k < 20) {
            k++
            l = k
            amount = amountList[k]
            textViewAmount.text = amount
        } else {
            k = 0
            amount = amountList[k]
            textViewAmount.text = amount
        }
    }

    private fun previousAmount() {
        if (l > 0) {
            l--
            k = l
            amount = amountList[l]
            textViewAmount.text = amount
        } else {
            l = 20
            amount = amountList[l]
            textViewAmount.text = amount
        }
    }

    private fun nextSubject() {
        if (i < 6) {
            i++
            logD("value of i : $i")
            j = i
            subject = subjectList[i]
            subCode = subjectCode[i]
            textViewSubject.text = subject
        } else {
            i = 0
            subject = subjectList[i]
            subCode = subjectCode[i]
            textViewSubject.text = subject
        }
    }

    private fun previousSubject() {
        if (j > 0) {
            j--
            i = j
            subCode = subjectCode[j]
            subject = subjectList[j]
            textViewSubject.text = subject
        } else {
            j = 6
            subCode = subjectCode[j]
            subject = subjectList[j]
            textViewSubject.text = subject
        }
    }

    private fun unRegisterBroadcastReceiver() {
        try {
            if (messageBroadcastReceiver != null) {
                activity?.unregisterReceiver(messageBroadcastReceiver)
            }
        } catch (e: IllegalArgumentException) {
            logE("Error - $e")
        }


    }
}
