package kh.edu.rupp.ite.furniturestore.view.activity

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.custom_method.PrevBackButton

class CheckoutActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)



        val prevBtn = findViewById<ImageView>(R.id.backBtn)
        prevBtn.setOnClickListener{
            PrevBackButton(this).prevBack(prevBtn)
        }
    }
}