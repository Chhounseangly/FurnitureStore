package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kh.edu.rupp.ite.furniturestore.BuildConfig
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivitySignInBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.StatusAuth
import kh.edu.rupp.ite.furniturestore.view.activity.MainActivity
import kh.edu.rupp.ite.furniturestore.view.activity.validation.AuthValidation
import kh.edu.rupp.ite.furniturestore.viewmodel.AuthViewModel

class SignInActivity : AuthActivity<ActivitySignInBinding>(ActivitySignInBinding::inflate) {
    private val email: EditText by lazy { binding.emInput }
    private val password: EditText by lazy { binding.pwInput }
    private val errorMessage: TextView by lazy { binding.errorMsg }

    private val signInBtn: Button by lazy { binding.signInBtn }
    private val signUpBtn: TextView by lazy { binding.signUpBtn }
    private val forgotPassBtn: TextView by lazy { binding.forgotPwBtn }

    private var authViewModel = AuthViewModel()

    override fun initActions() {
        //call method handle Sign In Processing
        handleSignInProcession()

        //return to prev activity
        prevBack(binding.backBtn)
        setupPasswordToggle(password)
        togglePasswordVisibility(password)

        // Set up navigation between EditTexts
        navigationBetweenEditTexts(email, password)
        navigationBetweenEditTexts(password, null) {
            handleSignIn()
        }
    }

    private fun setupPasswordToggle(passwordEditText: EditText) {
        val touchListener: (View, MotionEvent) -> Boolean = setOnTouchListener@{ _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = passwordEditText.compoundDrawables[2]
                if (event.rawX >= (passwordEditText.right - drawableEnd.bounds.width())) {
                    togglePasswordVisibility(password)
                    return@setOnTouchListener true
                }
            }
            false
        }

        passwordEditText.run { setOnTouchListener(touchListener) }
    }

    override fun setupListeners() {
        val forgotPasswordScreen = Intent(this, ForgotPasswordActivity::class.java)
        forgotPassBtn.setOnClickListener {
            startActivity(forgotPasswordScreen)
        }

        val signUpScreen = Intent(this, SignUpActivity::class.java)
        signUpBtn.setOnClickListener {
            startActivity(signUpScreen)
        }

        signInBtn.setOnClickListener {
            handleSignIn()
        }

        binding.lytGoogleSignIn.setOnClickListener {
            val url = BuildConfig.BASE_URL + "api/auth/google"
            val intent = Intent(Intent.ACTION_VIEW)

            // Set the data (URL) for the Intent
            intent.data = Uri.parse(url)

            // Start the activity using the created Intent
            startActivity(intent)
        }
    }

    override fun setupObservers() {
        authViewModel.validationResult.observe(this) { validationResult ->
            val (isValid, errorMessages) = validationResult
            if (isValid) {
                //process with api
                authViewModel.resAuth.observe(this) { it ->
                    when (it.status) {
                        StatusAuth.Processing -> {
                            errorMessage.visibility = View.GONE
                            // Disable the button to prevent multiple clicks
                            signInBtn.isEnabled = false
                            signInBtn.setTextColor(Color.BLACK)
                            signInBtn.setBackgroundResource(R.drawable.disable_btn)
                        }

                        StatusAuth.Success -> {
                            signInBtn.isEnabled = true
                            signInBtn.setTextColor(Color.WHITE)
                            signInBtn.setBackgroundResource(R.drawable.custom_style_btn)
                            val mainActivityIntent = Intent(this, MainActivity::class.java)
                            mainActivityIntent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(mainActivityIntent)
                        }

                        StatusAuth.Failed -> {
                            it.data.let { m ->
                                errorMessage.visibility = View.VISIBLE
                                errorMessage.text = m?.message
                            }
                            // Enable the button after sign-in logic
                            signInBtn.isEnabled = true
                            signInBtn.setTextColor(Color.WHITE)
                            signInBtn.setBackgroundResource(R.drawable.custom_style_btn)
                        }

                        StatusAuth.NeedVerify -> {
                            val codeVerificationActivity =
                                Intent(this, CodeVerificationActivity::class.java).apply {
                                    putExtra("email", email.text.toString())
                                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                            startActivity(codeVerificationActivity)
                            //Reset button to default
                            signInBtn.isEnabled = true
                            signInBtn.setTextColor(Color.WHITE)
                            signInBtn.setBackgroundResource(R.drawable.custom_style_btn)
                        }
                    }
                }
            } else {
                signInBtn.isEnabled = true
                signInBtn.setTextColor(Color.WHITE)
                signInBtn.setBackgroundResource(R.drawable.custom_style_btn)
                // Display an error message or handle the failed sign-up
                for (errorMessage in errorMessages) {
                    handleFieldError(errorMessage)
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        authViewModel.validationResult.removeObservers(this)
        authViewModel.resAuth.removeObservers(this)
    }

    //handle Sign In Process
    private fun handleSignInProcession() {
        //call dynamic handleOnChangeEditText from AuthValidation Class
        AuthValidation().handleOnChangeEditText(email)
        AuthValidation().handleOnChangeEditText(password)
    }

    private fun handleSignIn() {
        // Disable the button to prevent multiple clicks
        signInBtn.isEnabled = false
        signInBtn.setTextColor(Color.BLACK)
        signInBtn.setBackgroundResource(R.drawable.disable_btn)

        clearErrorUnderlines()
        authViewModel.signIn(email.text.toString(), password.text.toString())
    }

    private fun handleFieldError(errorMessage: String) {
        when {
            errorMessage.contains("Email") -> underlineField(email, errorMessage)
            errorMessage.contains("Invalided") -> underlineField(email, errorMessage)
            errorMessage.contains("Password") -> underlineField(password, errorMessage)
        }
    }

    private fun underlineField(editText: EditText, message: String) {
        editText.backgroundTintList = ColorStateList.valueOf(Color.RED)
        editText.error = message
    }

    private fun clearErrorUnderlines() {
        email.backgroundTintList = null
        password.backgroundTintList = null
    }
}
