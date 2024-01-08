package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivityCodeVerificationBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.view.activity.BaseActivity
import kh.edu.rupp.ite.furniturestore.view.activity.MainActivity
import kh.edu.rupp.ite.furniturestore.view.activity.validation.AuthValidation
import kh.edu.rupp.ite.furniturestore.viewmodel.AuthViewModel

/**
 * Activity for verifying a user's email with a code.
 */
class CodeVerificationActivity :
    BaseActivity<ActivityCodeVerificationBinding>(ActivityCodeVerificationBinding::inflate) {

    private val authViewModel: AuthViewModel by viewModels()
    private val codeInput: EditText by lazy { binding.codeVerifyInput }
    private val verifyBtn: Button by lazy { binding.verifyBtn }
    private val errMsg: TextView by lazy { binding.errorMsg }
    private val resendCodeContent: TextView by lazy { binding.resendCodeContent }
    private val resendCodeBtn: TextView by lazy { binding.resendCodeBtn }

    private lateinit var countdownTimer: CountDownTimer

    private val actionBarView: View by lazy {
        showCustomActionBar(this, R.layout.activity_action_bar)
    }

    companion object {
        const val TYPE_FORGOT_PASSWORD = "forgot_password"
        const val TYPE = "type"
        const val EMAIL_EXTRA = "email"
    }

    override fun initActions() {

        //show action bar
        actionBarView.apply {
            findViewById<TextView>(R.id.title_action_bar)?.apply {
                visibility = View.GONE
            }

            // Set up back button navigation
            findViewById<ImageView>(R.id.backPrev)?.setOnClickListener {
                prevBack(it)
            }
        }
        // Set up text input validation
        AuthValidation().handleOnChangeEditText(codeInput)

        navigationBetweenEditTexts(codeInput, null) {
            verifyEmail()
        }
        startCountdownTimer()
    }

    override fun setupListeners() {
        verifyBtn.setOnClickListener {
            verifyEmail()
        }

        codeInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Check if the length of the text is 6 characters
                if (s?.length == 6) {
                    // Call handleValidation() when the length is 6
                    verifyEmail()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }
        })

        resendCodeBtn.setOnClickListener {
            authViewModel.resendCode(intent.getStringExtra(EMAIL_EXTRA).toString())
        }
    }

    override fun setupObservers() {
        authViewModel.validationVerify.observe(this) {
            // Handle the validation response
            handleValidationResponse(it)
        }

        authViewModel.resMsg.observe(this) {
            when (it.status) {
                Status.Processing -> {
                    // Handle the processing status, e.g., show loading indicator
                    errMsg.visibility = View.GONE
                    disableVerifyButton()
                }

                Status.Success -> {
                    // Handle the success status, e.g., navigate to the main activity
                    startCountdownTimer()
                    enableVerifyButton()
                }

                Status.Failed -> {
                    // Handle the failure status, e.g., show error message
                    it.data?.let { m ->
                        handleInvalidValidation(m.message)
                    }
                }

                else -> {
                    // Handle any other unknown status
                    errMsg.visibility = View.VISIBLE
                    errMsg.text = getString(R.string.error_occurred)
                    verifyBtn.isEnabled = true
                    verifyBtn.setTextColor(Color.WHITE)
                    verifyBtn.setBackgroundResource(R.drawable.custom_style_btn)
                }
            }
        }

        // Observe the authentication response
        authViewModel.resAuth.observe(this) {
            when (it.status) {
                Status.Processing -> {
                    // Handle the processing status, e.g., show loading indicator
                    errMsg.visibility = View.GONE
                    disableVerifyButton()
                }

                Status.Success -> {
                    if (TYPE_FORGOT_PASSWORD == intent.getStringExtra(TYPE)) {
                        val resetPasswordActivity = Intent(this, ResetPasswordActivity::class.java)
                        resetPasswordActivity.putExtra(
                            EMAIL_EXTRA,
                            intent.getStringExtra(EMAIL_EXTRA).toString()
                        )
                        resetPasswordActivity.putExtra(
                            "otp",
                            codeInput.text.toString()
                        )
                        startActivity(resetPasswordActivity)
                    } else {
                        // Handle the success status, e.g., navigate to the main activity
                        val mainActivityIntent = Intent(this, MainActivity::class.java)
                        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(mainActivityIntent)
                    }
                }

                Status.Failed -> {
                    // Handle the failure status, e.g., show error message
                    it.data?.let { m ->
                        handleInvalidValidation(m.message)
                    }
                }

                else -> {
                    // Handle any other unknown status
                    errMsg.visibility = View.VISIBLE
                    errMsg.text = getString(R.string.error_occurred)
                    verifyBtn.isEnabled = true
                    verifyBtn.setTextColor(Color.WHITE)
                    verifyBtn.setBackgroundResource(R.drawable.custom_style_btn)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        authViewModel.resAuth.removeObservers(this)
        authViewModel.validationVerify.removeObservers(this)

        // Cancel the countdown timer to prevent leaks
        if (::countdownTimer.isInitialized) {
            countdownTimer.cancel()
        }
    }

    private fun startCountdownTimer() {
        resendCodeBtn.isEnabled = false
        countdownTimer = object : CountDownTimer(300000, 1000) { // 5 minutes countdown
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000

                val countdownMessage = if (minutes > 0) {
                    resources.getString(R.string.resend_code_countdown_minutes, minutes, seconds)
                } else {
                    resources.getString(R.string.resend_code_countdown_seconds, seconds)
                }
                resendCodeContent.text = countdownMessage
            }

            override fun onFinish() {
                resendCodeContent.text = getString(R.string.i_did_not_receive_a_code_txt)
                resendCodeBtn.isEnabled = true
            }
        }.start()
    }


    private fun verifyEmail() {
        if (TYPE_FORGOT_PASSWORD == intent.getStringExtra(TYPE)) {
            authViewModel.verifyOTP(
                intent.getStringExtra(EMAIL_EXTRA).toString(),
                codeInput.text.toString()
            )
        } else {
            authViewModel.verifyEmail(
                intent.getStringExtra(EMAIL_EXTRA).toString(),
                codeInput.text.toString()
            )
        }
    }

    /**
     * Handle the validation response from the API.
     */
    private fun handleValidationResponse(validateRes: Pair<Boolean, String>) {
        val (isValid, errorMessages) = validateRes

        if (isValid) {
            // If validation is successful, proceed to handle the authentication response
            enableVerifyButton()
        } else {
            // If validation fails, show error messages and Enable the verification button
            handleInvalidValidation(errorMessages)
        }
    }

    /**
     * Handle the case when validation is unsuccessful.
     */
    private fun handleInvalidValidation(errorMessages: String) {
        // Enable the verification button and set its appearance
        enableVerifyButton()

        // Highlight the input field with an error
        codeInput.backgroundTintList = ColorStateList.valueOf(Color.RED)
        codeInput.error = errorMessages
    }

    /**
     * Disable the verification button and set its appearance to indicate a disabled state.
     */
    private fun disableVerifyButton() {
        verifyBtn.isEnabled = false
        verifyBtn.setTextColor(Color.BLACK)
        verifyBtn.setBackgroundResource(R.drawable.disable_btn)
        codeInput.backgroundTintList = null
    }

    /**
     * Enable the verification button and set its appearance to indicate an enabled state.
     */
    private fun enableVerifyButton() {
        verifyBtn.isEnabled = true
        verifyBtn.setTextColor(Color.WHITE)
        verifyBtn.setBackgroundResource(R.drawable.custom_style_btn)
    }
}
