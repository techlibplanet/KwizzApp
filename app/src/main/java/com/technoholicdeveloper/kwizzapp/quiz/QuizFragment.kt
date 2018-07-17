package com.technoholicdeveloper.kwizzapp.quiz

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextSwitcher
import android.widget.TextView
import com.example.mayank.myplaygame.network.ApiClient
import com.example.mayank.myplaygame.network.IQuestion
import com.example.mayank.myplaygame.network.Question

import com.technoholicdeveloper.kwizzapp.R
import com.technoholicdeveloper.kwizzapp.libplaygame.PlayGameLibrary
import com.technoholicdeveloper.kwizzapp.result.GameResultFragment
import net.rmitsolutions.mfexpert.lms.helpers.hideProgress
import net.rmitsolutions.mfexpert.lms.helpers.logD
import net.rmitsolutions.mfexpert.lms.helpers.showProgress
import net.rmitsolutions.mfexpert.lms.helpers.switchToFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val AMOUNT = "Amount"
private const val SUBJECT_CODE = "SubjectCode"
private const val SUBJECT = "Subject"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [QuizFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [QuizFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class QuizFragment : Fragment(), View.OnClickListener {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private var amount: Float? = null
    private var subjectCode: String? = null
    private var subject: String? = null

    lateinit var randomNumbers: ArrayList<Int>
    private var q = 0
    private var answer  = ""
    private var rightAnswers = 0
    private var wrongAnswers = 0
    private var dropQuestions = 0
    private lateinit var textSwitcherCountdown: TextSwitcher
    private var countDownTimer: CountDownTimer? = null
    private lateinit var textViewCount: TextView
    private var playGameLibrary : PlayGameLibrary? = null
    private var numberOfRows : Int? = null


    private val CLICKABLES = intArrayOf(R.id.text_view_option_a,R.id.text_view_option_b,R.id.text_view_option_c, R.id.text_view_option_d,R.id.text_view_option_e)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            amount = it.getFloat(AMOUNT)
            subjectCode = it.getString(SUBJECT_CODE)
            subject = it.getString(SUBJECT)
        }
        logD("Amount : $amount")
        logD("Subject Code : $subjectCode")
        logD("Subject : $subject")

        playGameLibrary = PlayGameLibrary(activity!!)

        showProgress()

        getNumberOfRows()


    }

    private fun getRandomNonRepeatingIntegers(size: Int, min: Int,
                                              max: Int): ArrayList<Int> {
        randomNumbers = ArrayList()

        while (randomNumbers.size < size) {
            val random = getRandomInt(min, max)

            if (!randomNumbers.contains(random)) {
                randomNumbers.add(random)
            }
        }
        return randomNumbers
    }

    private fun getRandomInt(min: Int, max: Int): Int {
        val random = Random()

        return random.nextInt(max - min + 1) + min
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_quiz, container, false)
        for (id in CLICKABLES){
            view.findViewById<TextView>(id).setOnClickListener(this)
        }

        return view
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.text_view_option_a ->{
                if (view.findViewById<TextView>(R.id.text_view_option_a)?.text == answer){
                    rightAnswers++
                }else {
                    wrongAnswers++
                }
                getQuestionFromServer()
            }

            R.id.text_view_option_b ->{
                if (view.findViewById<TextView>(R.id.text_view_option_b)?.text == answer){
                    rightAnswers++
                }else {
                    wrongAnswers++
                }
                getQuestionFromServer()
            }

            R.id.text_view_option_c ->{
                if (view.findViewById<TextView>(R.id.text_view_option_c)?.text == answer){
                    rightAnswers++
                }else {
                    wrongAnswers++
                }
                getQuestionFromServer()
            }

            R.id.text_view_option_d ->{
                if (view.findViewById<TextView>(R.id.text_view_option_d)?.text == answer){
                    rightAnswers++
                }else {
                    wrongAnswers++
                }
                getQuestionFromServer()
            }

            R.id.text_view_option_e ->{
                if (view.findViewById<TextView>(R.id.text_view_option_e)?.text == answer){
                    rightAnswers++
                }else {
                    wrongAnswers++
                }
                getQuestionFromServer()
            }
        }
    }

    private fun getNumberOfRows() {
        val apiClient = ApiClient()
        var rowCount : Int? = null
        var retrofit = apiClient.getService<IQuestion>()

            retrofit.getNumberOfRows(subjectCode!!).enqueue(object : Callback<Question> {
                override fun onFailure(call: Call<Question>?, t: Throwable?) {
                    logD("Error : $t")
                }

                override fun onResponse(call: Call<Question>?, response: Response<Question>?) {
                    if (response?.isSuccessful!!){
                        val result = response.body()
                        rowCount = result?.numberOfRows

                        getRandomNonRepeatingIntegers(rowCount!!, 1, rowCount!!)
                        getQuestionFromServer()
                        hideProgress()

                    }
                }
            })

    }

    private fun getQuestionFromServer() {
        val apiClient = ApiClient()
        var retrofit = apiClient.getService<IQuestion>()
        logD("Random question - ${randomNumbers[q]}")
        if (q<10){
            retrofit.getQuestion(randomNumbers[q].toString(), subjectCode!!).enqueue(object : Callback<Question> {
                override fun onFailure(call: Call<Question>?, t: Throwable?) {
                    logD("Error : $t")
                }

                override fun onResponse(call: Call<Question>?, response: Response<Question>?) {
                    if (response?.isSuccessful!!){
                        val result = response.body()
                        logD("Response - ${result?.quesCode}")
                        q++
                        setQuestionTextViews(result)
                        //resetCountdownTimer(10000,1000)
                    }
                }
            })
        }else {
            logD("Question Finished !")
            if (countDownTimer != null) {
                Log.d("MyTag", "Cancelled Countdown")
                countDownTimer!!.cancel()
            }
            changeToResultScreen()
            // Broadcast score here
        }
    }

    private fun setQuestionTextViews(result: Question?) {
        answer = result?.answer!!
        view?.findViewById<TextView>(R.id.text_view_question)?.text = "Q. ${result.question}"
        view?.findViewById<TextView>(R.id.text_view_option_a)?.text =  "1. ${result.optionA}"         //result.optionA
        view?.findViewById<TextView>(R.id.text_view_option_b)?.text = "2. ${result.optionB}"
        view?.findViewById<TextView>(R.id.text_view_option_c)?.text = "3. ${result.optionC}"
        view?.findViewById<TextView>(R.id.text_view_option_d)?.text = "4. ${result.optionD}"
        view?.findViewById<TextView>(R.id.text_view_option_e)?.text = "5. ${result.optionE}"
    }

    private fun changeToResultScreen(){
        if (countDownTimer!=null){
            logD("Cancel countdown timer in quiz fragment")
            countDownTimer?.cancel()
        }
        logD("Display Name - ${PlayGameLibrary.GameConstants.displayName}")
        playGameLibrary?.broadcastResult('R', rightAnswers, wrongAnswers, dropQuestions)
        val bundle = Bundle()
        bundle.putFloat("Amount", amount!!)
        bundle.putInt("RightAnswers", rightAnswers)
        bundle.putInt("WrongAnswers", wrongAnswers)
        bundle.putInt("DropQuestions", dropQuestions)
        bundle.putString("DisplayName", PlayGameLibrary.GameConstants.displayName)
        val resultFragment = GameResultFragment()
        resultFragment.arguments = bundle
        switchToFragment(activity!!,resultFragment)
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
         * @return A new instance of fragment QuizFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String, param3 : String) =
                QuizFragment().apply {
                    arguments = Bundle().apply {
                        putString(AMOUNT, param1)
                        putString(SUBJECT_CODE, param2)
                        putString(SUBJECT, param3)

                    }
                }
    }
}
