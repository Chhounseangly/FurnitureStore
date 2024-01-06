package kh.edu.rupp.ite.furniturestore.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivityProfileBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.model.User
import kh.edu.rupp.ite.furniturestore.utility.AppPreference
import kh.edu.rupp.ite.furniturestore.view.activity.auth.ChangePasswordActivity
import kh.edu.rupp.ite.furniturestore.viewmodel.AuthViewModel

class ProfileActivity : BaseActivity<ActivityProfileBinding>(ActivityProfileBinding::inflate) {

    private val authViewModel: AuthViewModel by viewModels()
    private val editProfileBtn: Button by lazy { binding.editAvatarBtn }
    private val changePwBtn: Button by lazy { binding.changePwBtn }
    private val logoutBtn: Button by lazy { binding.logoutBtn }
    private val profile: ImageView by lazy { binding.profile }
    private val username: TextView by lazy { binding.username }
    private val languageBtn: Button by lazy { binding.changeLanguageBtn }

    private val switchTheme: Button by lazy { binding.switchThemes }

    private lateinit var editProfileLauncher: ActivityResultLauncher<Intent>
    private lateinit var changePasswordLauncher: ActivityResultLauncher<Intent>
    private val actionBarView: View by lazy {
        showCustomActionBar(this, R.layout.activity_action_bar)
    }
    override fun initActions() {

        //show action bar
        actionBarView.apply {
            findViewById<TextView>(R.id.title_action_bar)?.apply {
                text = getString(R.string.profile)
            }

            // Set up back button navigation
            findViewById<ImageView>(R.id.backPrev)?.setOnClickListener {
                prevBack(it)
            }
        }
        authViewModel.loadProfile()

        // Initialize the ActivityResultLauncher for edit profile
        setupEditProfileLauncher()
        setupChangePasswordLauncher()
    }

    override fun setupListeners() {
        changePwBtn.setOnClickListener {
            changePasswordLauncher.launch(Intent(this, ChangePasswordActivity::class.java))
        }

        editProfileBtn.setOnClickListener {
            editProfileLauncher.launch(Intent(this, EditProfileActivity::class.java))
        }

        logoutBtn.setOnClickListener {
            logOut()
        }

        languageBtn.setOnClickListener {
            val language = resources.configuration.locale.language
            if (language == "en") {
                changeLanguage("km")
            } else {
                changeLanguage("en")
            }
            recreate()
        }
        val themes = getSharedPreferences("Mode", Context.MODE_PRIVATE)

        switchTheme.setOnClickListener {
            val editMode: SharedPreferences.Editor?
            val nightMode = themes.getBoolean("night", false)
            if (nightMode){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editMode = themes.edit()
                editMode.putBoolean("night", false)
            }else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editMode = themes.edit()
                editMode.putBoolean("night", true)
            }
            editMode.apply()

        }
    }

    override fun setupObservers() {
        authViewModel.userData.observe(this) {
            when (it.status) {
                Status.Success -> {
                    it.data?.let { data ->
                        displayUi(data.data)
                    }
                }

                Status.Unauthorized -> {
                    finish()
                }

                Status.Failed -> {
                    Snackbar.make(
                        binding.root,
                        R.string.error_occurred,
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                else -> {

                }
            }
        }
    }

    private fun setupChangePasswordLauncher() {
        changePasswordLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    Snackbar.make(
                        binding.root,
                        R.string.password_update_success,
                        Snackbar.LENGTH_LONG
                    ).show()
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
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    authViewModel.loadProfile()

                    Snackbar.make(
                        binding.root,
                        R.string.profile_update_success,
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                else -> {

                }
            }
        }
    }

    private fun displayUi(data: User) {
        Picasso.get()
            .load(data.avatar)
            .placeholder(loadingImg(this))
            .error(R.drawable.ic_error) // Add an error image
            .into(profile)
        username.text = data.name
    }

    private fun logOut() {
        authViewModel.logout()
        authViewModel.resMsg.observe(this) {
            when (it.status) {
                Status.Success -> {
                    finish()
                }

                else -> {

                }
            }
        }
    }

    // Function to change the language
    private fun changeLanguage(languageCode: String) {
        AppPreference.get(this).setLanguage(languageCode)

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
