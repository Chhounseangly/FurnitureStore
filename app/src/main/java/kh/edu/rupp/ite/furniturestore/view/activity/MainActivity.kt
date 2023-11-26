package kh.edu.rupp.ite.furniturestore.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.core.AppCore
import kh.edu.rupp.ite.furniturestore.databinding.ActivityMainBinding
import kh.edu.rupp.ite.furniturestore.displayFragment.DisplayFragmentActivity
import kh.edu.rupp.ite.furniturestore.utility.AppPreference
import kh.edu.rupp.ite.furniturestore.view.activity.auth.SignInActivity
import kh.edu.rupp.ite.furniturestore.view.fragments.ShoppingCartFragment
import kh.edu.rupp.ite.furniturestore.view.fragments.SearchFragment
import kh.edu.rupp.ite.furniturestore.view.fragments.FavoriteFragment
import kh.edu.rupp.ite.furniturestore.view.fragments.HomeFragment
import kh.edu.rupp.ite.furniturestore.viewmodel.ShoppingCartViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var displayFragmentActivity: DisplayFragmentActivity

    private lateinit var activityMainBinding: ActivityMainBinding

    private var shoppingCartViewModel = ShoppingCartViewModel()

    private var homeFragment = HomeFragment()
    private var searchFragment = SearchFragment()
    private var favoriteFragment = FavoriteFragment()
    private var shoppingCartFragment = ShoppingCartFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        displayFragmentActivity = DisplayFragmentActivity(supportFragmentManager)

        //click on title of app go back to home fragment and set menu home active
        activityMainBinding.titleTxt.setOnClickListener {
            displayFragmentActivity.displayFragment(homeFragment)
            activityMainBinding.bottomNavigationView.selectedItemId = R.id.mnuHome
        }

        // load products cart data
        shoppingCartViewModel.loadProductsCartData()

        //display home fragment when starting app
        displayFragmentActivity.displayFragment(homeFragment)

        val signInScreen = Intent(this, SignInActivity::class.java)
        val token = AppPreference.get(this).getToken();

        //action on bottom nav_bar when user click menu
        activityMainBinding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mnuHome -> displayFragmentActivity.displayFragment(homeFragment)
                R.id.mnuFav -> displayFragmentActivity.displayFragment(favoriteFragment)
                R.id.mnuSearch -> displayFragmentActivity.displayFragment(searchFragment)
                R.id.mnuCart -> displayFragmentActivity.displayFragment(shoppingCartFragment)
                else -> {
                    if (token != null){
                        val profileActivity = Intent(this, ProfileActivity::class.java)
                        startActivity(profileActivity)
                    }else startActivity(signInScreen)
                }
            }
            true
        }
    }
}