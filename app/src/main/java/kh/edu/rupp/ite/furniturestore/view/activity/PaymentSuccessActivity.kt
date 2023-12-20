package kh.edu.rupp.ite.furniturestore.view.activity

import android.content.Intent
import android.widget.Button
import kh.edu.rupp.ite.furniturestore.databinding.ActivityPaymentSuccessBinding

class PaymentSuccessActivity :
    BaseActivity<ActivityPaymentSuccessBinding>(ActivityPaymentSuccessBinding::inflate) {

    private lateinit var continueBtn: Button

    override fun bindUi() {
        continueBtn = binding.continueBtn
    }

    override fun initFields() {

    }

    override fun initActions() {

    }

    override fun setupListeners() {
        continueBtn.setOnClickListener {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            mainActivityIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(mainActivityIntent)
        }
    }

    override fun setupObservers() {

    }
}
