package kh.edu.rupp.ite.furniturestore.view.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R

class PreviewImageActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_image)
        val intent = intent
        val imageUrl = intent.getStringExtra("imageUrl")

        val previewImg = findViewById<ImageView>(R.id.previewImg)
        val close  = findViewById<LinearLayout>(R.id.closePreview)

        Picasso.get().load(imageUrl).into(previewImg)

        close.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

}