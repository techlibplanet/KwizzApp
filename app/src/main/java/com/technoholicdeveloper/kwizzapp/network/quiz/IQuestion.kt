package com.example.mayank.myplaygame.network

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Mayank on 21/02/2018.
 */
interface IQuestion {

    @FormUrlEncoded
    @POST("insert_question.php")
    fun insertQuestion(
            @Field("quesId") quesID: String,
            @Field("question") question: String,
            @Field("optionA") optionA: String,
            @Field("optionB") optionB: String,
            @Field("optionC") optionC: String,
            @Field("optionD") optionD: String,
            @Field("optionE") optionE: String,
            @Field("answer") answer: String): Call<Question>


    @FormUrlEncoded
    @POST("quiz/getQuestions.php")
    fun getQuestion(
            @Field("quesId") questionID: String,
            @Field("tableName") tableName : String): Call<Question>


    @FormUrlEncoded
    @POST("quiz/getNumberOfRows.php")
    fun getNumberOfRows(@Field("tableName") tableName: String): Call<Question>


}