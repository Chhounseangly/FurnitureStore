package kh.edu.rupp.ite.furniturestore.view.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.custom_method.PrevBackButton
import kh.edu.rupp.ite.furniturestore.databinding.ActivityEditProfileBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.model.User
import kh.edu.rupp.ite.furniturestore.viewmodel.AuthViewModel

class EditProfileActivity :
    BaseActivity<ActivityEditProfileBinding>(ActivityEditProfileBinding::inflate) {

    private lateinit var prevBackButton: PrevBackButton
    private lateinit var backBtn: ImageView
    private lateinit var editAvatarBtn: ImageView
    private lateinit var name: TextInputEditText
    private lateinit var avatar: ImageView
    private lateinit var saveBtn: Button

    private lateinit var authViewModel: AuthViewModel

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
        // Load profile
        authViewModel.loadProfile()

        // Back to prev activity
        prevBackButton.prevBack(backBtn)
    }

    override fun setupListeners() {
        // Handle upload change profile
        editAvatarBtn.setOnClickListener {
            openImageChooser()
        }

        // Handle save button to submit api
        saveBtn.setOnClickListener {
            val getName = name.text.toString()
            authViewModel.updateProfile(getName, null)
        }
    }

    override fun setupObservers() {
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
        Picasso.get()
            .load(data.avatar)
            .placeholder(R.drawable.loading) // Add a placeholder image
            .error(R.drawable.ic_error) // Add an error image
            .into(avatar);

        name.setText(data.name)
    }
}
