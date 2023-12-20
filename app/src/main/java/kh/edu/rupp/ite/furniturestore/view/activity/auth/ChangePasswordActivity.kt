package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.widget.ImageView
import com.google.android.material.textfield.TextInputEditText
import kh.edu.rupp.ite.furniturestore.custom_method.TogglePassword
import kh.edu.rupp.ite.furniturestore.databinding.ActivityChangePasswordBinding
import kh.edu.rupp.ite.furniturestore.view.activity.BaseActivity

class ChangePasswordActivity :
    BaseActivity<ActivityChangePasswordBinding>(ActivityChangePasswordBinding::inflate) {

    private lateinit var currentPwField: TextInputEditText
    private lateinit var changePwField: TextInputEditText
    private lateinit var cfNewPwField: TextInputEditText
    private lateinit var toggleCurrentPwVisibilityBtn: ImageView
    private lateinit var toggleNewPwVisibilityBtn: ImageView
    private lateinit var toggleCfPwVisibilityBtn: ImageView

    override fun bindUi() {
        currentPwField = binding.currentPwField
        changePwField = binding.changeNewPwField
        cfNewPwField = binding.cfNewPwInput

        toggleCurrentPwVisibilityBtn = binding.toggleCurrentPwVisibilityBtn
        toggleNewPwVisibilityBtn = binding.toggleNewPwVisibilityBtn
        toggleCfPwVisibilityBtn = binding.toggleCfPwVisibilityBtn
    }

    override fun initFields() {

    }

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
