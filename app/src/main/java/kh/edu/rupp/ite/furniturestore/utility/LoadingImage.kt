package kh.edu.rupp.ite.furniturestore.utility

import android.content.Context
import android.graphics.Color
import androidx.swiperefreshlayout.widget.CircularProgressDrawable

object LoadingImage {
    fun loadingImg(context: Context): CircularProgressDrawable{
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        return circularProgressDrawable
    }
}