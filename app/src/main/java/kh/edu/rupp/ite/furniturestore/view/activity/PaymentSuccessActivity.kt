package kh.edu.rupp.ite.furniturestore.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kh.edu.rupp.ite.furniturestore.R

class PaymentSuccessActivity: AppCompatActivity() {

    private lateinit var continueBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_success)

        continueBtn = findViewById(R.id.continueBtn)
        continueBtn.setOnClickListener {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(mainActivityIntent)
        }
    }
}