package com.technoholicdeveloper.kwizzapp.wallet

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import com.example.mayank.googleplaygame.network.wallet.Itransaction
import com.example.mayank.googleplaygame.network.wallet.Transactions
import com.example.mayank.myplaygame.network.ApiClient
import com.payumoney.core.entity.TransactionResponse
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager
import com.payumoney.sdkui.ui.utils.ResultModel
import com.technoholicdeveloper.kwizzapp.R
import net.rmitsolutions.mfexpert.lms.helpers.*
import org.jetbrains.anko.find
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WalletActivity : AppCompatActivity(),
        AddPointsFragment.OnFragmentInteractionListener,
        WithdrawalPointsFragment.OnFragmentInteractionListener,
        TransferPointsFragment.OnFragmentInteractionListener,
        WalletMenuFragment.OnFragmentInteractionListener {

    private val TAG = WalletActivity::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        val walletMenuFragment = WalletMenuFragment()
        switchToFragment(walletMenuFragment)
    }




    override fun onFragmentInteraction(uri: Uri) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result Code is -1 send from Payumoney activity
        Log.d(TAG, "request code $requestCode resultCode $resultCode")

        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == AppCompatActivity.RESULT_OK && data !=
                null) {
//            var transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
            var transactionResponse = data.getParcelableExtra<TransactionResponse>(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE)

            var resultModel = data.getParcelableExtra<ResultModel>(PayUmoneyFlowManager.ARG_RESULT);


            // Response from Payumoney
            val payuResponse = transactionResponse.getPayuResponse();

            // Check which object is non-null
            if (payuResponse != null) {
                if (transactionResponse.transactionStatus == TransactionResponse.TransactionStatus.SUCCESSFUL) {
                    //Success Transaction
                    logD("Successfull transaction")

                    Log.d(TAG, "Pay u Response - $payuResponse")

                    if (payuResponse!=""){
                        val response = JSONObject(payuResponse)     // Done
                        val result = response.getJSONObject("result")   // Done
                        val status = result.getString("status")     // Done
                        val paymentId = result.getString("paymentId")
                        val txnId = result.getString("txnid")
                        val amount = result.getString("amount")
                        val addedOn = result.getString("addedon")
                        val createdOn = result.getString("createdOn")
                        val productInfo = result.getString("productinfo")
                        val firstName = result.getString("firstname")
//                        val lastName = result.getString("lastname")
                        val lastName = getPref(SharedPrefKeys.LAST_NAME, "")
                        val email = result.getString("email")
                        val mobileNumber = result.getString("phone")
                        val bankRefNumber = result.getString("bank_ref_num")
                        val bankCode = result.getString("bankcode")

                        // displayName = playGameLib?.getDisplayName()!!
                        updateTransactionDetails(firstName, lastName!!, "Display Name", mobileNumber, "", "",
                                email, productInfo, amount, txnId, paymentId, addedOn, createdOn, bankRefNumber, bankCode, "Credited", status)

                        logD("Transaction status - $status")
                    }else{
                        logD("Payu response is null")
                    }

                } else {
                    //Failure Transaction
                    logD( "Failed transaction")
                }




                // Response from SURl and FURL
                //val merchantResponse = transactionResponse.transactionDetails
                //Log.d(TAG, "Merchant Response - $merchantResponse")



//                AlertDialog.Builder(this)
//                        .setCancelable(false)
//                        .setMessage("Payu's Data : $payuResponse\n\n\n Merchant's Data: $merchantResponse")
//                        .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, whichButton ->
//                            dialog.dismiss()
//                        })

            } else if (resultModel?.error != null) {
                Log.d(TAG, "Error response : " + resultModel.error.transactionResponse);
            } else {
                Log.d(TAG, "Both objects are null!");
            }
        }
    }


    private fun updateTransactionDetails(firstName : String, lastName : String, playGameName : String, mobileNumber : String, transferTo : String, receivedFrom: String,
                                         email : String, productInfo : String, amount : String,txnId : String, paymentId : String, addedOn : String, createdOn : String,
                                         bankRefNumber : String, bankCode : String, transactionType : String, status : String){
        val apiClient = ApiClient()
        var retrofit = apiClient.getService<Itransaction>()
        retrofit.addTransactionDetails(firstName, lastName, playGameName,mobileNumber, transferTo, receivedFrom, email, productInfo,
                amount, txnId, paymentId, addedOn, createdOn, bankRefNumber, bankCode, transactionType, status).enqueue(object : Callback<Transactions> {
            override fun onFailure(call: Call<Transactions>?, t: Throwable?) {
                Log.d(TAG, "Error : $t")
                showDialog(this@WalletActivity, "Error", "$t")
            }

            override fun onResponse(call: Call<Transactions>?, response: Response<Transactions>?) {
                Log.d(TAG, "Response - $response")
                val responseBody = response?.body()
                Log.d(TAG, "Mobile Number - ${responseBody?.mobileNumber}")
                Log.d(TAG, "Amount : ${responseBody?.balance}")
                val walletMenuFragment = WalletMenuFragment()
                switchToFragment(walletMenuFragment)
                showDialog(this@WalletActivity, "Add Points", "Points added successfully!\n\n Balance : ${responseBody?.balance}")

            }

        })
    }

}
