package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.content.Intent
import android.graphics.Color
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivityForgotPasswordBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.view.activity.BaseActivity
import kh.edu.rupp.ite.furniturestore.view.activity.validation.AuthValidation
import kh.edu.rupp.ite.furniturestore.viewmodel.AuthViewModel

class ForgotPasswordActivity :
    BaseActivity<ActivityForgotPasswordBinding>(ActivityForgotPasswordBinding::inflate) {

    private val authViewModel: AuthViewModel by viewModels()
    private val verifyEmail: EditText by lazy { binding.codeVerifyInput }
    private val sendResetBtn: Button by lazy { binding.sendResetBtn }

    override fun initActions() {
        prevBack(binding.backBtn)

        navigationBetweenEditTexts(verifyEmail, null) {
            handleSendEmail()
        }

        handleVerifyForgotEmail()
    }

    override fun setupListeners() {
        sendResetBtn.setOnClickListener {
            val checkField = AuthValidation().forgotPasswordValidation(verifyEmail)
            if (checkField) {
                handleSendEmail()
            }
        }
    }

    override fun setupObservers() {
        authViewModel.resMsg.observe(this) {
            when (it.status) {
                Status.Processing -> {

                }

                Status.Success -> {
                    val codeVerifyActivity = Intent(this, CodeVerificationActivity::class.java)
                    codeVerifyActivity.putExtra(CodeVerificationActivity.EMAIL_EXTRA, verifyEmail.text.toString())
                    codeVerifyActivity.putExtra(CodeVerificationActivity.TYPE, CodeVerificationActivity.TYPE_FORGOT_PASSWORD)
                    startActivity(codeVerifyActivity)
                }

                Status.Failed -> {
                    it.data?.let { message ->
                        Snackbar.make(
                            binding.root,
                            message.message,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    sendResetBtn.isEnabled = true
                    sendResetBtn.setTextColor(Color.WHITE)
                    sendResetBtn.setBackgroundResource(R.drawable.custom_style_btn)
                }

                else -> {}
            }
        }
    }

    private fun handleVerifyForgotEmail() {
        AuthValidation().handleOnChangeEditText(verifyEmail)
    }

    private fun handleSendEmail() {
        sendResetBtn.isEnabled = false
        sendResetBtn.setTextColor(Color.BLACK)
        sendResetBtn.setBackgroundResource(R.drawable.disable_btn)
        authViewModel.forgotPassword(verifyEmail.text.toString())
    }
}
