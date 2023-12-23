package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.content.Intent
import android.widget.Button
import android.widget.EditText
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivityChangePasswordBinding
import kh.edu.rupp.ite.furniturestore.databinding.ActivityForgotPasswordBinding
import kh.edu.rupp.ite.furniturestore.view.activity.BaseActivity
import kh.edu.rupp.ite.furniturestore.view.activity.validation.AuthValidation

class ForgotPasswordActivity :
    BaseActivity<ActivityForgotPasswordBinding>(ActivityForgotPasswordBinding::inflate) {

    private lateinit var verifyEmail: EditText

    override fun initActions() {
        //return to prev activity
        prevBack(binding.backBtn)

//        handleVerifyForgotEmail()
    }

    override fun setupListeners() {

    }

    override fun setupObservers() {

    }

    private fun initVerifyScreen() {
        val verifyScreen = Intent(this, CodeVerificationActivity::class.java)
        startActivity(verifyScreen)
    }

    private fun handleVerifyForgotEmail() {
//        val verifyBtn = findViewById<Button>(R.id.verifyBtn)
        verifyEmail = findViewById(R.id.codeVerifyInput)
        AuthValidation().handleOnChangeEditText(verifyEmail)

//        verifyBtn.setOnClickListener {
//            val checkField = AuthValidation().forgotPasswordValidation(verifyEmail)
//            if (checkField){
//                initVerifyScreen()
////                Toast.makeText(this, "Validation Success", Toast.LENGTH_LONG).show()
//            }
//        }
    }
}
