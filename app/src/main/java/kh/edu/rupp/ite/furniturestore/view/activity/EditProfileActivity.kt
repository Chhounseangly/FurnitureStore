package kh.edu.rupp.ite.furniturestore.view.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
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

    private val authViewModel: AuthViewModel by viewModels()

    private val avatar: ImageView by lazy { binding.profile }
    private val name: TextInputEditText by lazy { binding.username }
    private val editAvatarBtn: ImageView by lazy { binding.editAvatarBtn }
    private val saveBtn: Button by lazy { binding.saveBtn }

    // ActivityResultLauncher to handle image selection result
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    private companion object {
        const val MEDIA_TYPE_IMAGE = "image/jpeg"
        const val IMAGE_FORM_DATA_NAME = "avatar"
        const val IMAGE_FILE_NAME = "image.jpg"
    }

    override fun initActions() {
        // Load user profile data
        authViewModel.loadProfile()

        // Set up back button navigation
        prevBack(binding.backBtn)

        // Initialize the ActivityResultLauncher for image picking
        setupImagePickerLauncher()
    }

    override fun setupListeners() {
        // Handle click on "Edit Avatar" button to open image chooser
        editAvatarBtn.setOnClickListener {
            openImageChooser()
        }

        // Handle click on "Save" button to update the user's profile
        saveBtn.setOnClickListener {
            saveUserProfile()
        }
    }

    override fun setupObservers() {
        authViewModel.userData.observe(this) {
            when (it.status) {
                Status.Success -> {
                    it.data?.let { userData -> displayUi(userData) }
                }

                Status.Failed -> {
                    // Handle failure
                }

                else -> {
                    // Handle other cases
                }
            }
        }

        authViewModel.updateMsg.observe(this) {
            when (it.status) {
                Status.Success -> {
                    setResult(Activity.RESULT_OK)
                    finish()
                }

                Status.Failed -> {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
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
        imagePickerLauncher.launch(openGallery)
    }

    // Function to set up the ActivityResultLauncher for image picking
    private fun setupImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                // Process the result
                data?.let { handleImageChooserResult(it) }
            }
        }
    }

    // Function to handle the result of image selection
    private fun handleImageChooserResult(data: Intent) {
        val imageUri = data.data
        val imageStream = imageUri?.let {
            contentResolver.openInputStream(it)
        }
        val selectedImage = BitmapFactory.decodeStream(imageStream)
        avatar.setImageBitmap(selectedImage)
    }

    // Function to handle click on the "Save" button
    private fun saveUserProfile() {
        val name = RequestBody.create(MediaType.parse("text/plain"), name.text.toString())

        val drawable: Drawable? = avatar.drawable

        if (drawable is BitmapDrawable) {
            val bitmap: Bitmap = drawable.bitmap

            // Convert Bitmap to ByteArray
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            val requestFile = RequestBody.create(MediaType.parse(MEDIA_TYPE_IMAGE), byteArray)
            val imagePart = MultipartBody.Part.createFormData(
                IMAGE_FORM_DATA_NAME,
                IMAGE_FILE_NAME,
                requestFile
            )

            // Update the user's profile with the new data
            authViewModel.updateProfile(name, imagePart)
        } else {
            // Handle the case where the drawable is not a BitmapDrawable
            authViewModel.updateProfile(name, null)
        }
    }

    // Function to display user profile data in the UI
    private fun displayUi(userData: User) {
        Picasso.get()
            .load(userData.avatar)
            .placeholder(R.drawable.loading)
            .error(R.drawable.ic_error)
            .into(avatar)

        name.setText(userData.name)
    }
}
