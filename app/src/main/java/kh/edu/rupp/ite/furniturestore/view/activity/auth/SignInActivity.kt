package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.displayFragment.DisplayFragmentActivity
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.view.activity.MainActivity
import kh.edu.rupp.ite.furniturestore.view.activity.validation.AuthValidation
import kh.edu.rupp.ite.furniturestore.view.fragments.HomeFragment
import kh.edu.rupp.ite.furniturestore.viewmodel.AuthViewModel


class SignInActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private var authViewModel = AuthViewModel()

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


    //handle back
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
//            //validation checked is true go
            if (isAllFieldsChecked) {
                authViewModel.login(this, email.text.toString(), password.text.toString())
            }
        }

        authViewModel.resAuth.observe(this){
            when(it.status){
                Status.Processing -> null
                    Status.Success -> {
                        val mainActivityIntent = Intent(this, MainActivity::class.java)
                        mainActivityIntent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(mainActivityIntent)
                    }
                Status.Failed -> {

                }
            }
        }

    }

}