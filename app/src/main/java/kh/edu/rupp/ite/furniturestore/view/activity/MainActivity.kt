package kh.edu.rupp.ite.furniturestore.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivityMainBinding
import kh.edu.rupp.ite.furniturestore.view.activity.auth.SignInActivity
import kh.edu.rupp.ite.furniturestore.view.fragments.ShoppingCartFragment
import kh.edu.rupp.ite.furniturestore.view.fragments.SearchFragment
import kh.edu.rupp.ite.furniturestore.view.fragments.FavoriteFragment
import kh.edu.rupp.ite.furniturestore.view.fragments.HomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    private var homeFragment = HomeFragment()
    private var searchFragment = SearchFragment()
    private var favoriteFragment = FavoriteFragment()
    private var shoppingCartFragment = ShoppingCartFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        //click on title of app go back to home fragment and set menu home active
        activityMainBinding.titleTxt.setOnClickListener {
            displayFragment(HomeFragment())
            activityMainBinding.bottomNavigationView.selectedItemId = R.id.mnuHome
        }

        //display home fragment when starting app
        displayFragment(homeFragment)

        val signInScreen = Intent(this, SignInActivity::class.java)
        val profileActivity = Intent(this, ProfileActivity::class.java)


        //action on bottom nav_bar when user click menu
        activityMainBinding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mnuHome -> displayFragment(homeFragment)
                R.id.mnuFav -> displayFragment(favoriteFragment)
                R.id.mnuSearch -> displayFragment(searchFragment)
                R.id.mnuCart -> displayFragment(shoppingCartFragment)
                else -> startActivity(profileActivity)
//                else -> startActivity(signInScreen)
//                else -> displayFragment(ProfileFragment())
            }
            true
        }
    }


    //function display Fragments
//    private fun displayFragment(fragment: Fragment) {
//
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        // Replace fragment in lytFragment
//        fragmentTransaction.replace(R.id.lytFragment, fragment)
//        // Commit transaction
//        fragmentTransaction.commit()
//    }
    // Function to display fragments without reloading
    private fun displayFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Hide all existing fragments
        for (existingFragment in fragmentManager.fragments) {
            fragmentTransaction.hide(existingFragment)
        }

        // Check if the fragment is already added
        if (!fragment.isAdded) {
            // If not added, add it to the fragment container
            fragmentTransaction.add(R.id.lytFragment, fragment)
        } else {
            // If already added, show it
            fragmentTransaction.show(fragment)
        }

        // Add the transaction to the back stack
        fragmentTransaction.addToBackStack(null)

        // Commit the transaction
        fragmentTransaction.commit()
    }

}