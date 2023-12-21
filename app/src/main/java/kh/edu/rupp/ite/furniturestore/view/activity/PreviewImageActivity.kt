package kh.edu.rupp.ite.furniturestore.view.activity

import android.widget.ImageView
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.databinding.ActivityPreviewImageBinding

class PreviewImageActivity :
    BaseActivity<ActivityPreviewImageBinding>(ActivityPreviewImageBinding::inflate) {

    private val previewImg: ImageView by lazy { binding.previewImg }

    override fun initActions() {
        val intent = intent
        val imageUrl = intent.getStringExtra("imageUrl")
        Picasso.get().load(imageUrl).into(previewImg)
    }

    override fun setupListeners() {
        prevBack(binding.closePreview)
    }

    override fun setupObservers() {

    }
}
