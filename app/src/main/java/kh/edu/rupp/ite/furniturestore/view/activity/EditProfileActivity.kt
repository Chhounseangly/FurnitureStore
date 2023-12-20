package kh.edu.rupp.ite.furniturestore.view.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.custom_method.PrevBackButton
import kh.edu.rupp.ite.furniturestore.databinding.ActivityEditProfileBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.model.User
import kh.edu.rupp.ite.furniturestore.viewmodel.AuthViewModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

class EditProfileActivity :
    BaseActivity<ActivityEditProfileBinding>(ActivityEditProfileBinding::inflate) {

    private lateinit var prevBackButton: PrevBackButton
    private lateinit var backBtn: ImageView
    private lateinit var editAvatarBtn: ImageView
    private lateinit var name: TextInputEditText
    private lateinit var avatar: ImageView
    private lateinit var saveBtn: Button

    private lateinit var authViewModel: AuthViewModel

    // ActivityResultLauncher to handle image selection result
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>


    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun bindUi() {
        avatar = binding.profile
        name = binding.username
        editAvatarBtn = binding.editAvatarBtn
        saveBtn = binding.saveBtn
        backBtn = binding.backBtn
    }

    override fun initFields() {
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        prevBackButton = PrevBackButton(this)
    }

    override fun initActions() {
        // Load user profile data
        authViewModel.loadProfile()

        // Set up back button navigation
        prevBackButton.prevBack(backBtn)

        // Initialize the ActivityResultLauncher for image picking
//        setupImagePickerLauncher()
    }

    override fun setupListeners() {
        // Handle click on "Edit Avatar" button to open image chooser
        editAvatarBtn.setOnClickListener {
            openImageChooser()
        }

        // Handle click on "Save" button to update the user's profile
        saveBtn.setOnClickListener {
            val name = RequestBody.create(MediaType.parse("text/plain"), name.text.toString())

            val drawable: Drawable? = avatar.drawable

            if (drawable is BitmapDrawable) {
                val bitmap: Bitmap = drawable.bitmap

                // Convert Bitmap to ByteArray
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()

                // Now 'byteArray' contains the image data in bytes
                // You can proceed to upload this to your API using Retrofit
                // Assuming 'byteArray' contains the image data
                val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray)
                val imagePart = MultipartBody.Part.createFormData("avatar", "image.jpg", requestFile)

                authViewModel.updateProfile(name, imagePart)
            } else {
                // Handle the case where the drawable is not a BitmapDrawable
                authViewModel.updateProfile(name, null)
            }

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
        }
    }

    override fun setupObservers() {
        authViewModel.userData.observe(this) {
            when (it.status) {
                Status.Success -> {
                    it.data?.let { userData ->
                        // Display user profile data in the UI
                        displayUi(userData)
                    }
                }

                Status.Failed -> {
                    // Handle failure
                }

                else -> {
                    // Handle other cases
                }
            }
        }
    }

    // Function to open the image chooser for selecting a new profile picture
    private fun openImageChooser() {
        val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(openGallery, PICK_IMAGE_REQUEST)
    }

    // Function to set up the ActivityResultLauncher for image picking
    @Deprecated("Deprecated in Java")
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

    // Function to display user profile data in the UI
    private fun displayUi(userData: User) {
        // Use Picasso library to load and display the user's avatar
        Picasso.get()
            .load(userData.avatar)
            .placeholder(R.drawable.loading) // Placeholder image while loading
            .error(R.drawable.ic_error) // Error image if loading fails
            .into(avatar)

        // Set the user's name in the TextInputEditText
        name.setText(userData.name)
    }
}
