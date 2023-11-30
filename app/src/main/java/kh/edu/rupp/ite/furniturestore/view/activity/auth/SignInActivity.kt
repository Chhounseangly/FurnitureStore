package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.view.activity.MainActivity
import kh.edu.rupp.ite.furniturestore.view.activity.validation.AuthValidation
import kh.edu.rupp.ite.furniturestore.viewmodel.AuthViewModel


class SignInActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var errorMessage: TextView

    private lateinit var signInBtn: Button

    private var authViewModel = AuthViewModel()
    private var isPasswordVisible = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        //call method for handle go to sign up screen
        initSignUpScreen()
        //class method for handle go to forgot password screen
        initForgotPasswordScreen()
        //call method handle Sign In Processing
        handleSignIn()

        //return to prev activity
        prevBack()

        togglePasswordVisibility()
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

    }


    //handle back
    private fun prevBack() {
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    //handel user click on forgot password
    private fun initForgotPasswordScreen() {
        val forgotPassBtn = findViewById<TextView>(R.id.forgotPsBtn)
        val forgotPasswordScreen = Intent(this, ForgotPasswordActivity::class.java)
        forgotPassBtn.setOnClickListener {
            startActivity(forgotPasswordScreen)
        }
    }


    //handel user click on  sign up
    private fun initSignUpScreen() {
        val signUpBtn = findViewById<TextView>(R.id.signUpBtn)
        val signUpScreen = Intent(this, SignUpActivity::class.java)
        signUpBtn.setOnClickListener {
            startActivity(signUpScreen)
        }
    }


    //handle Sign In Process
    private fun handleSignIn() {
        email = findViewById(R.id.emInput)
        password = findViewById(R.id.pwInput)
        signInBtn = findViewById(R.id.signInBtn)

        errorMessage = findViewById(R.id.errorMsg)

        //call dynamic handleOnChangeEditText from AuthValidation Class
        AuthValidation().handleOnChangeEditText(email)
        AuthValidation().handleOnChangeEditText(password)

        // event Processing of Sign In Button
        signInBtn.setOnClickListener {
            authViewModel.resAuth.removeObservers(this)
            // Disable the button to prevent multiple clicks
            signInBtn.isEnabled = false
            signInBtn.setTextColor(Color.BLACK)
            signInBtn.setBackgroundResource(R.drawable.disable_btn)

            clearErrorUnderlines()
            authViewModel.signIn(email.text.toString(), password.text.toString())
        }

        //perform Login with api
        performLogin()

    }

    private fun performLogin() {

        // Remove previous observers before adding a new one
        authViewModel.validationResult.observe(this) { validationResult ->
            val (isValid, errorMessages) = validationResult
            if (isValid) {
                //process with api
                authViewModel.resAuth.observe(this) { it ->
                    when (it.status) {
                        Status.Processing -> {
                            errorMessage.visibility = View.GONE
                            // Disable the button to prevent multiple clicks
                            signInBtn.isEnabled = false
                            signInBtn.setTextColor(Color.BLACK)
                            signInBtn.setBackgroundResource(R.drawable.disable_btn)
                        }

                        Status.Success -> {
                            signInBtn.isEnabled = true
                            signInBtn.setTextColor(Color.WHITE)
                            signInBtn.setBackgroundResource(R.drawable.custom_style_btn)
                            val mainActivityIntent = Intent(this, MainActivity::class.java)
                            mainActivityIntent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(mainActivityIntent)
                        }

                        Status.Failed -> {
                            it.data.let { m->
                                errorMessage.visibility = View.VISIBLE
                                errorMessage.text = m?.message
                            }
                            // Enable the button after sign-in logic
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

    override fun onDestroy() {
        super.onDestroy()
        authViewModel.validationResult.removeObservers(this)
        authViewModel.resAuth.removeObservers(this)
    }
}