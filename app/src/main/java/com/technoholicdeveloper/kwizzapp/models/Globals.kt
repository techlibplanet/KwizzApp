package com.technoholicdeveloper.kwizzapp.models

class Globals {

    companion object {

        fun isEmptyString(s: String?): Boolean {
            return s == null || s.trim { it <= ' ' }.isEmpty()
        }
    }
}