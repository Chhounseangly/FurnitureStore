package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivityCodeVerificationBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.StatusAuth
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

    companion object {
        private const val EMAIL_EXTRA = "email"
    }

    override fun initActions() {
        // Set up text input validation
        AuthValidation().handleOnChangeEditText(codeInput)

        prevBack(binding.backBtn)
    }

    override fun setupListeners() {
        verifyBtn.setOnClickListener {
            // Disable the verification button and submit the verification request
            disableVerifyButton()
            authViewModel.verifyEmail(
                intent.getStringExtra(EMAIL_EXTRA).toString(),
                codeInput.text.toString()
            )
        }
    }

    override fun setupObservers() {
        authViewModel.validationVerify.observe(this) {
            // Handle the validation response
            handleValidationResponse(it)
        }

        // Observe the authentication response
        authViewModel.resAuth.observe(this) {
            when (it.status) {
                StatusAuth.Processing -> {
                    // Handle the processing status, e.g., show loading indicator
                    errMsg.visibility = View.GONE
                    disableVerifyButton()
                }

                StatusAuth.Success -> {
                    // Handle the success status, e.g., navigate to the main activity
                    val mainActivityIntent = Intent(this, MainActivity::class.java)
                    mainActivityIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(mainActivityIntent)
                }

                StatusAuth.Failed -> {
                    // Handle the failure status, e.g., show error message
                    it.data?.let { m ->
                        handleInvalidValidation(m.message)
                    }
                }

                else -> {
                    // Handle any other unknown status
                    errMsg.visibility = View.VISIBLE
                    errMsg.text = "Something went wrong"
                    verifyBtn.isEnabled = true
                    verifyBtn.setTextColor(Color.WHITE)
                    verifyBtn.setBackgroundResource(R.drawable.custom_style_btn)
                }
            }
        }
    }

    /**
     * Handle the validation response from the API.
     */
    private fun handleValidationResponse(validateRes: Pair<Boolean, String>) {
        val (isValid, errorMessages) = validateRes

        if (isValid) {
            // If validation is successful, proceed to handle the authentication response
            handleValidValidation()
        } else {
            // If validation fails, show error messages and Enable the verification button
            handleInvalidValidation(errorMessages)
        }
    }

    /**
     * Handle the case when validation is successful.
     */
    private fun handleValidValidation() {
        // Enable the verification button and set its appearance
        verifyBtn.isEnabled = true
        verifyBtn.setTextColor(Color.WHITE)
        verifyBtn.setBackgroundResource(R.drawable.custom_style_btn)
    }

    /**
     * Handle the case when validation is unsuccessful.
     */
    private fun handleInvalidValidation(errorMessages: String) {
        // Enable the verification button and set its appearance
        verifyBtn.isEnabled = true
        verifyBtn.setTextColor(Color.WHITE)
        verifyBtn.setBackgroundResource(R.drawable.custom_style_btn)

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

    override fun onDestroy() {
        super.onDestroy()
        authViewModel.resAuth.removeObservers(this)
        authViewModel.validationVerify.removeObservers(this)
    }
}
