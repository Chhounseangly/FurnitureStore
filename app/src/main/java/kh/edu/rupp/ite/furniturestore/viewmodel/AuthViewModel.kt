package kh.edu.rupp.ite.furniturestore.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.core.AppCore
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.Login
import kh.edu.rupp.ite.furniturestore.model.api.model.Register
import kh.edu.rupp.ite.furniturestore.model.api.model.ResAuth
import kh.edu.rupp.ite.furniturestore.model.api.model.ResponseMessage
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.model.UpdateProfile
import kh.edu.rupp.ite.furniturestore.model.api.model.User
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kh.edu.rupp.ite.furniturestore.utility.AppPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel : ViewModel() {

    private val _resAuth = MutableLiveData<ApIData<ResAuth>>()
    private val _userData = MutableLiveData<ApIData<User>>()

    private val _resMsg = MutableLiveData<ApIData<ResponseMessage>>()

    val resMsg: LiveData<ApIData<ResponseMessage>>
        get() = _resMsg



    // Expose as LiveData
    val resAuth: LiveData<ApIData<ResAuth>>
        get() = _resAuth

    val userData: LiveData<ApIData<User>>
        get() = _userData

    fun login(context: Context, email: String, password: String) {
        var responseData = ApIData<ResAuth>(Status.Processing, null)
        _resAuth.value = responseData
        viewModelScope.launch(Dispatchers.IO) {
            responseData = try {
                val auth = RetrofitInstance.get().api.login(Login(email, password))
                auth.data?.let { AppPreference.get(context).setToken(it.token) }
                ApIData(Status.Success, null)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("error", "${e.message}")
                ApIData(Status.Failed, null)
            }
            withContext(Dispatchers.Main.immediate) {
                _resAuth.value = responseData
            }
        }
    }

    fun signUp(context: Context, name: String, email: String, password: String) {
        var responseData = ApIData<ResAuth>(Status.Processing, null)
        _resAuth.value = responseData
        viewModelScope.launch(Dispatchers.IO) {
            responseData = try {
                val auth = RetrofitInstance.get().api.register(Register(name, email, password))
                auth.data?.let {
                    AppPreference.get(context).setToken(it.token)
                }
                ApIData(Status.Success, null)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("error", "${e.message}")
                ApIData(Status.Failed, null)
            }

            withContext(Dispatchers.Main.immediate) {
                _resAuth.value = responseData
            }
        }
    }

    fun loadProfile() {
        val token = AppPreference.get(AppCore.get().applicationContext).getToken()
        var responseData = ApIData<User>(Status.Processing, null)
        _userData.value = responseData

        viewModelScope.launch(Dispatchers.IO) {
            responseData = try {
                if (token != null) {
                    val authToken = "Bearer $token"
                    val data = RetrofitInstance.get().api.loadProfile(authToken)
                    ApIData(Status.Success, data.data)
                } else {
                    ApIData(Status.Failed, null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Error", "${e.message}")
//                AppPreference.get(AppCore.get().applicationContext).removeToken()
                ApIData(Status.Failed, null)
            }
            withContext(Dispatchers.Main.immediate) {
                _userData.value = responseData
            }
        }
    }

    fun logout(){
        val token = AppPreference.get(AppCore.get().applicationContext).getToken()
        var responseData = ApIData<ResponseMessage>(Status.Processing, null)
        _resMsg.postValue(responseData)
        viewModelScope.launch(Dispatchers.IO) {
            responseData = try {
                if (token != null) {
                    val authToken = "Bearer $token"
                    val data = RetrofitInstance.get().api.logout(authToken)
                    AppPreference.get(AppCore.get().applicationContext).removeToken()
                    ApIData(Status.Success, null)
                } else {
                    ApIData(Status.Failed, null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Error", "${e.message}")
                ApIData(Status.Failed, null)
            }
            withContext(Dispatchers.Main.immediate) {
                _resMsg.postValue(responseData)
            }
        }
    }


    fun updateProfile(name: String, avatar: String?){
        val token = AppPreference.get(AppCore.get().applicationContext).getToken()
//        var resMessage = ApIData<ResponseMessage>(Status.Processing, null)
//        _resMsg.postValue(resMessage)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (token != null){
                    val authToken = "Bearer $token"
                    val res = RetrofitInstance.get().api.updateProfile(authToken, UpdateProfile(name, avatar))
//                    loadProfile()
                    Log.d("name", "${res.data}")
                    ApIData(Status.Success, null);
                } else {
                    ApIData(Status.Failed, null)
                }

            }catch (e: Exception){
                Log.e("error", "${e.message}")
                ApIData(Status.Failed, null);
            }

            withContext(Dispatchers.Main.immediate){
//                _resMsg.postValue(resMessage)
            }
        }

    }
}