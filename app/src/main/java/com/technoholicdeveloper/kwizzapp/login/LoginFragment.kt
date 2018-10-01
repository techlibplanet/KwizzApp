package com.technoholicdeveloper.kwizzapp.login

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.SyncStateContract
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton

import com.technoholicdeveloper.kwizzapp.R
import com.technoholicdeveloper.kwizzapp.helper.Constants
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.find
import kotlin.concurrent.thread

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [LoginFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class LoginFragment : Fragment(), View.OnClickListener {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private lateinit var downToTop: Animation
    private lateinit var topToDown : Animation
    private lateinit var leftToRight : Animation
    private lateinit var rightToLeft : Animation
    private  var signInButton : SignInButton? =null
    private var appLogo : ImageView? = null
    private var appName : TextView? = null

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
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        signInButton = view.find(R.id.button_sign_in)
        appLogo = view.find(R.id.imageViewAppLogo)
        appName = view.find(R.id.textViewAppName)
        downToTop = AnimationUtils.loadAnimation(activity, R.anim.down_to_top)
        topToDown = AnimationUtils.loadAnimation(activity, R.anim.top_to_down)
        leftToRight = AnimationUtils.loadAnimation(activity, R.anim.left_to_right)
        rightToLeft = AnimationUtils.loadAnimation(activity, R.anim.right_to_left)

        appLogo?.animation = topToDown
        appName?.animation = topToDown
        signInButton?.animation = downToTop


        signInButton?.setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.button_sign_in ->{
                startSignInIntent()
            }
        }
    }

    private fun startSignInIntent() {
        val signInClient = GoogleSignIn.getClient(activity!!,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)

        val intent = signInClient.signInIntent
        startActivityForResult(intent, Constants.RC_SIGN_IN)
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
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                LoginFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
