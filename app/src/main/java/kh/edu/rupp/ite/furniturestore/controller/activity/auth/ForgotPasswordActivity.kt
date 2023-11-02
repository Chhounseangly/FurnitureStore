package kh.edu.rupp.ite.furniturestore.controller.activity.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.controller.activity.validation.AuthValidation


class ForgotPasswordActivity: AppCompatActivity() {

    private lateinit var verifyEmail: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        //return to prev activity
        prevBack()

        handleVerifyForgotEmail()

    }

    private fun prevBack(){
        val backBtn = findViewById<ImageView>(R.id.backBtn)

        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private  fun initVerifyScreen(){
        val verifyScreen = Intent(this, CodeVerificationActivity::class.java)
        startActivity(verifyScreen)
    }

    private fun handleVerifyForgotEmail (){
        val verifyBtn = findViewById<Button>(R.id.verifyBtn)
        verifyEmail = findViewById(R.id.emVerifyInput)
        AuthValidation().handleOnChangeEditText(verifyEmail)

        verifyBtn.setOnClickListener {
            val checkField = AuthValidation().forgotPasswordValidation(verifyEmail)
            if (checkField){
                initVerifyScreen()
//                Toast.makeText(this, "Validation Success", Toast.LENGTH_LONG).show()
            }
        }
    }


}