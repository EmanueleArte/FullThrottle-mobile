package com.example.fullthrottle

import android.text.TextUtils
import java.util.regex.Matcher
import java.util.regex.Pattern

object Utils {

    private const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$"

    fun isValidPassword(password: String): Boolean {
        val pattern = Pattern.compile(PASSWORD_PATTERN)
        val matcher = pattern.matcher(password)
        return matcher.matches()
    }

    fun isValidMail(mail: String): Boolean {
        return !TextUtils.isEmpty(mail) && android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }

    fun isValidUsername(username: String): Boolean {
        return !TextUtils.isEmpty(username)
    }
}