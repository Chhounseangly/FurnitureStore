package kh.edu.rupp.ite.furniturestore.view.activity

import android.content.Context
import android.content.Intent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivityCheckoutBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.ObjectPayment
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.viewmodel.PaymentViewModel

class CheckoutActivity : BaseActivity<ActivityCheckoutBinding>(ActivityCheckoutBinding::inflate) {
    private lateinit var paymentViewModel: PaymentViewModel
    private lateinit var paymentBtn: Button
    private lateinit var totalPrice: TextView

    override fun bindUi() {
        paymentBtn = binding.paymentBtn
    }

    override fun initFields() {
        paymentViewModel = ViewModelProvider(this)[PaymentViewModel::class.java]
    }

    override fun initActions() {
        val shoppingCartList = intent.getParcelableArrayListExtra<ObjectPayment>(EXTRA_LIST)

        if (shoppingCartList != null) {
            displayUi(shoppingCartList)
            handlePaymentListener(shoppingCartList)
        }

        prevBack()
    }

    override fun setupListeners() {

    }

    override fun setupObservers() {

    }

    private fun handlePaymentListener(value: List<ObjectPayment>){
        paymentBtn.setOnClickListener {
            paymentViewModel.payment(value)
            paymentViewModel.responseMessage.observe(this){ res ->
                when(res.status){
                    Status.Processing->{

                    }
                    Status.Success -> {
                        //navigate to payment Success Activity
                        val paymentSuccessActivity = Intent(this, PaymentSuccessActivity::class.java)
                        startActivity(paymentSuccessActivity)
                    }
                    Status.Failed -> {

                    }
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

    private fun prevBack() {
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    companion object {
        private const val EXTRA_LIST = "shoppingCartObject"
        fun newIntent(context: Context, shoppingCart: List<ObjectPayment>): Intent{
            val intent = Intent(context, CheckoutActivity::class.java)
            intent.putParcelableArrayListExtra(EXTRA_LIST, ArrayList(shoppingCart))
            return intent
        }
    }
}