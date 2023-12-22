package kh.edu.rupp.ite.furniturestore.view.activity

import android.app.Activity
import android.content.Intent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivityProfileBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.model.User
import kh.edu.rupp.ite.furniturestore.view.activity.auth.ChangePasswordActivity
import kh.edu.rupp.ite.furniturestore.viewmodel.AuthViewModel

class ProfileActivity : BaseActivity<ActivityProfileBinding>(ActivityProfileBinding::inflate) {

    private val authViewModel: AuthViewModel by viewModels()
    private val editProfileBtn: Button by lazy { binding.editAvatarBtn }
    private val changePwBtn: Button by lazy { binding.changePwBtn }
    private val logoutBtn: Button by lazy { binding.logoutBtn }
    private val profile: ImageView by lazy { binding.profile }
    private val username: TextView by lazy { binding.username }

    private lateinit var editProfileLauncher: ActivityResultLauncher<Intent>

    private companion object {
        const val TOAST_SUCCESS_MESSAGE = "Profile updated successfully"
        const val TOAST_FAILURE_MESSAGE = "Failed to update profile"
    }

    override fun initActions() {
        authViewModel.loadProfile()
        prevBack(binding.backBtn)

        // Initialize the ActivityResultLauncher for edit profile
        setupEditProfileLauncher()
    }

    override fun setupListeners() {
        val intentChangePasswordActivity = Intent(this, ChangePasswordActivity::class.java)
        changePwBtn.setOnClickListener {
            startActivity(intentChangePasswordActivity)
        }

        logoutBtn.setOnClickListener {
            logOut()
        }

        //route to edit profile activity screen
        editProfileBtn.setOnClickListener {
            editProfileLauncher.launch(Intent(this, EditProfileActivity::class.java))
        }
    }

    override fun setupObservers() {
        authViewModel.userData.observe(this) {
            when (it.status) {
                Status.Success -> {
                    it.data?.let { data ->
                        displayUi(data)
                    }
                }

                else -> {

                }
            }
        }
    }

    private fun setupEditProfileLauncher() {
        editProfileLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                authViewModel.loadProfile()

                Snackbar.make(
                    binding.root,
                    TOAST_SUCCESS_MESSAGE,
                    Snackbar.LENGTH_LONG
                ).show()
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(
                    binding.root,
                    TOAST_FAILURE_MESSAGE,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun displayUi(data: User) {
        Picasso.get()
            .load(data.avatar)
            .placeholder(R.drawable.loading) // Add a placeholder image
            .error(R.drawable.ic_error) // Add an error image
            .into(profile)
        username.text = data.name
    }

    private fun logOut() {
        authViewModel.logout()
        authViewModel.resMsg.observe(this) {
            when (it.status) {
                Status.Success -> {
                    val mainActivityIntent = Intent(this, MainActivity::class.java)
                    mainActivityIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(mainActivityIntent)
                }

                else -> {

                }
            }
        }
    }
}
