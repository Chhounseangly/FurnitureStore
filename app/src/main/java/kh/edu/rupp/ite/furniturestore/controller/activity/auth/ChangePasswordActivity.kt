package kh.edu.rupp.ite.furniturestore.controller.activity.auth

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.custom_method.PrevBackButton
import kh.edu.rupp.ite.furniturestore.custom_method.TogglePassword

class ChangePasswordActivity: AppCompatActivity() {

    private lateinit var currentPwField: TextInputEditText
    private lateinit var changePwField: TextInputEditText
    private lateinit var cfNewPwField: TextInputEditText
    private lateinit var toggleCurrentPwVisibilityBtn: ImageView
    private lateinit var toggleNewPwVisibilityBtn: ImageView
    private lateinit var toggleCfPwVisibilityBtn: ImageView
    private lateinit var prevBackButton: PrevBackButton
    private lateinit var backBtn: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        currentPwField = findViewById(R.id.currentPwField)
        changePwField = findViewById(R.id.changeNewPwField)
        cfNewPwField = findViewById(R.id.cfNewPwInput)

        toggleCurrentPwVisibilityBtn = findViewById(R.id.toggleCurrentPwVisibilityBtn)
        toggleNewPwVisibilityBtn = findViewById(R.id.toggleNewPwVisibilityBtn)
        toggleCfPwVisibilityBtn = findViewById(R.id.toggleCfPwVisibilityBtn)

        //current password
        TogglePassword().togglePasswordVisibility(currentPwField, toggleCurrentPwVisibilityBtn)
        //New password
        TogglePassword().togglePasswordVisibility(changePwField, toggleNewPwVisibilityBtn)
        //Confirm New password
        TogglePassword().togglePasswordVisibility(cfNewPwField, toggleCfPwVisibilityBtn)

        backBtn = findViewById(R.id.backBtn)
        prevBackButton = PrevBackButton(this)
        prevBackButton.prevBack(backBtn)

    }



}