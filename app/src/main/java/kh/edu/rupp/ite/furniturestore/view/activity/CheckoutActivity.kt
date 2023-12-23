package kh.edu.rupp.ite.furniturestore.view.activity

import android.content.Context
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivityCheckoutBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.ObjectPayment
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.viewmodel.PaymentViewModel

class CheckoutActivity : BaseActivity<ActivityCheckoutBinding>(ActivityCheckoutBinding::inflate) {

    private val paymentViewModel: PaymentViewModel by viewModels()
    private val paymentBtn: Button by lazy { binding.paymentBtn }
    private lateinit var totalPrice: TextView

    companion object {
        private const val EXTRA_LIST = "shoppingCartObject"
        fun newIntent(context: Context, shoppingCart: List<ObjectPayment>): Intent {
            val intent = Intent(context, CheckoutActivity::class.java)
            intent.putParcelableArrayListExtra(EXTRA_LIST, ArrayList(shoppingCart))
            return intent
        }
    }

    override fun initActions() {
        val shoppingCartList = intent.getParcelableArrayListExtra<ObjectPayment>(EXTRA_LIST)

        if (shoppingCartList != null) {
            displayUi(shoppingCartList)
            handlePaymentListener(shoppingCartList)
        }

        // Set up back button navigation
        prevBack(binding.backBtn)
    }

    override fun setupListeners() {

    }

    override fun setupObservers() {

    }

    private fun handlePaymentListener(value: List<ObjectPayment>) {
        paymentBtn.setOnClickListener {
            paymentViewModel.payment(value)
            paymentViewModel.resMessage.observe(this) { res ->
                when (res.status) {
                    Status.Processing -> {

                    }

                    Status.Success -> {
                        //navigate to payment Success Activity
                        val paymentSuccessActivity =
                            Intent(this, PaymentSuccessActivity::class.java)
                        startActivity(paymentSuccessActivity)
                    }

                    Status.Failed -> {

                    }

                    else -> {}
                }
            }
        }
    }

    private fun displayUi(data: List<ObjectPayment>) {
        totalPrice = findViewById(R.id.totalPrice)
        var total = 0.0
        for (i in data) {
            total += i.price * i.qty
        }
        totalPrice.text = total.toString()
    }
}
