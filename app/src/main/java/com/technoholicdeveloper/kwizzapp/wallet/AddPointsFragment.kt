package com.technoholicdeveloper.kwizzapp.wallet

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

import com.technoholicdeveloper.kwizzapp.R
import com.technoholicdeveloper.kwizzapp.gateway.PayUMoney
import com.technoholicdeveloper.kwizzapp.helper.Constants
import net.rmitsolutions.mfexpert.lms.helpers.SharedPrefKeys
import net.rmitsolutions.mfexpert.lms.helpers.getPref
import net.rmitsolutions.mfexpert.lms.helpers.logD
import net.rmitsolutions.mfexpert.lms.helpers.putPref
import org.jetbrains.anko.find

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AddPointsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AddPointsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AddPointsFragment : Fragment(), View.OnClickListener {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null


    private var email: String? = null
    private var mobileNumber: String? = null
    private var amount: String? = null

    private lateinit var inputAmount: TextInputEditText
    private lateinit var inputEmail: TextInputEditText
    private lateinit var inputMobileNumber: TextInputEditText
    private lateinit var inputLayoutMobileNumber : TextInputLayout
    private lateinit var inputLayoutEmail : TextInputLayout

    private lateinit var payUMoney: PayUMoney


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        payUMoney = PayUMoney(activity!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_points, container, false)

        view.find<Button>(R.id.buttonPay).setOnClickListener(this)
        inputAmount = view.find(R.id.editTextAmount)
        inputEmail = view.find(R.id.editTextEmail)
        inputMobileNumber = view.find(R.id.editTextMobileNumber)
        inputLayoutMobileNumber = view.find(R.id.inputLayoutMobileNumber)
        inputLayoutEmail = view.find(R.id.inputLayoutEmail)
        email = activity?.getPref(SharedPrefKeys.EMAIL, "")
        mobileNumber = activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")
        logD("Mobile Number - $mobileNumber Email - $email Amount - $amount")
        if (email == "" && mobileNumber == "") {
            inputLayoutEmail.visibility = View.VISIBLE
            inputLayoutMobileNumber.visibility = View.VISIBLE
        }
        return view
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.buttonPay ->{
                if (email.isNullOrBlank() && mobileNumber.isNullOrBlank()){
                    email = inputEmail.text.toString().trim()
                    mobileNumber = inputMobileNumber.text.toString().trim()
                    activity?.putPref(SharedPrefKeys.EMAIL, email)
                    activity?.putPref(SharedPrefKeys.MOBILE_NUMBER, mobileNumber!!)
                }
                amount = inputAmount.text.toString().trim()
                val firstName = activity?.getPref(SharedPrefKeys.FIRST_NAME, "")

                logD("Amount - $amount")
                logD("First name - $firstName")
                logD("Email - $email")
                logD("Mobile Number -$mobileNumber")
                logD("Product - ${SharedPrefKeys.PRODUCT_RECHARGE_POINTS}")
                payUMoney.launchPayUMoney(amount?.toDouble()!!,firstName!!,mobileNumber!!,email!!, SharedPrefKeys.PRODUCT_RECHARGE_POINTS)
            }
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
         * @return A new instance of fragment AddPointsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                AddPointsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
