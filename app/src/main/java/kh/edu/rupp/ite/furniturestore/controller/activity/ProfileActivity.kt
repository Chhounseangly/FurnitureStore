package kh.edu.rupp.ite.furniturestore.controller.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.controller.activity.auth.ChangePasswordActivity
import kh.edu.rupp.ite.furniturestore.model.api.model.User
import kh.edu.rupp.ite.furniturestore.model.api.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileActivity: AppCompatActivity() {

    private lateinit var editProfileBtn: Button
    private lateinit var changePwBtn: Button
    private lateinit var profile: ImageView
    private val BASE_URL = "https://api.genderize.io/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val intentChangePasswordActivity = Intent(this, ChangePasswordActivity::class.java)

        editProfileBtn = findViewById(R.id.editProfileBtn)
        changePwBtn = findViewById(R.id.changePwBtn)
        profile = findViewById(R.id.profile)

        //route to change password activity screen
        changePwBtn.setOnClickListener {
            startActivity(intentChangePasswordActivity)
        }
        val image = "https://i4.komnit.com/store/upload/images/express_2207/112290-ARJDYN/1657316942-ARJDYN.jpg"
         Picasso.get().load(image).into(profile)

        //route to edit profile activity screen
        editProfileBtn.setOnClickListener {
            val intentEditProfileActivity = Intent(this, EditProfileActivity::class.java)
            intentEditProfileActivity.putExtra("profile", image)
            startActivity(intentEditProfileActivity)
        }
        //call method prev back
        prevBack()
        profileFetching()
    }


    //method prev back
    private fun prevBack() {
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun profileFetching() {

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)

        val call = api.getUser()

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Toast.makeText(
                            this@ProfileActivity,
                            it.name.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Log.d("onResponse", "Error: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("user", "falid 2 $t")

            }
        })
    }

}