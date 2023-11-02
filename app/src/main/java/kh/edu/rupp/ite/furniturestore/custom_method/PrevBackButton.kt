package kh.edu.rupp.ite.furniturestore.custom_method

import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kh.edu.rupp.ite.furniturestore.R

class PrevBackButton(private val activity: AppCompatActivity) {

     fun prevBack(backBtn: ImageView){
        backBtn.setOnClickListener {
           activity.onBackPressedDispatcher.onBackPressed()
        }
    }
}