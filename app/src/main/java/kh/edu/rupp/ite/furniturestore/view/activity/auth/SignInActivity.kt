package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kh.edu.rupp.ite.furniturestore.BuildConfig
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivitySignInBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.StatusAuth
import kh.edu.rupp.ite.furniturestore.view.activity.BaseActivity
import kh.edu.rupp.ite.furniturestore.view.activity.MainActivity
import kh.edu.rupp.ite.furniturestore.view.activity.validation.AuthValidation
import kh.edu.rupp.ite.furniturestore.viewmodel.AuthViewModel

class SignInActivity : BaseActivity<ActivitySignInBinding>(ActivitySignInBinding::inflate) {
    private val email: EditText by lazy { binding.emInput }
    private val password: EditText by lazy { binding.pwInput }
    private val errorMessage: TextView by lazy { binding.errorMsg }

    private val signInBtn: Button by lazy { binding.signInBtn }

    private var authViewModel = AuthViewModel()
    private var isPasswordVisible = false

    override fun initActions() {
        //call method for handle go to sign up screen
        initSignUpScreen()
        //class method for handle go to forgot password screen
        initForgotPasswordScreen()
        //call method handle Sign In Processing
        handleSignInProcession()

        //return to prev activity
        prevBack(binding.backBtn)
        setupPasswordToggle(password)
        togglePasswordVisibility()

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
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        passwordEditText.run { setOnTouchListener(touchListener) }
    }

    override fun setupListeners() {
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

    }

    override fun onDestroy() {
        super.onDestroy()
        authViewModel.validationResult.removeObservers(this)
        authViewModel.resAuth.removeObservers(this)
    }

    //handel user click on forgot password
    private fun initForgotPasswordScreen() {
        val forgotPassBtn = binding.forgotPsBtn
        val forgotPasswordScreen = Intent(this, ForgotPasswordActivity::class.java)
        forgotPassBtn.setOnClickListener {
            startActivity(forgotPasswordScreen)
        }
    }


    //handel user click on  sign up
    private fun initSignUpScreen() {
        val signUpBtn = binding.signUpBtn
        val signUpScreen = Intent(this, SignUpActivity::class.java)
        signUpBtn.setOnClickListener {
            startActivity(signUpScreen)
        }
    }


    //handle Sign In Process
    private fun handleSignInProcession() {
        //call dynamic handleOnChangeEditText from AuthValidation Class
        AuthValidation().handleOnChangeEditText(email)
        AuthValidation().handleOnChangeEditText(password)

        //perform Login with api
        performLogin()

    }

    private fun handleSignIn() {
        authViewModel.resAuth.removeObservers(this)
        // Disable the button to prevent multiple clicks
        signInBtn.isEnabled = false
        signInBtn.setTextColor(Color.BLACK)
        signInBtn.setBackgroundResource(R.drawable.disable_btn)

        clearErrorUnderlines()
        authViewModel.signIn(email.text.toString(), password.text.toString())
    }

    private fun performLogin() {

        // Remove previous observers before adding a new one
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

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        val drawableResId = if (isPasswordVisible) {
            // Show password
            password.transformationMethod = PasswordTransformationMethod.getInstance()

            // Post a delayed action to toggle icon visibility every 2 seconds
            R.drawable.ic_invisible_pw
        } else {
            // Hide password after a delay
            password.transformationMethod = HideReturnsTransformationMethod.getInstance()

            // Update drawableEnd icon after hiding the password
            R.drawable.ic_visible_pw

        }
        // Move the cursor to the end of the text
        password.setSelection(password.text.length)
        password.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableResId, 0)
    }
}
