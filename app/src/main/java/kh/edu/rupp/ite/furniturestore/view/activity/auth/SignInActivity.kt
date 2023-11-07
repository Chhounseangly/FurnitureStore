package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.view.activity.validation.AuthValidation


class SignInActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private var isAllFieldsChecked = false

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

    }

    private fun prevBack(){
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
        val signInBtn = findViewById<Button>(R.id.signInBtn)

        //call dynamic handleOnChangeEditText from AuthValidation Class
        AuthValidation().handleOnChangeEditText(email)
        AuthValidation().handleOnChangeEditText(password)

        // event Processing of Sign In Button
        signInBtn.setOnClickListener {
            //call method signInValidation from AuthValidation Class
            val isAllFieldsChecked = AuthValidation().signInValidation(email, password);

            //validation checked is true go
            if (isAllFieldsChecked) {
                Toast.makeText(this, "Validation Success", Toast.LENGTH_LONG).show()
            }
        }

    }

}