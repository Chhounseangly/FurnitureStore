package kh.edu.rupp.ite.furniturestore.utility

import android.content.Context
import android.content.SharedPreferences


class AppPreference private constructor(context: Context){

    private lateinit var pref: SharedPreferences

    init {
        pref = context.getSharedPreferences("myapp", Context.MODE_PRIVATE)
    }


    fun setToken(token: String) {
        pref.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return pref.getString(KEY_TOKEN, null)
    }

    fun removeToken() {
        pref.edit().remove(KEY_TOKEN).apply()
    }

    companion object {

        private const val KEY_TOKEN = "token"

        private var instance: AppPreference? = null

        fun get(context: Context): AppPreference {
            if (instance == null) {
                instance = AppPreference(context)
            }

            return instance!!
        }

    }

}
