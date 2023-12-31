package kh.edu.rupp.ite.furniturestore.view.activity.validation

import android.content.res.ColorStateList
import android.content.res.Resources.Theme
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat.ThemeCompat
import androidx.core.content.res.ResourcesCompat.getColorStateList

class AuthValidation {

    private fun isValidEmail(email: EditText): Boolean {
        val regex = Regex("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}\$")
        return regex.matches(email.text.toString())
    }

    fun signInValidation(email: EditText, password: EditText): Boolean {
        val emailCheck = checkFields(email)
        val passwordCheck = checkFields(password)
        if (emailCheck && passwordCheck) {
            return if (!isValidEmail(email)) {
                email.error = "Email invalid address"
                email.backgroundTintList = ColorStateList.valueOf(Color.RED)
                false
            } else if (password.text.length < 8) {
                password.error = "Must be at least 8 characters"
                password.backgroundTintList = ColorStateList.valueOf(Color.RED)
                return false
            } else true
        } else if (emailCheck) {
            return if (!isValidEmail(email)) {
                email.error = "Email invalid address"
                false
            } else true
        } else if (passwordCheck) {
            return if (password.text.length < 8) {
                password.error = "password must be at least 8 characters"
                password.backgroundTintList = ColorStateList.valueOf(Color.RED)
                return false
            } else true
        }
        return false
    }

    fun forgotPasswordValidation(emailVerify:EditText): Boolean {
        val emailVerifyCheck = checkFields(emailVerify)
        if (emailVerifyCheck){
            return if (!isValidEmail(emailVerify)){
                emailVerify.error = "Email invalid address"
                emailVerify.backgroundTintList = ColorStateList.valueOf(Color.RED)
                false
            }else true
        }

        return false

    }

    //handle dynamic input field
    fun handleOnChangeEditText(obj: EditText) {
        obj.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                obj.backgroundTintList = ColorStateList.valueOf(Color.CYAN)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                obj.backgroundTintList = ColorStateList.valueOf(Color.CYAN)
            }

            override fun afterTextChanged(s: Editable) {
                obj.backgroundTintList = ColorStateList.valueOf(Color.CYAN)
            }
        })
    }

    //function validation checked
    private fun checkFields(obj: EditText): Boolean {
        if (obj.text?.isEmpty() == true) {
            obj.error = "This field is required"
            obj.backgroundTintList = ColorStateList.valueOf(Color.RED)
            return false
        }
        return true
    }

}