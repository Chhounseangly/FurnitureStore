package kh.edu.rupp.ite.furniturestore.ui.activity.auth

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kh.edu.rupp.ite.furniturestore.R


class ForgotPasswordActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        var backBtn = findViewById<ImageView>(R.id.backBtn)
//        var signInScreen = Intent(this, SignInActivity::class.java)

        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


}