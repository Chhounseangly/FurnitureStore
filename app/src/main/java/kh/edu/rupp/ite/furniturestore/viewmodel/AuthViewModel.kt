package kh.edu.rupp.ite.furniturestore.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.Login
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kh.edu.rupp.ite.furniturestore.utility.AppPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {

    fun login(context: Context, username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val auth = RetrofitInstance.get().api.login(Login(username, password))
                auth.data?.let { AppPreference.get(context).setToken(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}