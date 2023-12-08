package kh.edu.rupp.ite.furniturestore.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.model.User
import kh.edu.rupp.ite.furniturestore.view.activity.auth.ChangePasswordActivity
import kh.edu.rupp.ite.furniturestore.viewmodel.AuthViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var editProfileBtn: Button
    private lateinit var changePwBtn: Button
    private lateinit var logoutBtn: Button
    private lateinit var profile: ImageView
    private lateinit var username: TextView


    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        authViewModel.loadProfile()

        val intentChangePasswordActivity = Intent(this, ChangePasswordActivity::class.java)

        editProfileBtn = findViewById(R.id.editAvatarBtn)
        changePwBtn = findViewById(R.id.changePwBtn)
        username = findViewById(R.id.username)
//        profile = findViewById(R.id.profile)
        logoutBtn = findViewById(R.id.logoutBtn)
        //route to change password activity screen
        changePwBtn.setOnClickListener {
            startActivity(intentChangePasswordActivity)
        }


        //display data
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

        logoutBtn.setOnClickListener {
            logOut()
        }

        //route to edit profile activity screen
        editProfileBtn.setOnClickListener {
            val intentEditProfileActivity = Intent(this, EditProfileActivity::class.java)
//            intentEditProfileActivity.putExtra("profile", image)
            startActivity(intentEditProfileActivity)
        }
        //call method prev back
        prevBack()
    }

    private fun displayUi(data: User){
        val avatar = findViewById<ImageView>(R.id.profile)
        Picasso.get()
            .load(data.avatar)
            .placeholder(R.drawable.loading) // Add a placeholder image
            .error(R.drawable.ic_error) // Add an error image
            .into(avatar)
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

    //method prev back
    private fun prevBack() {
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


}