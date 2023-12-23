package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kh.edu.rupp.ite.furniturestore.core.AppCore
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.AuthApiData
import kh.edu.rupp.ite.furniturestore.model.api.model.Login
import kh.edu.rupp.ite.furniturestore.model.api.model.Password
import kh.edu.rupp.ite.furniturestore.model.api.model.ResAuth
import kh.edu.rupp.ite.furniturestore.model.api.model.ResProfile
import kh.edu.rupp.ite.furniturestore.model.api.model.ResponseMessage
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.model.StatusAuth
import kh.edu.rupp.ite.furniturestore.model.api.model.User
import kh.edu.rupp.ite.furniturestore.model.api.model.ValidationTypes
import kh.edu.rupp.ite.furniturestore.model.api.model.VerifyEmailRequest
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kh.edu.rupp.ite.furniturestore.utility.AppPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class AuthViewModel : BaseViewModel<ResponseMessage>() {
    // LiveData and MutableLiveData declarations for various data associated with authentication and user actions
    private val _resAuth = MutableLiveData<AuthApiData<ResAuth>>()
    private val _userData = MutableLiveData<ApIData<User>>()
    private val _validationResult = MutableLiveData<Pair<Boolean, List<String>>>()
    private val _resMsg = MutableLiveData<ApIData<ResponseMessage>>()
    private val _validationVerify = MutableLiveData<Pair<Boolean, String>>()
    private val _updateMsg = MutableLiveData<ApIData<ResProfile>>()

    // Exposed LiveData properties for observing in the UI
    val validationVerify: LiveData<Pair<Boolean, String>> get() = _validationVerify
    val validationResult: LiveData<Pair<Boolean, List<String>>> = _validationResult
    val resMsg: LiveData<ApIData<ResponseMessage>> get() = _resMsg
    val resAuth: LiveData<AuthApiData<ResAuth>> get() = _resAuth
    val userData: LiveData<ApIData<User>> get() = _userData
    val updateMsg: LiveData<ApIData<ResProfile>> get() = _updateMsg

    /**
     * Function to handle validation and sign up.
     *
     * @param name The name input for sign-up.
     * @param email The email input for sign-up.
     * @param password The password input for sign-up.
     */
    fun signUp(
        name: String,
        email: String,
        password: String,
        cfPassword: String,
        avatar: MultipartBody.Part? = null
    ) {
        // Validate the input fields for sign-up
        val validationResult = validateInputs(
            ValidationTypes.SIGN_UP,
            name, email, password, cfPassword
        )

        // Update the LiveData with the validation result
        _validationResult.value = validationResult

        // If validation passes, submit the sign-up request to the API
        if (validationResult.first) {
            signUpService(name, email, password, cfPassword, avatar)
        }
    }

    /**
     * Function to handle validation and sign-in.
     *
     * @param email The email input for sign-in.
     * @param password The password input for sign-in.
     */
    fun signIn(email: String, password: String) {
        // Validate the input fields for sign-in
        val validationResult =
            validateInputs(
                ValidationTypes.SIGN_IN,
                "null",
                email,
                password,
                "null"
            ) // Use an empty string for username in sign-in
        _validationResult.value = validationResult

        // If validation passes, submit the sign-in request to the API
        if (validationResult.first) {
            loginService(email, password)
        }
    }

    /**
     * Function to verify email with a code.
     *
     * @param email The email input for verification.
     * @param code The verification code input.
     */
    fun verifyEmail(email: String, code: String) {
        // Validate the input for email verification
        val validateVerify = validationCodeInput(code)

        // Update the LiveData with the validation result
        _validationVerify.postValue(validateVerify)

        // If validation passes, submit the email verification request to the API
        if (validateVerify.first) {
            verifyEmailService(email, code)
        }
    }

    // Function to change password
    fun changePassword(current: String, new: String, confirm: String) {
        performApiCall(
            request = {
                RetrofitInstance.get().api.changePassword(Password(current, new, confirm))
            },
            successBlock = { data ->
                Log.e("AuthViewModel", "Update Success: $data")
                ApIData(Status.Success, data)
            },
            failureBlock = { response ->
                // Handle specific failure cases based on HTTP status code
                when (response.code()) {
                    // Handle 400 Bad Request error
                    400 -> {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        val errorResAuth = Gson().fromJson(errorBody, ResponseMessage::class.java)

                        Log.e("AuthViewModel", "Update Error: $errorBody")
                        ApIData(Status.Failed, errorResAuth)
                    }
                    // Handle other failure cases
                    else -> ApIData(Status.Failed, null)
                }
            }
        )
    }

    /**
     * Validates the input for an OTP (One-Time Password) code.
     *
     * @param code The OTP code input.
     * @return A Pair<Boolean, String> where the first element indicates
     *         whether the input is valid, and the second element is an
     *         error message if any validation errors occur.
     */
    private fun validationCodeInput(
        code: String
    ): Pair<Boolean, String> {
        // Initialize an empty error message
        var errorMsg = ""

        // Check if input is null or empty
        if (code.isEmpty()) {
            errorMsg = "This input is required"
        }

        // Check if input is not null or empty, and if it is exactly 6 characters long
        if (code.isNotEmpty()) {
            if (code.length != 6) {
                errorMsg = "This must be exactly 6 numbers"
            }
        }

        // Return the validation result as a Pair
        return Pair(errorMsg.isEmpty(), errorMsg)
    }

    /**
     * Validates if the given string is a well-formed email address.
     *
     * @param email The email address to be validated.
     * @return True if the email is valid, false otherwise.
     */
    private fun isValidEmail(email: String): Boolean {
        // Define a regular expression pattern for a valid email address
        val regex = Regex("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}\$")

        // Check if the provided email matches the defined pattern
        return regex.matches(email)
    }

    /**
     * Validates the input fields for a user registration.
     *
     * @param name The name input.
     * @param email The email input.
     * @param password The password input.
     * @return A Pair<Boolean, List<String>> where the first element indicates
     *         whether the inputs are valid, and the second element is a list of
     *         error messages if any validation errors occur.
     */
    private fun validateInputs(
        type: ValidationTypes,
        name: String?,
        email: String,
        password: String,
        cfPassword: String?
    ): Pair<Boolean, List<String>> {
        val errorMessages = mutableListOf<String>()
        // Common validations for both sign-in and sign-up
        // Check if email is provided
        if (email.isBlank()) {
            errorMessages.add("Email is required")
        } else if (!isValidEmail(email)) {
            errorMessages.add("Invalid Email")
        }

        // Check if password is provided
        if (password.isBlank()) {
            errorMessages.add("Password is required")
        } else if (password.length < 8 || password.length > 16) {
            errorMessages.add("Password must be at least 8 characters")
        }

        when (type) {
            ValidationTypes.SIGN_UP -> {
                // Check if name is provided
                if (name.isNullOrBlank()) {
                    errorMessages.add("Name is required")
                }

                // Check if name has at least 4 characters
                if ((name?.length ?: 0) < 4) {
                    errorMessages.add("Name must be at least 4 characters")
                }

                // Check if confirm password is provided
                if (cfPassword.isNullOrBlank()) {
                    errorMessages.add("Confirm password is required")
                }

                // Check if confirm password is not matched
                if (password != cfPassword) {
                    errorMessages.add("Confirm password is not matched")
                }

            }

            else -> {

            }
        }
        return Pair(errorMessages.isEmpty(), errorMessages)
    }


    /**
     * Function to handle login service with API.
     *
     * @param email The email of the user.
     * @param password The password for the user.
     */
    private fun loginService(email: String, password: String) {
        // Use the generic performApiCall function to handle the API call and response
        performAuthApiCall(
            // Make the API call to log in with the provided email and password
            request = { RetrofitInstance.get().api.login(Login(email, password)) },
            // Define the expected success HTTP status code for login
            successCode = 200,
            // Define the block to execute on success
            successBlock = { auth ->
                // Update the token in AppPreference if available
                auth.data?.let {
                    AppPreference.get(AppCore.get().applicationContext).setToken(it.token)
                }
                // Return success status with null data
                AuthApiData(StatusAuth.Success, null)
            },
            // Define the block to execute on failure
            failureBlock = { response ->
                // Handle specific failure cases based on HTTP status code
                when (response.code()) {
                    // Handle 400 Bad Request error
                    400 -> {
                        // Parse error response and return failure status with relevant data
                        val errorBody = response.errorBody()?.string()
                        val errorResAuth = Gson().fromJson(errorBody, ResAuth::class.java)
                        AuthApiData(
                            StatusAuth.Failed,
                            ResAuth(errorResAuth.message, errorResAuth.data)
                        )
                    }
                    // Handle 403 Forbidden error
                    403 -> AuthApiData(StatusAuth.NeedVerify, null)
                    // Handle other failure cases
                    else -> AuthApiData(StatusAuth.Failed, null)
                }
            }
        )
    }

    /**
     * Function to handle sign-up service with API.
     *
     * @param name The name of the user.
     * @param email The email of the user.
     * @param password The password for the user.
     */
    private fun signUpService(
        name: String,
        email: String,
        password: String,
        cfPassword: String,
        avatar: MultipartBody.Part?
    ) {
        // Use the generic performApiCall function to handle the API call and response
        performAuthApiCall(
            // Make the API call to register a new user with the provided credentials
            request = {
                val n = RequestBody.create(MediaType.parse("text/plain"), name)
                val e = RequestBody.create(MediaType.parse("text/plain"), email)
                val p = RequestBody.create(MediaType.parse("text/plain"), password)
                val cfP = RequestBody.create(MediaType.parse("text/plain"), cfPassword)

                RetrofitInstance.get().api.register(n, e, p, cfP, avatar)
            },
            // Define the expected success HTTP status code for registration
            successCode = 201,
            // Define the block to execute on success
            successBlock = {
                // Return success status with the need for email verification
                AuthApiData(StatusAuth.NeedVerify, null)
            },
            // Define the block to execute on failure
            failureBlock = { response ->
                // Handle specific failure cases based on HTTP status code
                when (response.code()) {
                    // Handle 400 Bad Request error
                    400 -> {
                        // Parse error response and return failure status with relevant data
                        val errorBody = response.errorBody()?.string()
                        val errorResAuth = Gson().fromJson(errorBody, ResAuth::class.java)
                        AuthApiData(
                            StatusAuth.Failed,
                            ResAuth(errorResAuth.message, errorResAuth.data)
                        )
                    }
                    // Handle other failure cases
                    else -> AuthApiData(StatusAuth.Failed, null)
                }
            }
        )
    }

    private fun verifyEmailService(email: String, code: String) {
        // Use the generic performApiCall function to handle the API call and response
        performAuthApiCall(
            // Make the API call to verify the email with the provided code
            request = { RetrofitInstance.get().api.verifyEmail(VerifyEmailRequest(email, code)) },
            // Define the expected success HTTP status code
            successCode = 200,
            // Define the block to execute on success
            successBlock = { auth ->
                // Update the token in AppPreference if available
                auth.data?.let {
                    AppPreference.get(AppCore.get().applicationContext).setToken(it.token)
                }
                // Return success status with null data
                AuthApiData(StatusAuth.Success, null)
            },
            // Define the block to execute on failure
            failureBlock = { response ->
                // Handle specific failure cases based on HTTP status code
                when (response.code()) {
                    // Handle 403 to 403 errors (inclusive)
                    in 403 until 404 -> {
                        // Parse error response and return failure status with relevant data
                        val errorBody = response.errorBody()?.string()
                        val errorResAuth = Gson().fromJson(errorBody, ResAuth::class.java)
                        AuthApiData(StatusAuth.Failed, ResAuth(errorResAuth.message, null))
                    }
                    // Handle other failure cases
                    else -> AuthApiData(StatusAuth.Failed, null)
                }
            }
        )
    }


    // Function to handle loading profile from API
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

    // Function to handle logging out user and clearing token
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

    // Function to handle updating profile data
    fun updateProfile(name: RequestBody, avatar: MultipartBody.Part? = null) {
        var resMessage = ApIData<ResProfile>(Status.Processing, null)
        _updateMsg.postValue(resMessage)

        viewModelScope.launch(Dispatchers.IO) {
            resMessage = try {
                RetrofitInstance.get().api.updateProfile(name, avatar)
                Log.e("AuthViewModel", "Success")
                ApIData(Status.Success, null)
            } catch (e: Exception) {
                Log.e("AuthViewModel", e.message.toString())
                ApIData(Status.Failed, null)
            }

            withContext(Dispatchers.Main.immediate) {
                _updateMsg.postValue(resMessage)
            }
        }
    }

    /**
     * A generic function to perform API calls asynchronously and handle the response.
     *
     * @param T The type of the response body.
     * @param request A suspend function representing the API call.
     * @param successCode The expected HTTP status code for a successful response.
     * @param successBlock A block to execute when the response is successful.
     * @param failureBlock A block to execute when the response is unsuccessful.
     */
    private fun <T> performAuthApiCall(
        request: suspend () -> Response<T>,
        successCode: Int,
        successBlock: (T) -> AuthApiData<ResAuth>,
        failureBlock: (Response<T>) -> AuthApiData<ResAuth>
    ) {
        // Create an initial AuthApiData with Processing status
        var responseData = AuthApiData<ResAuth>(StatusAuth.Processing, null)

        // Set the initial value in the ViewModel
        _resAuth.value = responseData

        // Launch a coroutine in the IO dispatcher
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Make the API call
                val response = request.invoke()

                // Check the HTTP status code of the response
                responseData = when (response.code()) {
                    // If the status code is the expected success code, execute the success block
                    successCode -> successBlock(response.body()!!)
                    // If the status code is not the expected success code, execute the failure block
                    else -> failureBlock(response)
                }
                Log.d("AuthViewModel", "responseData: $responseData")
            } catch (e: Exception) {
                // Handle exceptions, e.g., network issues
                Log.e("AuthViewModel", e.message.toString())
                // Set failure status in case of an exception
                responseData = AuthApiData(StatusAuth.Failed, null)
            }

            // Update the LiveData in the Main dispatcher after the API call completion
            withContext(Dispatchers.Main.immediate) {
                _resAuth.value = responseData
            }
        }
    }
}
