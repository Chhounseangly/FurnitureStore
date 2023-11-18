package kh.edu.rupp.ite.furniturestore.utility

import android.content.Context
import android.content.SharedPreferences

class AppPreference private constructor(context: Context) {

    private var pref: SharedPreferences

    init {
        pref = context.getSharedPreferences("myApp", Context.MODE_PRIVATE)
    }

    fun setToken(token: String) {
        pref.edit().putString(TOKEN, token).apply()
    }

    fun getToken(): String? {
        return pref.getString(TOKEN, null)
    }

    companion object {
        private const val TOKEN = "token"

        private var instance: AppPreference? = null

        fun get(context: Context): AppPreference {
            if (instance == null) instance = AppPreference(context)
            return instance!!
        }
    }
}