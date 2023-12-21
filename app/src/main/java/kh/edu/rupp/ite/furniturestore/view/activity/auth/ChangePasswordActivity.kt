package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.widget.ImageView
import com.google.android.material.textfield.TextInputEditText
import kh.edu.rupp.ite.furniturestore.custom_method.TogglePassword
import kh.edu.rupp.ite.furniturestore.databinding.ActivityChangePasswordBinding
import kh.edu.rupp.ite.furniturestore.view.activity.BaseActivity

class ChangePasswordActivity :
    BaseActivity<ActivityChangePasswordBinding>(ActivityChangePasswordBinding::inflate) {

    private val currentPwField: TextInputEditText by lazy { binding.currentPwField }
    private val changePwField: TextInputEditText by lazy { binding.changeNewPwField }
    private val cfNewPwField: TextInputEditText by lazy { binding.cfNewPwInput }
    private val toggleCurrentPwVisibilityBtn: ImageView by lazy { binding.toggleCurrentPwVisibilityBtn }
    private val toggleNewPwVisibilityBtn: ImageView by lazy { binding.toggleNewPwVisibilityBtn }
    private val toggleCfPwVisibilityBtn: ImageView by lazy { binding.toggleCfPwVisibilityBtn }

    override fun initActions() {
        // Current password
        TogglePassword().togglePasswordVisibility(currentPwField, toggleCurrentPwVisibilityBtn)
        // New password
        TogglePassword().togglePasswordVisibility(changePwField, toggleNewPwVisibilityBtn)
        // Confirm New password
        TogglePassword().togglePasswordVisibility(cfNewPwField, toggleCfPwVisibilityBtn)
        // Back button
        prevBack(binding.backBtn)
    }

    override fun setupListeners() {

    }

    override fun setupObservers() {

    }
}
