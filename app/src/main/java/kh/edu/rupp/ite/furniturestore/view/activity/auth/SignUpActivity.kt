package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
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
import kh.edu.rupp.ite.furniturestore.model.api.model.StatusAuth
import kh.edu.rupp.ite.furniturestore.view.activity.MainActivity
import kh.edu.rupp.ite.furniturestore.view.activity.validation.AuthValidation
import kh.edu.rupp.ite.furniturestore.viewmodel.AuthViewModel


class SignUpActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var cfPassword: EditText
    private lateinit var signUpBtn: Button
    private var authViewModel = AuthViewModel()
    private var isPasswordVisible = false

    private lateinit var errorMessage: TextView


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //start create sign up screen
        setContentView(R.layout.activity_sign_up)
        //call method SignInScreenDisplay
        initSignInScreenDisplay()

        //class method handleSignUpProcessing
        handleSignUpProcessing()

        prevBack();
    }

    private fun prevBack() {
        val backBtn = findViewById<ImageView>(R.id.backBtn)

        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    //handle Sign In Process
    @SuppressLint("ClickableViewAccessibility")
    private fun handleSignUpProcessing() {

        //get value of editText of SignUpActivity
        name = findViewById(R.id.nameInput)
        email = findViewById(R.id.emInput)
        password = findViewById(R.id.pwInput)
        signUpBtn = findViewById(R.id.signUpBtn)
        errorMessage = findViewById(R.id.errorMsg)

//        cfPassword = findViewById(R.id.cfPwInput)

        //call dynamic handleOnChangeEditText from AuthValidation Class
        AuthValidation().handleOnChangeEditText(name)
        AuthValidation().handleOnChangeEditText(email)
        AuthValidation().handleOnChangeEditText(password)
//        AuthValidation().handleOnChangeEditText(cfPassword)

        togglePasswordVisibility()

        password.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = password.compoundDrawables[2]
//                // Check if the touch event is on the drawableEnd area
                if (event.rawX >= (password.right - drawableEnd.bounds.width())) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        // event Processing of Sign In Button
        signUpBtn.setOnClickListener {
            signUpBtn.isEnabled = false
            signUpBtn.setTextColor(Color.BLACK)
            signUpBtn.setBackgroundResource(R.drawable.disable_btn)
            clearErrorUnderlines()
            authViewModel.signUp(name.text.toString(), email.text.toString(), password.text.toString())
        }
        performSignUp()


    }

    private fun performSignUp(){
        authViewModel.validationResult.observe(this){ validationResult->
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

                        }

                        StatusAuth.Failed -> {
                            it.data.let { m->
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
                            val codeVerificationActivity = Intent(this, CodeVerificationActivity::class.java)
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

    private fun handleFieldError(errorMessage: String) {
        when {
            errorMessage.contains("Name") -> underlineField(name, errorMessage)
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
        name.backgroundTintList = null
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


    private fun initSignInScreenDisplay() {
        //get value from Sign In Button
        val signInBtn = findViewById<TextView>(R.id.signInBtn)
        //defined activity SignIn we're want to route
        val signInScreen = Intent(this, SignInActivity::class.java)

        //action when user click sign in button it will go to sign in screen
        signInBtn.setOnClickListener {
            startActivity(signInScreen)
        }


    }
}