package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivitySignUpBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.StatusAuth
import kh.edu.rupp.ite.furniturestore.view.activity.BaseActivity
import kh.edu.rupp.ite.furniturestore.view.activity.validation.AuthValidation
import kh.edu.rupp.ite.furniturestore.viewmodel.AuthViewModel

class SignUpActivity : BaseActivity<ActivitySignUpBinding>(ActivitySignUpBinding::inflate) {

    private val authViewModel: AuthViewModel by viewModels()
    private val profile: ImageView by lazy { binding.profile }
    private val name: EditText by lazy { binding.nameInput }
    private val email: EditText by lazy { binding.emInput }
    private val password: EditText by lazy { binding.pwInput }
    private val cfPassword: EditText by lazy { binding.cfPwInput }
    private val signUpBtn: Button by lazy { binding.signUpBtn }
    private val errorMessage: TextView by lazy { binding.errorMsg }

    private var isPasswordVisible = false

    override fun initActions() {
        //call method SignInScreenDisplay
        initSignInScreenDisplay()

        //class method handleSignUpProcessing
        handleSignUpProcessing()

        prevBack(binding.backBtn)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setupListeners() {
        password.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = password.compoundDrawables[2]
                // Check if the touch event is on the drawableEnd area
                if (event.rawX >= (password.right - drawableEnd.bounds.width())) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }
        cfPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = password.compoundDrawables[2]
                // Check if the touch event is on the drawableEnd area
                if (event.rawX >= (password.right - drawableEnd.bounds.width())) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        signUpBtn.setOnClickListener {
            signUpBtn.isEnabled = false
            signUpBtn.setTextColor(Color.BLACK)
            signUpBtn.setBackgroundResource(R.drawable.disable_btn)
            clearErrorUnderlines()
            authViewModel.signUp(
                name.text.toString(),
                email.text.toString(),
                password.text.toString(),
                cfPassword.text.toString()
            )
        }
    }

    override fun setupObservers() {
        authViewModel.validationResult.observe(this) { validationResult ->
            val (isValid, errorMessages) = validationResult
            if (isValid) {
                // Sign-in successful, navigate to the next screen or perform other actions
                authViewModel.resAuth.observe(this) {
                    when (it.status) {
                        StatusAuth.Processing -> {
                            errorMessage.visibility = View.GONE
                            signUpBtn.isEnabled = false
                            signUpBtn.setTextColor(Color.BLACK)
                            signUpBtn.setBackgroundResource(R.drawable.disable_btn)
                        }

                        StatusAuth.Success -> {
                            signUpBtn.isEnabled = true
                        }

                        StatusAuth.Failed -> {
                            it.data.let { m ->
                                errorMessage.visibility = View.VISIBLE
                                errorMessage.text = m?.message
                            }
                            // Enable the button after sign-in logic
                            signUpBtn.isEnabled = true
                            signUpBtn.setTextColor(Color.WHITE)
                            signUpBtn.setBackgroundResource(R.drawable.custom_style_btn)
                        }

                        StatusAuth.NeedVerify -> {
                            signUpBtn.isEnabled = true
                            signUpBtn.setTextColor(Color.WHITE)
                            signUpBtn.setBackgroundResource(R.drawable.custom_style_btn)
                            val codeVerificationActivity =
                                Intent(this, CodeVerificationActivity::class.java)
                            codeVerificationActivity.putExtra("email", email.text.toString())
                            codeVerificationActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(codeVerificationActivity)
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

    //handle Sign In Process
    private fun handleSignUpProcessing() {
//        cfPassword = findViewById(R.id.cfPwInput)

        //call dynamic handleOnChangeEditText from AuthValidation Class
        AuthValidation().handleOnChangeEditText(name)
        AuthValidation().handleOnChangeEditText(email)
        AuthValidation().handleOnChangeEditText(password)
        AuthValidation().handleOnChangeEditText(cfPassword)

        togglePasswordVisibility()
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

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        val drawableResId = if (isPasswordVisible) {
            // Show password
            password.transformationMethod = PasswordTransformationMethod.getInstance()
            cfPassword.transformationMethod = PasswordTransformationMethod.getInstance()

            // Post a delayed action to toggle icon visibility every 2 seconds
            R.drawable.ic_invisible_pw
        } else {
            // Hide password after a delay
            password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            cfPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()

            // Update drawableEnd icon after hiding the password
            R.drawable.ic_visible_pw

        }

        // Move the cursor to the end of the text
        password.setSelection(password.text.length)
        cfPassword.setSelection(cfPassword.text.length)

        // Update the drawableEnd icon
        password.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableResId, 0)
        cfPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableResId, 0)
    }

    private fun initSignInScreenDisplay() {
        //get value from Sign In Button
        val signInBtn = binding.signInBtn
        //defined activity SignIn we're want to route
        val signInScreen = Intent(this, SignInActivity::class.java)

        //action when user click sign in button it will go to sign in screen
        signInBtn.setOnClickListener {
            startActivity(signInScreen)
        }
    }
}
