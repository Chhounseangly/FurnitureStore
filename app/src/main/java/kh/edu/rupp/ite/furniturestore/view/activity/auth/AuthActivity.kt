package kh.edu.rupp.ite.furniturestore.view.activity.auth

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.viewbinding.ViewBinding
import kh.edu.rupp.ite.furniturestore.view.activity.BaseActivity
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

abstract class AuthActivity<T : ViewBinding>(bindingInflater: (LayoutInflater) -> T) :
    BaseActivity<T>(bindingInflater) {

    // ActivityResultLauncher to handle image selection result
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var imagePart: MultipartBody.Part

    private var isImageChanged = false

    private companion object {
        const val MEDIA_TYPE_IMAGE = "image/jpeg"
        const val IMAGE_FORM_DATA_NAME = "avatar"
        const val IMAGE_FILE_NAME = "image.jpg"
    }

    // Function to open the image chooser for selecting a new profile picture
     fun openImageChooser() {
        val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(openGallery)
    }

    // Function to set up the ActivityResultLauncher for image picking
     fun setupImagePickerLauncher(avatar: ImageView) {
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                // Process the result
                data?.let { handleImageChooserResult(it, avatar) }
            }
        }
    }

    fun getAvatar(): MultipartBody.Part {
        return imagePart
    }

    fun isImageChanged(): Boolean {
        return isImageChanged
    }

    private fun handleImageChooserResult(data: Intent, avatar: ImageView) {
        val imageUri = data.data

        // Check if the image URI is not null
        if (imageUri != null) {
            try {
                val bitmap = decodeBitmapFromUri(imageUri)
                avatar.setImageBitmap(bitmap)
                imagePart = createImagePart(bitmap)
                isImageChanged = true
            } catch (e: Exception) {
                // Handle image processing errors
                Log.e("ImageChooser", "Error processing image", e)
            }
        } else {
            // Handle the case where the image URI is null
            // You may want to show an error message or take appropriate action
            Log.e("ImageChooser", "Image URI is null")
        }
    }

    private fun decodeBitmapFromUri(uri: Uri): Bitmap {
        val imageStream = contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(imageStream)
            ?: throw IllegalArgumentException("Unable to decode bitmap from the provided URI")
    }

    private fun createImagePart(bitmap: Bitmap): MultipartBody.Part {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        val requestFile = RequestBody.create(MediaType.parse(MEDIA_TYPE_IMAGE), byteArray)
        return MultipartBody.Part.createFormData(
            IMAGE_FORM_DATA_NAME,
            IMAGE_FILE_NAME, requestFile)
    }
}