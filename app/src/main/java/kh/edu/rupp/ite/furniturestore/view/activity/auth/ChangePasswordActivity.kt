package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.app.Activity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivityChangePasswordBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.viewmodel.AuthViewModel

class ChangePasswordActivity :
    AuthActivity<ActivityChangePasswordBinding>(ActivityChangePasswordBinding::inflate) {

    private val authViewModel: AuthViewModel by viewModels()
    private val currentPwField: TextInputEditText by lazy { binding.currentPwField }
    private val changePwField: TextInputEditText by lazy { binding.changeNewPwField }
    private val cfNewPwField: TextInputEditText by lazy { binding.cfNewPwInput }
    private val toggleCurrentPwVisibilityBtn: ImageView by lazy { binding.toggleCurrentPwVisibilityBtn }
    private val toggleNewPwVisibilityBtn: ImageView by lazy { binding.toggleNewPwVisibilityBtn }
    private val toggleCfPwVisibilityBtn: ImageView by lazy { binding.toggleCfPwVisibilityBtn }
    private val savePasswordBtn: Button by lazy { binding.savePasswordBtn }
    private val actionBarView: View by lazy {
         showCustomActionBar(this, R.layout.activity_action_bar)
    }

    override fun initActions() {

        //show action bar
        actionBarView.apply {
            findViewById<TextView>(R.id.title_action_bar)?.apply {
                text = getString(R.string.change_new_pw_txt)
            }

            // Set up back button navigation
            findViewById<ImageView>(R.id.backPrev)?.setOnClickListener {
                prevBack(it)
            }
        }
        // Current password
        togglePasswordVisibility(currentPwField, toggleCurrentPwVisibilityBtn)
        // New password
        togglePasswordVisibility(changePwField, toggleNewPwVisibilityBtn)
        // Confirm New password
        togglePasswordVisibility(cfNewPwField, toggleCfPwVisibilityBtn)

    }

    override fun setupListeners() {
        savePasswordBtn.setOnClickListener {
            authViewModel.changePassword(
                currentPwField.text.toString(),
                changePwField.text.toString(),
                cfNewPwField.text.toString())
        }

    }

    override fun setupObservers() {
        authViewModel.resMsg.observe(this) {
            when (it.status) {
                Status.Processing -> {

                }

                Status.Success -> {
                    setResult(Activity.RESULT_OK)
                    finish()
                }

                Status.Failed -> {
                    it.data?.let { it1 ->
                        Snackbar.make(
                            binding.root,
                            it1.message,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }

                else -> {

                }
            }
        }
    }
}
