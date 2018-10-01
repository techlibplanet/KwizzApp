package com.technoholicdeveloper.kwizzapp.result

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.mayank.googleplaygame.network.wallet.Itransaction
import com.example.mayank.googleplaygame.network.wallet.Transactions
import com.example.mayank.myplaygame.network.ApiClient
import com.google.android.gms.games.multiplayer.Participant

import com.technoholicdeveloper.kwizzapp.R
import com.technoholicdeveloper.kwizzapp.libplaygame.PlayGameLibrary
import com.technoholicdeveloper.kwizzapp.result.adapter.FinalResultViewModel
import com.technoholicdeveloper.kwizzapp.result.adapter.ResultViewAdapter
import com.technoholicdeveloper.kwizzapp.viewhelper.ShowResultProgress
import com.technoholicdeveloper.kwizzapp.viewmodels.ResultViewModel
import com.technoholicdeveloper.kwizzapp.viewmodels.TestViewModel
import net.rmitsolutions.mfexpert.lms.helpers.hideProgress
import net.rmitsolutions.mfexpert.lms.helpers.logD
import net.rmitsolutions.mfexpert.lms.helpers.showDialog
import net.rmitsolutions.mfexpert.lms.helpers.showProgress
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.find
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Comparable
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

const val RIGHT_ANSWERS = "RightAnswers"
const val WRONG_ANSWERS = "WrongAnswers"
const val DROP_QUESTIONS = "DropQuestions"
private const val AMOUNT = "Amount"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [GameResultFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [GameResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class GameResultFragment : Fragment(), View.OnClickListener {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private var rightAnswers: Int? = 0
    private var wrongAnswers: Int? = 0
    private var dropQuestions: Int? = 0
    private var amount : Float? = 0F
    private lateinit var resultRecyclerView: RecyclerView
    val adapter: ResultViewAdapter by lazy { ResultViewAdapter() }
    private var playGameLibrary : PlayGameLibrary ? =null
    private lateinit var buttonBack : Button
    private lateinit var list : MutableList<ResultViewModel>

    private lateinit var showResultProgress: ShowResultProgress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            rightAnswers = it.getInt(RIGHT_ANSWERS)
            wrongAnswers = it.getInt(WRONG_ANSWERS)
            dropQuestions = it.getInt(DROP_QUESTIONS)
            amount = it.getFloat(AMOUNT)
        }
        playGameLibrary = PlayGameLibrary(activity!!)
        resultList = mutableListOf<ResultViewModel>()
        showResultProgress = ShowResultProgress()
        list = mutableListOf<ResultViewModel>()

        logD( "Right Answers - $rightAnswers")
        logD("Wrong Answers - $wrongAnswers")
        logD("Drop Questions - $dropQuestions")

        context?.registerReceiver(resultBroadcastReceiver, syncIntentFilter);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_game_result, container, false)
        resultRecyclerView = view.findViewById(R.id.result_recycler_view)
        resultRecyclerView.layoutManager = LinearLayoutManager(activity)
        resultRecyclerView.setHasFixedSize(true)
        resultRecyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        buttonBack = view.find(R.id.buttonBack)
        buttonBack.setOnClickListener(this)
        resultRecyclerView.adapter = adapter
        showResultProgress.showProgressDialog(activity!!)
        setItem()
        return view
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.buttonBack ->{
                playGameLibrary?.leaveRoom()
            }
        }
    }


    private fun setItem(){
        PlayGameLibrary.GameConstants.modelList.clear()
        logD("Own Image uri - ${PlayGameLibrary.GameConstants.imageUri}")
        PlayGameLibrary.GameConstants.modelList.add(ResultViewModel(PlayGameLibrary.GameConstants.displayName!!,rightAnswers!!, PlayGameLibrary.GameConstants.imageUri))

        for (p in PlayGameLibrary.GameConstants.mParticipants!!) {
            if (PlayGameLibrary.GameConstants.mRoomId != null) {
                val pid = p.participantId
                if (pid == PlayGameLibrary.GameConstants.mMyId) {
                    logD("Adding sender in fragment")
                    PlayGameLibrary.GameConstants.mFinishedParticipants.add(pid)

                }
            }
        }

        updateScore()
    }

    private fun setRecyclerViewAdapter(list: List<ResultViewModel>) {
        adapter.items = list
        adapter.notifyDataSetChanged()
    }

    private lateinit var resultList : List<ResultViewModel>



    private fun updateScore() {
        if (PlayGameLibrary.GameConstants.mRoomId!=null){
            for (p in PlayGameLibrary.GameConstants.mParticipants!!){
                val pid = p.participantId
                if (pid == PlayGameLibrary.GameConstants.mMyId) {
                    continue
                }
                if (p.status != Participant.STATUS_JOINED) {
                    continue
                }

                if (PlayGameLibrary.GameConstants.mParticipants?.size != PlayGameLibrary.GameConstants.mFinishedParticipants.size){
                    continue
                }

                val score = if (PlayGameLibrary.GameConstants.mParticipantScore.containsKey(pid)) PlayGameLibrary.GameConstants.mParticipantScore.get(pid) else 0
                logD("Participants size = ${PlayGameLibrary.GameConstants.mParticipants?.size} Finished Participants size - ${PlayGameLibrary.GameConstants.mFinishedParticipants.size}")

                if (PlayGameLibrary.GameConstants.mParticipants?.size == PlayGameLibrary.GameConstants.mFinishedParticipants.size){
                    if (p.displayName != PlayGameLibrary.GameConstants.displayName){
                        PlayGameLibrary.GameConstants.modelList.add(ResultViewModel(p.displayName, score!!, p.iconImageUri))

                        for (data in PlayGameLibrary.GameConstants.modelList){
                            logD("Before - PlayerName : ${data.playerName} Marks - ${data.rightAnswers} Image Uri - ${data.imageUri}")
                        }

//                    val result = Collections.max(PlayGameLibrary.GameConstants.modelList, compResult())

//                    logD("Result Player Name - ${result.playerName} score - ${result.rightAnswers}")
                        resultList = PlayGameLibrary.GameConstants.modelList.sortedByDescending {
                            it.rightAnswers
                        }

//                        Comparable<ResultViewModel> {
//                            it.rightAnswers
//                        }

                        for (data in resultList){
                            logD("After - PlayerName : ${data.playerName} Marks - ${data.rightAnswers} Image Uri - ${data.imageUri}")
                        }
                    }else {
                        logD("Display name are same")
                    }





                }else{
                    logD("Playgame participants and finished size is not equal")
                }



                showResultProgress.hideProgressDialog()
                buttonBack.visibility = View.VISIBLE

//                if (PlayGameLibrary.GameConstants.mParticipants.size == PlayGameLibrary.GameConstants.mFinishedParticipants.size){
//                    logD("List size is equal")
//
//                    logD("Other image uri - ${p.iconImageUri}")
//                    PlayGameLibrary.GameConstants.modelList.add(ResultViewModel(p.displayName, "$score",p.iconImageUri))
//
//                    logD("All players finished the game...")
//
////                    PlayGameLibrary.GameConstants.modelList.sortByDescending {
////                        it.rightAnswers
////                    }
//
//                    for (data in PlayGameLibrary.GameConstants.modelList){
//                        logD("inside loop Before - PlayerName : ${data.playerName} Marks - ${data.rightAnswers} Image Uri - ${data.imageUri}")
//                    }
//
//                    list = bubbleSortResult(PlayGameLibrary.GameConstants.modelList)
//
//                    for (data in list){
//                        logD("inside loop After - PlayerName : ${data.playerName} Marks - ${data.rightAnswers} Image Uri - ${data.imageUri}")
//                    }
//
//
//
//
//
//
//
//
//                    //val result = Collections.max(PlayGameLibrary.GameConstants.modelList, compResult())
////                    logD("Result max value is = Player Name ${result.playerName}, Player score - ${result.rightAnswers}")
////                    showDialog(activity!!, "Summary", "Winner - ${result.playerName} \nScore - ${result.rightAnswers}")
////                    if (list[0].playerName == PlayGameLibrary.GameConstants.displayName){
////                        if (list[1].rightAnswers == list[0].rightAnswers &&
////                                list[0].playerName != PlayGameLibrary.GameConstants.displayName){
////                            showDialog(activity!!, "Result", "Tie between ${list[0].playerName} and ${list[1].playerName}!")
////                        }else{
////                            var totalAmount = (amount?.times(PlayGameLibrary.GameConstants.mFinishedParticipants.size))?.times(80)?.div(100)
////
////                            logD("Total amount - $totalAmount")
////                            val message = "Congrats! You Win!\nAmount Bid - $amount\nAmount Win - ${totalAmount}"
////                            if (!PlayGameLibrary.GameConstants.balanceAdded){
////                                PlayGameLibrary.GameConstants.balanceAdded = true
////                                updateBalance(PlayGameLibrary.GameConstants.displayName!!, totalAmount, Calendar.getInstance().time.toString(), message)
////                            }
////                        }
////                    }else {
////                        val message = "Sorry! You Loose!\nAmount Bid - $amount\nAmount Loose - $amount"
////                        if (!PlayGameLibrary.GameConstants.balanceAdded){
////                            PlayGameLibrary.GameConstants.balanceAdded = true
////                            //subtractBalance(PlayGameLibrary.GameConstants.displayName!!, amount, Calendar.getInstance().time.toString(), message)
////                            if (list[0].playerName != "Player Name"){
////                                list.add(0,ResultViewModel("Player Name", "Scores", null))
////                            }
////                            setRecyclerViewAdapter(list)
////                            showDialog(activity!!, "Summary", "$message")
////                        }
////                    }
//                    buttonBack.visibility = View.VISIBLE
//                    showResultProgress.hideProgressDialog()
//
//                }else {
//                    logD("List size is not equal")
//                }
            }

            setRecyclerViewAdapter(resultList)


        }
    }

//    private fun subtractBalance(playerName: String, amount: Float?, timeStamp: String, message : String) {
//        val apiClient = ApiClient()
//        var retrofit = apiClient.getService<Itransaction>()
//        retrofit.subtractResultBalance(playerName, amount!!, timeStamp).enqueue(object : Callback<Transactions>{
//            override fun onFailure(call: Call<Transactions>?, t: Throwable?) {
//                showDialog(activity!!, "Error", "Error : $t")
//            }
//
//            override fun onResponse(call: Call<Transactions>?, response: Response<Transactions>?) {
//                if (response?.isSuccessful!!){
//                    val balance = response.body()?.balance
//                    if (PlayGameLibrary.GameConstants.modelList[0].playerName != "Player Name"){
//                        PlayGameLibrary.GameConstants.modelList.add(0,ResultViewModel("Player Name", "Scores", null))
//                    }
//                    setRecyclerViewAdapter(PlayGameLibrary.GameConstants.modelList)
//                    showDialog(activity!!, "Summary", "$message\nBalance : $balance")
//                }else{
//                    showDialog(activity!!, "Summary", "$message\nError : ${response.body()?.error}")
//                }
//            }
//
//        })
//    }

    private fun bubbleSortResult(modelList: MutableList<ResultViewModel>): MutableList<ResultViewModel> {
        var tempList: ResultViewModel
        for (i in 0 until modelList.size){
            for (j in 0 until modelList.size){
                if (modelList[i].rightAnswers > modelList[j].rightAnswers){
                    tempList = modelList[i]
                    modelList[i] = modelList[j]
                    modelList[j] = tempList
                }
            }
        }
        return modelList
    }

    private fun updateBalance(displayName: String, totalAmount: Float?, timeStamp: String, message : String) {
        val apiClient = ApiClient()
        var retrofit = apiClient.getService<Itransaction>()
        retrofit.addResultBalance(displayName, totalAmount!!, timeStamp).enqueue(object : Callback<Transactions> {
            override fun onFailure(call: Call<Transactions>?, t: Throwable?) {
                showDialog(activity!!, "Error", "Error : $t")
            }

            override fun onResponse(call: Call<Transactions>?, response: Response<Transactions>?) {
                if (response?.isSuccessful!!){
                    val balance = response.body()?.balance
                    logD("Message - $message Balance - $balance")
                    if (list[0].playerName != "Player Name"){
                        PlayGameLibrary.GameConstants.modelList.add(0,ResultViewModel("Player Name", "Score".toInt(), null))
                    }

                    showDialog(activity!!, "Summary", "$message\nBalance : $balance")
                    setRecyclerViewAdapter(PlayGameLibrary.GameConstants.modelList)
                }
            }

        })

    }

    fun unlockAchievements(){

    }


    inner class compResult : Comparator<ResultViewModel> {
        override fun compare(a: ResultViewModel, b: ResultViewModel): Int {
            if (a.rightAnswers > b.rightAnswers)
                return -1 // highest value first
            return if (a.rightAnswers === b.rightAnswers) 0 else 1
        }
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
         * @return A new instance of fragment GameResultFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                GameResultFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }

        const val ACTION_RESULT_RECEIVED = "com.technoholicdeveloper.kwizzapp.ACTION_RESULT_RECEIVED"
    }

    private val syncIntentFilter = IntentFilter(ACTION_RESULT_RECEIVED)

    private val resultBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            logD( "Receiving broadcast...")
            logD("${intent.action}")
            if (GameResultFragment.ACTION_RESULT_RECEIVED == intent.action) {
                val state = intent.getCharExtra("state", 'Z')
                val rightAnswers = intent.getIntExtra("RightAnswers", -1)
                val wrongAnswers = intent.getIntExtra("WrongAnswers", -1)
                val dropQuestions = intent.getIntExtra("DropQuestions", -1)
                logD("State - $state")
                logD("RightAnswers - $rightAnswers")
                logD("WrongAnswers - $wrongAnswers")
                logD("DropQuestions - $dropQuestions")
                updateScore()


            }
        }
    }
}
