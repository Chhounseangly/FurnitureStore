package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kh.edu.rupp.ite.furniturestore.R

class CodeVerificationActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_verification)

        //return to prev activity
        prevBack()
    }
    private fun prevBack(){
        val backBtn = findViewById<ImageView>(R.id.backBtn)

        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

}