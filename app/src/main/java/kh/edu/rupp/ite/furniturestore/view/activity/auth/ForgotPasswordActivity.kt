package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.graphics.Color
import android.os.CountDownTimer
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

    private lateinit var countdownTimer: CountDownTimer

    override fun initActions() {
        prevBack(binding.backBtn)

        navigationBetweenEditTexts(verifyEmail, null) {
            startCountdownTimer()
            authViewModel.forgotPassword(verifyEmail.text.toString())
        }

        handleVerifyForgotEmail()
    }

    override fun setupListeners() {
        sendResetBtn.setOnClickListener {
            val checkField = AuthValidation().forgotPasswordValidation(verifyEmail)
            if (checkField) {
                startCountdownTimer()
                authViewModel.forgotPassword(verifyEmail.text.toString())
            }
        }
    }

    override fun setupObservers() {
        authViewModel.resMsg.observe(this) {
            when (it.status) {
                Status.Processing -> {

                }

                Status.Success -> {
                    Snackbar.make(
                        binding.root,
                        R.string.send_reset_email_already,
                        Snackbar.LENGTH_LONG
                    ).show()
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

    override fun onDestroy() {
        super.onDestroy()

        // Cancel the countdown timer to prevent leaks
        if (::countdownTimer.isInitialized) {
            countdownTimer.cancel()
        }
    }

    private fun handleVerifyForgotEmail() {
        AuthValidation().handleOnChangeEditText(verifyEmail)
    }

    private fun startCountdownTimer() {
        countdownTimer = object : CountDownTimer(3600000, 60000) { // 1 hour in milliseconds, tick every 1 minute
            override fun onTick(millisUntilFinished: Long) {
                // Update the button text with the remaining time in minutes
                val minutesRemaining = millisUntilFinished / 60000
                sendResetBtn.text = "Resend in: $minutesRemaining minutes"
                sendResetBtn.isEnabled = false
                sendResetBtn.setTextColor(Color.BLACK)
                sendResetBtn.setBackgroundResource(R.drawable.disable_btn)
            }

            override fun onFinish() {
                // Reset the button to its initial state after the countdown finishes
                sendResetBtn.text = "Resend"
                sendResetBtn.isEnabled = true
                sendResetBtn.setTextColor(Color.WHITE)
                sendResetBtn.setBackgroundResource(R.drawable.custom_style_btn)
            }
        }

        // Start the countdown
        countdownTimer.start()
    }
}
