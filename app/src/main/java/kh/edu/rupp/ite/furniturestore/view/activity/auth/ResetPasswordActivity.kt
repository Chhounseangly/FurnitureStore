package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kh.edu.rupp.ite.furniturestore.R
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
    private lateinit var otp: String

    private val actionBarView: View by lazy {
        showCustomActionBar(this, R.layout.activity_action_bar)
    }


    override fun initActions() {

        actionBarView.apply {
            findViewById<TextView>(R.id.title_action_bar)?.apply {
                text = getString(R.string.reset_new_password)
            }

            // Set up back button navigation
            findViewById<ImageView>(R.id.backPrev)?.setOnClickListener {
                prevBack(it)
            }
        }
        // Get email and token from intent
//        prevBack(binding.backBtn)

        email = intent.getStringExtra("email").toString()
        otp = intent.getStringExtra("otp").toString()
    }

    override fun setupListeners() {
        saveBtn.setOnClickListener {
            authViewModel.resetPassword(
                email,
                otp,
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