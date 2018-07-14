package com.technoholicdeveloper.kwizzapp.userInfo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.mayank.googleplaygame.network.wallet.Itransaction
import com.example.mayank.googleplaygame.network.wallet.Transactions
import com.example.mayank.myplaygame.network.ApiClient

import com.technoholicdeveloper.kwizzapp.R
import com.technoholicdeveloper.kwizzapp.dashboard.DashboardFragment
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
 * [UserInformationFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [UserInformationFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class UserInformationFragment : Fragment(), View.OnClickListener {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private lateinit var inputMobileNumber : TextInputEditText
    private lateinit var inputEmail : TextInputEditText
    private var mobileNumber : String ?= null
    private var email : String? = null

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
        val view =inflater.inflate(R.layout.fragment_user_information, container, false)
        inputMobileNumber = view.find(R.id.editTextMobileNumber)
        inputEmail = view.find(R.id.editTextEmail)
        view.find<Button>(R.id.buttonAddInformation).setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.buttonAddInformation ->{
                mobileNumber = inputMobileNumber.text.toString().trim()
                email = inputEmail.text.toString().trim()
                val displayName = activity?.getPref(SharedPrefKeys.DISPLAY_NAME, "")
                val apiClient = ApiClient()
                var retrofit = apiClient.getService<Itransaction>()
                retrofit.addDetails("$mobileNumber", "$email", "$displayName").enqueue(object : Callback<Transactions> {
                    override fun onFailure(call: Call<Transactions>?, t: Throwable?) {
                        showDialog(activity!!, "Error", "$t")
                    }

                    override fun onResponse(call: Call<Transactions>?, response: Response<Transactions>?) {
                        if (response?.isSuccessful!!){
                            logD("Response - $response")
                            logD("Response body - ${response.body()?.result}")
                            if (response.body()?.result == "success"){
                                saveDetailToPrefs()
                            }else if(response.body()?.error == "Mobile number already registered!"){
                                logD("${response.body()?.error}")
                                //AlertDialog.alertDialog(activity!!, "Warning", "Mobile Number already registered !")
                                saveDetailToPrefs()

                            }else {
                                showDialog(activity!!, "Error", "${response.body()?.error}")
                            }
                        }
                    }
                })
            }
        }
    }

    private fun saveDetailToPrefs(){
        activity?.putPref(SharedPrefKeys.EMAIL, email!!)
        activity?.putPref(SharedPrefKeys.MOBILE_NUMBER, mobileNumber!!)
        val dashboardFragment = DashboardFragment()
        switchToFragment(activity!!,dashboardFragment)
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
         * @return A new instance of fragment UserInformationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                UserInformationFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
