package kh.edu.rupp.ite.furniturestore.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivityMainBinding
import kh.edu.rupp.ite.furniturestore.ui.activity.auth.SignInActivity
import kh.edu.rupp.ite.furniturestore.ui.fragments.CartFragment
import kh.edu.rupp.ite.furniturestore.ui.fragments.FavoriteFragment
import kh.edu.rupp.ite.furniturestore.ui.fragments.HomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        //click on title of app go back to home fragment and set menu home active
        activityMainBinding.titleTxt?.setOnClickListener {
            displayFragment(HomeFragment())
            activityMainBinding.bottomNavigationView.selectedItemId = R.id.mnuHome
        }

        //display home fragment when starting app
        displayFragment(HomeFragment())

        val signInScreen = Intent(this, SignInActivity::class.java)

        //action on bottom nav_bar when user click menu
        activityMainBinding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.mnuHome -> displayFragment(HomeFragment())
                R.id.mnuFav -> displayFragment(FavoriteFragment())
                R.id.mnuCart -> displayFragment(CartFragment())
                else -> startActivity(signInScreen)
//                else -> displayFragment(ProfileFragment())
            }
            true
        }
    }


    //function display Fragments
    private fun displayFragment (fragment: Fragment) {

        val fragmentTransaction  = supportFragmentManager.beginTransaction()
        // Replace fragment in lytFragment
        fragmentTransaction.replace(R.id.lytFragment, fragment)
        // Commit transaction
        fragmentTransaction.commit()
    }

}