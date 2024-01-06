package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivitySignUpBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.view.activity.validation.AuthValidation
import kh.edu.rupp.ite.furniturestore.viewmodel.AuthViewModel

class SignUpActivity : AuthActivity<ActivitySignUpBinding>(ActivitySignUpBinding::inflate) {

    private val authViewModel: AuthViewModel by viewModels()
    private val avatar: ImageView by lazy { binding.profile }
    private val name: EditText by lazy { binding.nameInput }
    private val email: EditText by lazy { binding.emInput }
    private val password: EditText by lazy { binding.pwInput }
    private val cfPassword: EditText by lazy { binding.cfPwInput }
    private val signUpBtn: Button by lazy { binding.signUpBtn }
    private val errorMessage: TextView by lazy { binding.errorMsg }

    override fun initActions() {
        supportActionBar?.hide()

        // Initialize sign-in screen display
        initSignInScreenDisplay()

        // Initialize sign-up processing
        handleSignUpProcessing()

        // Set up listeners and actions
        prevBack(binding.backBtn)
        setupImagePickerLauncher(avatar)
        setupPasswordToggle(password, cfPassword)
        togglePasswordVisibility(password, cfPassword)

        // Set up navigation between EditTexts
        navigationBetweenEditTexts(name, email)
        navigationBetweenEditTexts(email, password)
        navigationBetweenEditTexts(password, cfPassword)
        navigationBetweenEditTexts(cfPassword, null) {
            handleSignUp()
        }
    }

    override fun setupListeners() {
        avatar.setOnClickListener {
            openImageChooser()
        }

        signUpBtn.setOnClickListener {
            handleSignUp()
        }
    }

    override fun setupObservers() {
        authViewModel.validationResult.observe(this) { validationResult ->
            val (isValid, errorMessages) = validationResult
            if (isValid) {
                // Sign-in successful, navigate to the next screen or perform other actions
                authViewModel.resAuth.observe(this) {
                    when (it.status) {
                        Status.Processing -> {
                            Log.d("SignUpActivity", "Processing")
                            errorMessage.visibility = View.GONE
                            signUpBtn.isEnabled = false
                            signUpBtn.setTextColor(Color.BLACK)
                            signUpBtn.setBackgroundResource(R.drawable.disable_btn)
                        }

                        Status.Success -> {
                            Log.d("SignUpActivity", "NeedVerify")
                            signUpBtn.isEnabled = true
                            signUpBtn.setTextColor(Color.WHITE)
                            signUpBtn.setBackgroundResource(R.drawable.custom_style_btn)
                            val codeVerificationActivity =
                                Intent(this, CodeVerificationActivity::class.java)
                            codeVerificationActivity.putExtra("email", email.text.toString())
                            codeVerificationActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(codeVerificationActivity)
                        }

                        Status.Failed -> {
                            Log.d("SignUpActivity", "Failed")
                            it.data.let { m ->
                                errorMessage.visibility = View.VISIBLE
                                errorMessage.text = m?.message
                            }
                            // Enable the button after sign-in logic
                            signUpBtn.isEnabled = true
                            signUpBtn.setTextColor(Color.WHITE)
                            signUpBtn.setBackgroundResource(R.drawable.custom_style_btn)
                        }

                        else -> {
                            Log.d("SignUpActivity", "Else")
                        }
                    }
                }
            } else {
                // Enable the button after sign-in logic
                signUpBtn.isEnabled = true
                signUpBtn.setTextColor(Color.WHITE)
                signUpBtn.setBackgroundResource(R.drawable.custom_style_btn)
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

    private fun handleSignUpProcessing() {
        //call dynamic handleOnChangeEditText from AuthValidation Class
        AuthValidation().handleOnChangeEditText(name)
        AuthValidation().handleOnChangeEditText(email)
        AuthValidation().handleOnChangeEditText(password)
        AuthValidation().handleOnChangeEditText(cfPassword)
    }

    private fun handleFieldError(errorMessage: String) {
        when {
            errorMessage.contains("Name") -> underlineField(name, errorMessage)
            errorMessage.contains("Email") -> underlineField(email, errorMessage)
            errorMessage.contains("Invalided") -> underlineField(email, errorMessage)
            errorMessage.contains("Password") -> underlineField(password, errorMessage)
            errorMessage.contains("Confirm") -> underlineField(cfPassword, errorMessage)
        }
    }

    private fun underlineField(editText: EditText, message: String) {
        editText.backgroundTintList = ColorStateList.valueOf(Color.RED)
        editText.error = message
    }

    private fun clearErrorUnderlines() {
        name.backgroundTintList = null
        email.backgroundTintList = null
        password.backgroundTintList = null
    }

    private fun setupPasswordToggle(passwordEditText: EditText, confirmPasswordEditText: EditText) {
        val touchListener: (View, MotionEvent) -> Boolean = setOnTouchListener@{ _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = passwordEditText.compoundDrawables[2]
                if (event.rawX >= (passwordEditText.right - drawableEnd.bounds.width())) {
                    togglePasswordVisibility(password, cfPassword)
                    return@setOnTouchListener true
                }
            }
            false
        }

        passwordEditText.run { setOnTouchListener(touchListener) }
        confirmPasswordEditText.run { setOnTouchListener(touchListener) }
    }

    private fun initSignInScreenDisplay() {
        //get value from Sign In Button
        val signInBtn = binding.signInBtn

        //action when user click sign in button it will go to sign in screen
        signInBtn.setOnClickListener {
            finish()
        }
    }

    private fun handleSignUp() {
        signUpBtn.isEnabled = false
        signUpBtn.setTextColor(Color.BLACK)
        signUpBtn.setBackgroundResource(R.drawable.disable_btn)
        clearErrorUnderlines()
        if (isImageChanged()) {
            authViewModel.signUp(
                name.text.toString(),
                email.text.toString(),
                password.text.toString(),
                cfPassword.text.toString(),
                getAvatar()
            )
        } else {
            authViewModel.signUp(
                name.text.toString(),
                email.text.toString(),
                password.text.toString(),
                cfPassword.text.toString()
            )
        }
    }
}
