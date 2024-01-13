package kh.edu.rupp.ite.furniturestore.utility

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


class AppPreference private constructor(context: Context) {

    private var pref: SharedPreferences
    private var encryptedPref: SharedPreferences

    init {
        // Create an instance of SharedPreferences
        pref = context.getSharedPreferences("myapp", Context.MODE_PRIVATE)

        // Create an instance of MasterKey
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        // Create an instance of EncryptedSharedPreferences
        encryptedPref = EncryptedSharedPreferences.create(
            context,
            "myapp",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun setToken(token: String) {
        encryptedPref.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return encryptedPref.getString(KEY_TOKEN, null)
    }

    fun removeToken() {
        encryptedPref.edit().remove(KEY_TOKEN).apply()
    }

    fun setLanguage(language: String) {
        pref.edit().putString(KEY_LANGUAGE, language).apply()
    }

    fun getLanguage(): String? {
        return pref.getString(KEY_LANGUAGE, null)
    }

    fun setTheme(theme: Boolean){
        pref.edit().putBoolean(KEY_THEMES, theme).apply()
    }
    fun getTheme(): Boolean {
        return pref.getBoolean(KEY_THEMES, false)
    }

    companion object {

        private const val KEY_TOKEN = "token"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_THEMES = "themes"

        private var instance: AppPreference? = null

        fun get(context: Context): AppPreference {
            if (instance == null) {
                instance = AppPreference(context)
            }

            return instance!!
        }
    }
}