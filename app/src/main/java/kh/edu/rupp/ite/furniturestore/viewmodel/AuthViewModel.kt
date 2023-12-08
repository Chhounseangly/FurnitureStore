package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
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
    private val _validationResult = MutableLiveData<Pair<Boolean, List<String>>>()
    private val _resMsg = MutableLiveData<ApIData<ResponseMessage>>()

    val validationResult: LiveData<Pair<Boolean, List<String>>> = _validationResult

    val resMsg: LiveData<ApIData<ResponseMessage>>
        get() = _resMsg

    // Expose as LiveData
    val resAuth: LiveData<ApIData<ResAuth>>
        get() = _resAuth

    val userData: LiveData<ApIData<User>>
        get() = _userData


    //handle validation and sign up
    fun signUp(name: String, email: String, password: String) {
        val validationResult = validateInputs(name, email, password)
        _validationResult.value = validationResult
        //validation valid submit to api
        if (validationResult.first) {
            signUpService(name, email, password)
        }
    }

    //handle validation and sign in
    fun signIn(email: String, password: String) {
        val validationResult =
            validateInputs("null", email, password) // Use an empty string for username in sign-in
        _validationResult.value = validationResult
        if (validationResult.first) {
            loginService(email, password)
        }
    }

    //validation email
    private fun isValidEmail(email: String): Boolean {
        val regex = Regex("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}\$")
        return regex.matches(email)
    }

    //validation of input filed
    private fun validateInputs(
        name: String,
        email: String,
        password: String
    ): Pair<Boolean, List<String>> {
        val errorMessages = mutableListOf<String>()

        // Check if username is not null
        if (name.isEmpty()) {
            errorMessages.add("Name is required")
        }

        if (name.isNotEmpty()) {
            if (name.length < 4) {
                errorMessages.add("Name must be at least 4 characters")
            }
        }

        if (email.isEmpty()) {
            errorMessages.add("Email is required")
        }

        if (email.isNotEmpty()) {
            if (!isValidEmail(email)) {
                errorMessages.add("Invalided email address")
            }
        }

        if (password.isEmpty()) {
            errorMessages.add("Password is required")
        }

        if (password.isNotEmpty()) {
            if (password.length < 8) {
                errorMessages.add("Passwords must be at least 8 characters")

            }
        }

        return Pair(errorMessages.isEmpty(), errorMessages)
    }


    //handle Login service with api
    private fun loginService(email: String, password: String) {
        var responseData = ApIData<ResAuth>(Status.Processing, null)
        _resAuth.value = responseData
        viewModelScope.launch(Dispatchers.IO) {
            responseData = try {
                val response = RetrofitInstance.get().api.login(Login(email, password))
                val auth = response.body()
                when (response.code()) {
                    200 -> {
                        if (auth != null) {
                            auth.data?.let {
                                AppPreference.get(AppCore.get().applicationContext)
                                    .setToken(it.token)
                            }
                            ApIData(Status.Success, null)
                        } else {
                            ApIData(Status.Failed, null)
                        }
                    }

                    400 -> {
                        val errorBody = response.errorBody()?.string()
                        val errorResAuth = Gson().fromJson(errorBody, ResAuth::class.java)
                        ApIData(Status.Failed, ResAuth(errorResAuth.message, errorResAuth.data))
                    }

                    else -> {
                        ApIData(Status.Failed, null)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ApIData(Status.Failed, null)
            }
            withContext(Dispatchers.Main.immediate) {
                _resAuth.value = responseData
            }
        }
    }

    //handle SignUp service with api
    private fun signUpService(name: String, email: String, password: String) {
        var responseData = ApIData<ResAuth>(Status.Processing, null)
        _resAuth.value = responseData
        viewModelScope.launch(Dispatchers.IO) {
            responseData = try {
                val response = RetrofitInstance.get().api.register(Register(name, email, password))
                val auth = response.body()
                when (response.code()) {
                    201 -> {
                        if (auth != null) {
                            auth.data?.let {
                                AppPreference.get(AppCore.get().applicationContext)
                                    .setToken(it.token)
                            }
                            ApIData(Status.Success, null)
                        } else ApIData(Status.Failed, null)
                    }

                    400 -> {
                        val errorBody = response.errorBody()?.string()
                        val errorResAuth = Gson().fromJson(errorBody, ResAuth::class.java)
                        ApIData(Status.Failed, ResAuth(errorResAuth.message, errorResAuth.data))
                    }

                    else -> {
                        ApIData(Status.Failed, null)

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ApIData(Status.Failed, null)
            }

            withContext(Dispatchers.Main.immediate) {
                _resAuth.value = responseData
            }
        }
    }

    //handle load profile from api
    fun loadProfile() {
        var responseData = ApIData<User>(Status.Processing, null)
        _userData.value = responseData

        viewModelScope.launch(Dispatchers.IO) {
            responseData = try {
                val data = RetrofitInstance.get().api.loadProfile()
                ApIData(Status.Success, data.data)
            } catch (e: Exception) {
                AppPreference.get(AppCore.get().applicationContext).removeToken()
                e.printStackTrace()
                ApIData(Status.Failed, null)
            }
            withContext(Dispatchers.Main.immediate) {
                _userData.value = responseData
            }
        }
    }

    //handle logout user and clear token
    fun logout() {
        var responseData = ApIData<ResponseMessage>(Status.Processing, null)
        _resMsg.postValue(responseData)
        viewModelScope.launch(Dispatchers.IO) {
            responseData = try {
                RetrofitInstance.get().api.logout()
                AppPreference.get(AppCore.get().applicationContext).removeToken()
                ApIData(Status.Success, null)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Error", "${e.message}")
                AppPreference.get(AppCore.get().applicationContext).removeToken()
                ApIData(Status.Failed, null)
            }
            withContext(Dispatchers.Main.immediate) {
                _resMsg.postValue(responseData)
            }
        }
    }

    //handle update data of profile
    fun updateProfile(name: String, avatar: String?) {
//        var resMessage = ApIData<ResponseMessage>(Status.Processing, null)
//        _resMsg.postValue(resMessage)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = RetrofitInstance.get().api.updateProfile(
                    UpdateProfile(name, avatar)
                )
//                    loadProfile()
                Log.d("name", "${res.data}")
                ApIData(Status.Success, null);

            } catch (e: Exception) {
                Log.e("error", "${e.message}")
                ApIData(Status.Failed, null);
            }

            withContext(Dispatchers.Main.immediate) {
//                _resMsg.postValue(resMessage)
            }
        }
    }
}