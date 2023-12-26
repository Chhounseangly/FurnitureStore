package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.widget.Button
import androidx.activity.viewModels
import com.google.android.material.textfield.TextInputEditText
import kh.edu.rupp.ite.furniturestore.databinding.ActivityResetPasswordBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.view.activity.BaseActivity
import kh.edu.rupp.ite.furniturestore.viewmodel.AuthViewModel

class ResetPasswordActivity :
    BaseActivity<ActivityResetPasswordBinding>(ActivityResetPasswordBinding::inflate) {

    private val authViewModel: AuthViewModel by viewModels()
    private val resetPwField: TextInputEditText by lazy { binding.resetPwField }
    private val cfResetPwField: TextInputEditText by lazy { binding.cfResetPwField }
    private val saveBtn: Button by lazy { binding.savePasswordBtn }

    private val email = intent.getStringExtra("email")
    private val token = intent.getStringExtra("token")

    override fun initActions() {
        // Get email and token from intent
        prevBack(binding.backBtn)
    }

    override fun setupListeners() {
        saveBtn.setOnClickListener{
            authViewModel.resetPassword(
                email.toString(),
                token.toString(),
                resetPwField.text.toString(),
                cfResetPwField.text.toString()
            )
        }
    }

    override fun setupObservers() {
        authViewModel.resMsg.observe(this) {
            when (it.status) {
                Status.Processing -> {

                }

                Status.Success -> {
                    setResult(RESULT_OK)
                    finish()
                }

                Status.Failed -> {

                }

                else -> {}
            }
        }
    }
}