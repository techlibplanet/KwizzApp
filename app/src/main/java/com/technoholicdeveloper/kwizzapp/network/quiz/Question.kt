package com.example.mayank.myplaygame.network

import com.google.gson.annotations.SerializedName

/**
 * Created by Mayank on 21/02/2018.
 */
class Question{
    @SerializedName("quesId")
    var quesCode: String? = null

    @SerializedName("question")
    var question: String? = null

    @SerializedName("optionA")
    var optionA: String? = null

    @SerializedName("optionB")
    var optionB: String? = null

    @SerializedName("optionC")
    var optionC: String? = null

    @SerializedName("optionD")
    var optionD: String? = null

    @SerializedName("optionE")
    var optionE: String? = null

    @SerializedName("answer")
    var answer: String? = null

    @SerializedName("numberOfRows")
    var numberOfRows: Int? = null

    @SerializedName("error")
    var error: String? = null


}