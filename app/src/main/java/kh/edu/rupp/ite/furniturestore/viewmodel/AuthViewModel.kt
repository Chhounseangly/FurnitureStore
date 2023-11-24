package kh.edu.rupp.ite.furniturestore.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.Login
import kh.edu.rupp.ite.furniturestore.model.api.model.Register
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kh.edu.rupp.ite.furniturestore.utility.AppPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    fun login(context: Context, email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val auth = RetrofitInstance.get().api.login(Login(email, password))
                auth.data?.let { AppPreference.get(context).setToken(it.token) }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("error", "${e.message}")

            }
        }
    }

    fun signUp(context: Context, name: String, email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val auth = RetrofitInstance.get().api.register(Register(name, email, password))
                auth.data?.let {
                    AppPreference.get(context).setToken(it.token)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("error", "${e.message}")
            }
        }
    }
}