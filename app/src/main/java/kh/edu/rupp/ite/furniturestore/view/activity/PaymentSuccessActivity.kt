package kh.edu.rupp.ite.furniturestore.view.activity

import android.content.Intent
import android.widget.Button
import kh.edu.rupp.ite.furniturestore.databinding.ActivityPaymentSuccessBinding

class PaymentSuccessActivity :
    BaseActivity<ActivityPaymentSuccessBinding>(ActivityPaymentSuccessBinding::inflate) {

    private val continueBtn: Button by lazy { binding.continueBtn }

    override fun initActions() {
        supportActionBar?.hide()
    }

    override fun setupListeners() {
        continueBtn.setOnClickListener {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
        }
    }

    override fun setupObservers() {

    }
}
