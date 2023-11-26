package kh.edu.rupp.ite.furniturestore.view.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.custom_method.PrevBackButton
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.model.User
import kh.edu.rupp.ite.furniturestore.viewmodel.AuthViewModel

class EditProfileActivity : AppCompatActivity() {

    private lateinit var prevBackButton: PrevBackButton
    private lateinit var backBtn: ImageView
    private lateinit var editAvatarBtn: ImageView
    private lateinit var name: TextInputEditText
    private lateinit var avatar: ImageView
    private lateinit var saveBtn: Button

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        avatar = findViewById(R.id.profile)
        name = findViewById(R.id.username)
        editAvatarBtn = findViewById(R.id.editAvatarBtn)
        saveBtn = findViewById(R.id.saveBtn)

        authViewModel.loadProfile()
        authViewModel.userData.observe(this) {
            when (it.status) {
                Status.Success -> {
                    it.data?.let { it1 ->
                        displayUi(it1)
                    }
                }

                Status.Failed -> {

                }

                else -> {

                }
            }
        }

        //handle upload change profile
        editAvatarBtn.setOnClickListener {
            openImageChooser()
        }


        //handle save button to submit api
        saveBtn.setOnClickListener {
            val getName = name.text.toString()
            authViewModel.updateProfile(getName, null)
        }

        //back to prev activity
        backBtn = findViewById(R.id.backBtn)
        prevBackButton = PrevBackButton(this)
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
            avatar.setImageBitmap(selectedImage)
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    private fun displayUi(data: User) {
        if (data.avatar.isEmpty()) {
            Picasso.get()
                .load(R.drawable.ic_pf)
                .placeholder(R.drawable.loading)    // Add a placeholder image
                .error(R.drawable.ic_error) // Add an error image
                .into(avatar);
            Picasso.get().isLoggingEnabled = true;
        }
        Picasso.get()
            .load(data.avatar)
            .placeholder(R.drawable.loading) // Add a placeholder image
            .error(R.drawable.ic_error) // Add an error image
            .into(avatar);

        name.setText(data.name)
    }
}
