package kh.edu.rupp.ite.furniturestore.view.activity

import android.widget.ImageView
import android.widget.LinearLayout
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.databinding.ActivityPreviewImageBinding

class PreviewImageActivity :
    BaseActivity<ActivityPreviewImageBinding>(ActivityPreviewImageBinding::inflate) {

    private lateinit var previewImg: ImageView
    private lateinit var close: LinearLayout

    override fun bindUi() {
        previewImg = binding.previewImg
        close = binding.closePreview
    }

    override fun initFields() {

    }

    override fun initActions() {
        val intent = intent
        val imageUrl = intent.getStringExtra("imageUrl")
        Picasso.get().load(imageUrl).into(previewImg)
    }

    override fun setupListeners() {
        close.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun setupObservers() {

    }
}
