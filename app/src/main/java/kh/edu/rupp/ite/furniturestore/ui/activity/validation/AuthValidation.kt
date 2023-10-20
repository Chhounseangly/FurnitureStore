package kh.edu.rupp.ite.furniturestore.ui.activity.validation

import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText

class AuthValidation {

    //handle dynamic input field
     fun handleOnChangeEditText(obj: EditText){
        obj.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                obj.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                obj.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
            }

            override fun afterTextChanged(s: Editable) {
                obj.backgroundTintList = null
            }
        })
    }

    //function validation checked
     fun checkAllFields(obj: EditText): Boolean {
        if (obj.text?.isEmpty() == true) {
            obj.error = "This field is required"
            obj.backgroundTintList = ColorStateList.valueOf(Color.RED)
            return false
        }
        return true
    }


    //validation password
    fun passwordChecked(pw: EditText, cfPw: EditText?): Boolean {

        // Check if the password field is empty.
        if (pw.text.isEmpty()) {
            pw.error = "This field is required"
            pw.backgroundTintList = ColorStateList.valueOf(Color.RED)
            return false
        }

        // Check if the password field is at least 8 characters long.
        if (pw.text.length < 8) {
            pw.error = "Must be at least 8 characters"
            pw.backgroundTintList = ColorStateList.valueOf(Color.RED)
            return false
        }

        // Check if the confirmation password field is empty.
        if (cfPw?.text?.isEmpty() == true) {
            cfPw.error = "This field is required"
            cfPw.backgroundTintList = ColorStateList.valueOf(Color.RED)
            return false
        }

        if (cfPw != null){
            // Check if the confirmation password field is at least 8 characters long.
            if (cfPw.text.length < 8) {
                cfPw.error = "Must be at least 8 characters"
                cfPw.backgroundTintList = ColorStateList.valueOf(Color.RED)
                return false
            }
        // Check the confirmation password.
             if (pw.text.toString() != cfPw.text.toString()){
                 // The passwords are not the same.
                 cfPw.error = "Passwords must be the same"
                 cfPw.backgroundTintList = ColorStateList.valueOf(Color.RED)
                 return  false
             }
        }

        // The passwords are valid.
        return true
    }

}