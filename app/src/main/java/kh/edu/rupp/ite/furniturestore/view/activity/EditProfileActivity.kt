package kh.edu.rupp.ite.furniturestore.view.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.custom_method.PrevBackButton

class EditProfileActivity: AppCompatActivity() {
    private lateinit var prevBackButton: PrevBackButton
    private lateinit var backBtn: ImageView
    private lateinit var editProfileBtn: ImageButton
    private lateinit var profile: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        backBtn = findViewById(R.id.backBtn)
        prevBackButton = PrevBackButton(this)
        prevBackButton.prevBack(backBtn)

        editProfileBtn = findViewById(R.id.editProfileBtn)
        profile = findViewById(R.id.profile)


        //handle upload change profile
        editProfileBtn.setOnClickListener {
            openImageChooser()
        }
        val intent = intent
        val getProfile = intent.getStringExtra("profile")

        Picasso.get().load(getProfile).into(profile)

        //back to prev activity
        prevBackButton.prevBack(backBtn)

    }

    private fun openImageChooser() {
        val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(openGallery, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            val imageStream = imageUri?.let {
                contentResolver.openInputStream(it)
            }
            val selectedImage = BitmapFactory.decodeStream(imageStream)
            profile.setImageBitmap(selectedImage)
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
