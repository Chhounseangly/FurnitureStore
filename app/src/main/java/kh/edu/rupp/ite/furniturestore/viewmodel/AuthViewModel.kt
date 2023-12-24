package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kh.edu.rupp.ite.furniturestore.core.AppCore
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiData
import kh.edu.rupp.ite.furniturestore.model.api.model.Login
import kh.edu.rupp.ite.furniturestore.model.api.model.Password
import kh.edu.rupp.ite.furniturestore.model.api.model.Res
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.model.Token
import kh.edu.rupp.ite.furniturestore.model.api.model.User
import kh.edu.rupp.ite.furniturestore.model.api.model.ValidationTypes
import kh.edu.rupp.ite.furniturestore.model.api.model.VerifyEmailRequest
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kh.edu.rupp.ite.furniturestore.utility.AppPreference
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AuthViewModel : BaseViewModel() {
    // LiveData and MutableLiveData declarations for various data associated with authentication and user actions
    private val _resAuth = MutableLiveData<ApiData<Res<Token>>>()
    private val _userData = MutableLiveData<ApiData<Res<User>>>()
    private val _validationResult = MutableLiveData<Pair<Boolean, List<String>>>()
    private val _resMsg = MutableLiveData<ApiData<Res<String>>>()
    private val _validationVerify = MutableLiveData<Pair<Boolean, String>>()
    private val _updateMsg = MutableLiveData<ApiData<Res<User>>>()

    // Exposed LiveData properties for observing in the UI
    val validationVerify: LiveData<Pair<Boolean, String>> get() = _validationVerify
    val validationResult: LiveData<Pair<Boolean, List<String>>> = _validationResult
    val resMsg: LiveData<ApiData<Res<String>>> get() = _resMsg
    val resAuth: LiveData<ApiData<Res<Token>>> get() = _resAuth
    val userData: LiveData<ApiData<Res<User>>> get() = _userData
    val updateMsg: LiveData<ApiData<Res<User>>> get() = _updateMsg

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

    /**
     * Function to handle login service with API.
     *
     * @param email The email of the user.
     * @param password The password for the user.
     */
    private fun loginService(email: String, password: String) {
        // Use the generic performApiCall function to handle the API call and response
        performApiCall(
            response = _resAuth,
            // Make the API call to log in with the provided email and password
            request = { RetrofitInstance.get().api.login(Login(email, password)) },
            // Define the block to execute on success
            successBlock = { auth ->
                // Update the token in AppPreference if available
                auth.data?.let {
                    AppPreference.get(AppCore.get().applicationContext).setToken(it.token)
                }
                // Return success status with null data
                ApiData(Status.Success, null)
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
        performApiCall(
            response = _resAuth,
            request = {
                val n = RequestBody.create(MediaType.parse("text/plain"), name)
                val e = RequestBody.create(MediaType.parse("text/plain"), email)
                val p = RequestBody.create(MediaType.parse("text/plain"), password)
                val cfP = RequestBody.create(MediaType.parse("text/plain"), cfPassword)

                RetrofitInstance.get().api.register(n, e, p, cfP, avatar)
            }
        )
    }

    private fun verifyEmailService(email: String, code: String) {
        performApiCall(
            response = _resAuth,
            request = { RetrofitInstance.get().api.verifyEmail(VerifyEmailRequest(email, code)) },
            successBlock = { auth ->
                auth.data?.let {
                    AppPreference.get(AppCore.get().applicationContext).setToken(it.token)
                }
                ApiData(Status.Success, null)
            }
        )
    }

    // Function to handle loading profile from API
    fun loadProfile() {
        performApiCall(
            response = _userData,
            request = { RetrofitInstance.get().api.loadProfile() },
            failureBlock = { err ->
                Log.e("AuthViewModel", "Update Fail: $err")
                AppPreference.get(AppCore.get().applicationContext).removeToken()
                ApiData(Status.Failed, null)
            }
        )
    }

    // Function to handle logging out user and clearing token
    fun logout() {
        performApiCall(
            response = _resMsg,
            request = { RetrofitInstance.get().api.logout() },
            successBlock = {
                AppPreference.get(AppCore.get().applicationContext).removeToken()
                ApiData(Status.Success, null)
            },
            failureBlock = { err ->
                Log.e("AuthViewModel", "Update Fail: $err")
                AppPreference.get(AppCore.get().applicationContext).removeToken()
                ApiData(Status.Failed, null)
            }
        )
    }

    // Function to handle updating profile data
    fun updateProfile(name: RequestBody, avatar: MultipartBody.Part? = null) {
        performApiCall(
            response = _updateMsg,
            request = { RetrofitInstance.get().api.updateProfile(name, avatar) }
        )
    }

    // Function to change password
    fun changePassword(current: String, new: String, confirm: String) {
        performApiCall(
            response = _resMsg,
            request = {
                RetrofitInstance.get().api.changePassword(Password(current, new, confirm))
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
}
