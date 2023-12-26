package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.content.Intent
import android.widget.Button
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
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

    private lateinit var email: String
    private lateinit var token: String

    override fun initActions() {
        // Get email and token from intent
        prevBack(binding.backBtn)

        email = intent.getStringExtra("email").toString()
        token = intent.getStringExtra("token").toString()
    }

    override fun setupListeners() {
        saveBtn.setOnClickListener {
            authViewModel.resetPassword(
                email,
                token,
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
                    val signInActivity = Intent(this, SignInActivity::class.java)
                    signInActivity.putExtra("isReset", true)
                    startActivity(signInActivity)
                    finish()
                }

                Status.Failed -> {
                    Snackbar.make(
                        binding.root,
                        it.data?.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                else -> {}
            }
        }
    }
}