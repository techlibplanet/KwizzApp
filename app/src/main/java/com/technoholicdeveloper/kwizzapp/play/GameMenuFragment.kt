package com.technoholicdeveloper.kwizzapp.play

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import com.example.mayank.googleplaygame.network.wallet.Itransaction
import com.example.mayank.googleplaygame.network.wallet.Transactions
import com.example.mayank.myplaygame.network.ApiClient

import com.technoholicdeveloper.kwizzapp.R
import com.technoholicdeveloper.kwizzapp.libplaygame.PlayGameLibrary
import com.technoholicdeveloper.kwizzapp.wallet.AddPointsFragment
import net.rmitsolutions.mfexpert.lms.helpers.*
import org.jetbrains.anko.find
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PlayMenuFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PlayMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class GameMenuFragment : Fragment(), View.OnClickListener {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private val CLICKABLES = intArrayOf(R.id.singlePlayerButton, R.id.quickGameButton, R.id.multiplayerButton, R.id.invitationButton)
    private lateinit var leftToRight: Animation
    private lateinit var rightToLeft : Animation
    private lateinit var playGameLibrary: PlayGameLibrary
    private var check : Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        playGameLibrary = PlayGameLibrary(activity!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_play_menu, container, false)
        rightToLeft = AnimationUtils.loadAnimation(activity, R.anim.right_to_left)
        leftToRight = AnimationUtils.loadAnimation(activity, R.anim.left_to_right)

        for (id in CLICKABLES){
            view.find<Button>(id).setOnClickListener(this)
            when(id){
                R.id.singlePlayerButton -> view.find<Button>(id).animation = leftToRight
                R.id.quickGameButton -> view.find<Button>(id).animation = rightToLeft
                R.id.multiplayerButton -> view.find<Button>(id).animation = leftToRight
                R.id.invitationButton -> view.find<Button>(id).animation = rightToLeft

            }
        }
        return view
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.singlePlayerButton ->{

            }
            R.id.quickGameButton ->{
                showProgress()
                check = 0
                checkBalance()

            }

            R.id.multiplayerButton ->{
                showProgress()
                check = 1
                checkBalance()
            }
            R.id.invitationButton->{
                showProgress()
                playGameLibrary.showInvitationInbox()
            }
        }
    }

    private fun checkBalance() {
        val mobileNumber = activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")
        if (mobileNumber!=null){
            val apiClient = ApiClient()
            var retrofit = apiClient.getService<Itransaction>()
            retrofit.checkBalance(mobileNumber).enqueue(object : Callback<Transactions> {
                override fun onFailure(call: Call<Transactions>?, t: Throwable?) {
                    logD("Error - $t")

                }

                override fun onResponse(call: Call<Transactions>?, response: Response<Transactions>?) {
                    if (response?.isSuccessful!!){
                        val balance = response.body()?.balance
                        if(balance?.toFloat()!!>=10){
                            if (check == 0){
                                playGameLibrary.startQuickGame()
                            }else {
                                playGameLibrary.invitePlayers()
                            }
                            check = -1

                        }else{
                            AlertDialog.Builder(activity!!).setTitle("Error").setMessage("\nInsufficient balance to play game.")
                                    .setPositiveButton("Add Balance", DialogInterface.OnClickListener{ dialogInterface, i ->
                                        val addPointsFragment = AddPointsFragment()
                                        switchToFragment(activity!!,addPointsFragment)
                                    }).setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                                        dialogInterface.dismiss()
                                    }).show()
                        }
                    }
                }
            })
        }else {
            logD("Mobile number is null")
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
         * @return A new instance of fragment PlayMenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                GameMenuFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
