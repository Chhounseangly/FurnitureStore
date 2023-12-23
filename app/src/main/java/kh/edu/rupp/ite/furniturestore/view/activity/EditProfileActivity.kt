package kh.edu.rupp.ite.furniturestore.view.activity

import android.app.Activity
import android.widget.Button
import android.widget.ImageView
import androidx.activity.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivityEditProfileBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.model.User
import kh.edu.rupp.ite.furniturestore.view.activity.auth.AuthActivity
import kh.edu.rupp.ite.furniturestore.viewmodel.AuthViewModel
import okhttp3.MediaType
import okhttp3.RequestBody

class EditProfileActivity :
    AuthActivity<ActivityEditProfileBinding>(ActivityEditProfileBinding::inflate) {

    private val authViewModel: AuthViewModel by viewModels()

    private val avatar: ImageView by lazy { binding.profile }
    private val name: TextInputEditText by lazy { binding.username }
    private val editAvatarBtn: ImageView by lazy { binding.editAvatarBtn }
    private val saveBtn: Button by lazy { binding.saveBtn }

    override fun initActions() {
        authViewModel.loadProfile()
        prevBack(binding.backBtn)
        setupImagePickerLauncher(avatar)
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
                    finish()
                }

                else -> {
                    // Handle other cases
                }
            }
        }
    }

    // Function to handle click on the "Save" button
    private fun saveUserProfile() {
        val name = RequestBody.create(MediaType.parse("text/plain"), name.text.toString())
        if (isImageChanged()) {
            // Update the user's profile with the new data
            authViewModel.updateProfile(name, getAvatar())
        } else {
            // Handle the case where the drawable is not a BitmapDrawable
            authViewModel.updateProfile(name)
        }
    }

    // Function to display user profile data in the UI
    private fun displayUi(userData: User) {
        Picasso.get()
            .load(userData.avatar)
            .placeholder(loadingImg(this))
            .error(R.drawable.ic_error)
            .into(avatar)

        name.setText(userData.name)
    }
}
