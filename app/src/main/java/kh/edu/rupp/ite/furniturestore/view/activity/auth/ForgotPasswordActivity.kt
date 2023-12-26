package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import kh.edu.rupp.ite.furniturestore.databinding.ActivityForgotPasswordBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.view.activity.BaseActivity
import kh.edu.rupp.ite.furniturestore.view.activity.validation.AuthValidation
import kh.edu.rupp.ite.furniturestore.viewmodel.AuthViewModel

class ForgotPasswordActivity :
    BaseActivity<ActivityForgotPasswordBinding>(ActivityForgotPasswordBinding::inflate) {

    private val authViewModel: AuthViewModel by viewModels()
    private val verifyEmail: EditText by lazy { binding.codeVerifyInput }
    private val verifyBtn: Button by lazy { binding.verifyBtn }

    override fun initActions() {
        prevBack(binding.backBtn)

        handleVerifyForgotEmail()
    }

    override fun setupListeners() {
        verifyBtn.setOnClickListener {
            val checkField = AuthValidation().forgotPasswordValidation(verifyEmail)
            if (checkField) {
                authViewModel.forgotPassword(verifyEmail.text.toString())
            }
        }
    }

    override fun setupObservers() {
        authViewModel.resMsg.observe(this) {
            when (it.status) {
                Status.Processing -> {
                    Log.d("ForgotPasswordActivity", "Processing")
                }

                Status.Success -> {
                    Log.d("ForgotPasswordActivity", "Success")
//                    initVerifyScreen()
                }

                Status.Failed -> {
                    Log.d("ForgotPasswordActivity", "Failed")
                }

                else -> {}
            }
        }
    }

    private fun initVerifyScreen() {
        val verifyScreen = Intent(this, CodeVerificationActivity::class.java)
        startActivity(verifyScreen)
    }

    private fun handleVerifyForgotEmail() {
        AuthValidation().handleOnChangeEditText(verifyEmail)
    }
}
