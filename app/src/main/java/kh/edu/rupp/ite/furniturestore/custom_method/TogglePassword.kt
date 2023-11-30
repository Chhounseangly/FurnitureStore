package kh.edu.rupp.ite.furniturestore.custom_method

import android.os.Handler
import android.text.InputType
import android.widget.ImageView
import com.google.android.material.textfield.TextInputEditText
import kh.edu.rupp.ite.furniturestore.R

class TogglePassword {

    fun togglePasswordVisibility(editText: TextInputEditText, toggleButton: ImageView) {
        toggleButton.setOnClickListener {
            if (editText.inputType != InputType.TYPE_TEXT_VARIATION_PASSWORD){
                toggleButton.setImageResource(R.drawable.ic_visible_pw)
                editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD

                // Revert the password field back to its original visibility state after 5000 milliseconds
                Handler().postDelayed({
                    toggleButton.setImageResource(R.drawable.ic_invisible_pw)
                    editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }, 1000)
            }else{
                toggleButton.setImageResource(R.drawable.ic_invisible_pw)
                editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
    }

}