package kh.edu.rupp.ite.furniturestore.view.activity

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivityCheckoutBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.ObjectPayment
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.viewmodel.PaymentViewModel

class CheckoutActivity : BaseActivity<ActivityCheckoutBinding>(ActivityCheckoutBinding::inflate) {

    private val paymentViewModel: PaymentViewModel by viewModels()
    private val paymentBtn: Button by lazy { binding.paymentBtn }
    private val abaCheckBox: CheckBox by lazy { binding.abaCheckbox }
    private val acledaCheckBox: CheckBox by lazy { binding.acledaCheckBox }


    private lateinit var totalPrice: TextView

    private val actionBarView: View by lazy {
        showCustomActionBar(this, R.layout.activity_action_bar)
    }

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

        //show action bar
        actionBarView.apply {
            findViewById<TextView>(R.id.title_action_bar)?.apply {
                text = getString(R.string.checkout)
            }

            // Set up back button navigation
            findViewById<ImageView>(R.id.backPrev)?.setOnClickListener {
                prevBack(it)
            }
        }

        abaCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                acledaCheckBox.isChecked = false
            }
        }

        acledaCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
           if (isChecked){
               abaCheckBox.isChecked = false
           }
        }


    }

    override fun setupListeners() {

    }

    override fun setupObservers() {

    }

    private fun handlePaymentListener(value: List<ObjectPayment>) {
        paymentBtn.setOnClickListener {
            if (!abaCheckBox.isChecked && !acledaCheckBox.isChecked) {
                Snackbar.make(binding.root, "Please select a credit card.", Snackbar.LENGTH_SHORT).show()
            }else {
                paymentViewModel.payment(value)
                paymentViewModel.resMessage.observe(this) { res ->
                    when (res.status) {
                        Status.Processing -> {

                        }

                        Status.Success -> {
                            //navigate to payment Success Activity
                            val paymentSuccessActivity = Intent(this, PaymentSuccessActivity::class.java)
                            startActivity(paymentSuccessActivity)
                        }

                        Status.Failed -> {

                        }

                        else -> {}
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
}
