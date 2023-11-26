package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.displayFragment.DisplayFragmentActivity
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.view.activity.MainActivity
import kh.edu.rupp.ite.furniturestore.view.activity.validation.AuthValidation
import kh.edu.rupp.ite.furniturestore.view.fragments.HomeFragment
import kh.edu.rupp.ite.furniturestore.viewmodel.AuthViewModel


class SignUpActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var cfPassword: EditText
    private var authViewModel = AuthViewModel()
    private var homeFragment = HomeFragment()
    private var displayFragmentActivity = DisplayFragmentActivity(supportFragmentManager)

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

    private fun prevBack(){
        val backBtn = findViewById<ImageView>(R.id.backBtn)

        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    //handle Sign In Process
    private fun handleSignUpProcessing() {
        val signUpBtn = findViewById<Button>(R.id.signUpBtn)

        //get value of editText of SignUpActivity
        name = findViewById(R.id.usernameInput)
        email = findViewById(R.id.emInput)
        password = findViewById(R.id.pwInput)
//        cfPassword = findViewById(R.id.cfPwInput)


        //call dynamic handleOnChangeEditText from AuthValidation Class
        AuthValidation().handleOnChangeEditText(name)
        AuthValidation().handleOnChangeEditText(email)
        AuthValidation().handleOnChangeEditText(password)
//        AuthValidation().handleOnChangeEditText(cfPassword)

        // event Processing of Sign In Button
        signUpBtn.setOnClickListener {
            //call method signUpValidation from AuthValidation Class
            val isAllFieldsChecked = AuthValidation().signUpValidation(name, email, password)
            //validation checked is true process
            if (isAllFieldsChecked) {
                authViewModel.signUp(this, name.text.toString(), email.text.toString(), password.text.toString())
            }
        }

        authViewModel.resAuth.observe(this){
            when(it.status){
                Status.Processing -> null
                Status.Success -> {
                    val mainActivityIntent = Intent(this, MainActivity::class.java)
                    mainActivityIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(mainActivityIntent)
                }
                Status.Failed -> {

                }
            }
        }
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