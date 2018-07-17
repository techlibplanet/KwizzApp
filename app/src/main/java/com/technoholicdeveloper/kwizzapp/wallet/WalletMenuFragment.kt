package com.technoholicdeveloper.kwizzapp.wallet

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
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
import kotlinx.android.synthetic.main.wallet_menu_layout.*
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
 * [WalletFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [WalletFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class WalletMenuFragment : Fragment(), View.OnClickListener {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private lateinit var leftToRight: Animation
    private lateinit var rightToLeft: Animation
    private lateinit var buttonAddPoints: Button
    private lateinit var buttonWithdrawalPoints: Button
    private lateinit var buttonTransferPoints: Button
    private lateinit var buttonTransactions: Button

    private val CLICKABLES = intArrayOf(R.id.buttonAddPoints, R.id.buttonWithdrawalPoints, R.id.buttonTransferPoints, R.id.buttonTransactions)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_wallet, container, false)
        rightToLeft = AnimationUtils.loadAnimation(activity, R.anim.right_to_left)
        leftToRight = AnimationUtils.loadAnimation(activity, R.anim.left_to_right)
        buttonAddPoints = view.find(R.id.buttonAddPoints)
        buttonWithdrawalPoints = view.find(R.id.buttonWithdrawalPoints)
        buttonTransferPoints = view.find(R.id.buttonTransferPoints)
        buttonTransactions = view.find(R.id.buttonTransactions)

        buttonAddPoints.animation = rightToLeft
        buttonWithdrawalPoints.animation = leftToRight
        buttonTransferPoints.animation = rightToLeft
        buttonTransactions.animation = leftToRight

        checkBalance()

        for (id in CLICKABLES){
            view.find<Button>(id).setOnClickListener(this)
        }

        return view
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.buttonAddPoints ->{
                val addPointsFragment = AddPointsFragment()
                switchToFragment(activity!!,addPointsFragment)
            }

            R.id.buttonWithdrawalPoints ->{
                val withdrawalPointsFragment = WithdrawalPointsFragment()
                switchToFragment(activity!!,withdrawalPointsFragment)
            }

            R.id.buttonTransferPoints ->{
                val transferPointsFragment = TransferPointsFragment()
                switchToFragment(activity!!,transferPointsFragment)
            }

            R.id.buttonTransactions ->{
                showDialog(activity!!, "Transactions", "Coming soon !")
            }
        }
    }

    private fun checkBalance() {
        val mobileNumber = activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")
        if (mobileNumber!=""){
            val apiClient = ApiClient()
            var retrofit = apiClient.getService<Itransaction>()
            retrofit.checkBalance(mobileNumber!!).enqueue(object : Callback<Transactions> {
                override fun onFailure(call: Call<Transactions>?, t: Throwable?) {
                    logD("Error - $t")
                }

                override fun onResponse(call: Call<Transactions>?, response: Response<Transactions>?) {
                    if (response?.isSuccessful!!){
                        val balance = response.body()?.balance
                        balanceTextView.text = "${activity?.getString(R.string.rupeeText)} - $balance"
                    }else {
                        logD("${response.body()?.error}")
                        balanceTextView.text = "Failed"
                        showDialog(activity!!, "Error","${response.body()?.error}")

                    }
                }

            })
        }else {
            balanceTextView.visibility = View.GONE
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
         * @return A new instance of fragment WalletFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                WalletMenuFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
