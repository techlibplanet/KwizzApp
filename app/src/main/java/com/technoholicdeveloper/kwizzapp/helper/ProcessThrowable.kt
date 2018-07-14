package com.technoholicdeveloper.kwizzapp.helper

import com.technoholicdeveloper.kwizzapp.models.Error
import net.rmitsolutions.mfexpert.lms.helpers.JsonHelper
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Created by Madhu on 24-Apr-2018
 */
object ProcessThrowable {
    fun getMessage(t: Throwable) = when (t) {
        is IOException -> if (t.message == null) "Failed to connect to server." else t.message
        is SocketTimeoutException -> if (t.message == null) "Request timeout." else t.message
        is HttpException -> {
            val err = t.response().errorBody()
            if (t.code() == 401) "Session expired"
            else if (err == null) "Request failed."
            else {
                try {
                    val jObjError = JSONObject(err.string())
                    val error = JsonHelper.jsonToKt<com.technoholicdeveloper.kwizzapp.models.Error>(jObjError.toString())
                    error.error_description
                } catch(ex: Exception) {
                    "Request failed."
                }
            }

        }
        else -> t.message ?: "Request failed."
    }

    fun getMessage(errorBody: ResponseBody?): String? {
        try {
            if (errorBody == null) return "Request failed"
            val jObjError = JSONObject(errorBody.string())
            val error = JsonHelper.jsonToKt<Error>(jObjError.toString())
            return error.error_description
        } catch(ex: Exception) {
            return "Request failed."
        }
    }
}