package kh.edu.rupp.ite.furniturestore.view.activity

import android.content.Intent
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivityMainBinding
import kh.edu.rupp.ite.furniturestore.displayFragment.DisplayFragmentActivity
import kh.edu.rupp.ite.furniturestore.utility.AppPreference
import kh.edu.rupp.ite.furniturestore.view.activity.auth.SignInActivity
import kh.edu.rupp.ite.furniturestore.view.fragments.FavoriteFragment
import kh.edu.rupp.ite.furniturestore.view.fragments.HomeFragment
import kh.edu.rupp.ite.furniturestore.view.fragments.SearchFragment
import kh.edu.rupp.ite.furniturestore.view.fragments.ShoppingCartFragment

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private lateinit var displayFragmentActivity: DisplayFragmentActivity
    private lateinit var signInScreen: Intent

    private var homeFragment = HomeFragment()
    private var searchFragment = SearchFragment()
    private var favoriteFragment = FavoriteFragment()
    private var shoppingCartFragment = ShoppingCartFragment()

    override fun bindUi() {
        // Initialize DisplayFragmentActivity to manage fragment transactions
        displayFragmentActivity = DisplayFragmentActivity(supportFragmentManager)
        // Display the home fragment initially
        displayFragmentActivity.displayFragment(homeFragment)
    }

    override fun initFields() {

    }

    override fun initActions() {
        if (intent.action == Intent.ACTION_VIEW) {
            val data = intent.data

            // Handle the case when the user signs in with Google
            if (data != null && data.scheme == getString(R.string.app_scheme)) {
                showSnackBar(this, binding.root, getString(R.string.sign_in_success))

                // Save the token to shared preferences
                val token = data.getQueryParameter("token")
                AppPreference.get(this).setToken(token ?: "")
            }
        }
    }

    override fun setupListeners() {
        // Click on the app title goes back to the home fragment and sets the home menu as active
        binding.titleTxt.setOnClickListener {
            displayFragmentActivity.displayFragment(homeFragment)
            binding.bottomNavigationView.selectedItemId = R.id.mnuHome
        }

        // Initialize the Intent for signing in
        signInScreen = Intent(this, SignInActivity::class.java)

        // Action on bottom nav_bar when the user clicks on a menu item
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mnuHome -> displayFragmentActivity.displayFragment(homeFragment)
                R.id.mnuFav -> {
                    if (isUserSignedIn()) {
                        displayFragmentActivity.displayFragment(favoriteFragment)
                    } else startActivity(signInScreen)
                }

                R.id.mnuSearch -> displayFragmentActivity.displayFragment(searchFragment)
                R.id.mnuCart -> {
                    if (isUserSignedIn()) {
                        displayFragmentActivity.displayFragment(shoppingCartFragment)
                    } else startActivity(signInScreen)
                }

                else -> {
                    if (isUserSignedIn()) {
                        val profileActivity = Intent(this, ProfileActivity::class.java)
                        startActivity(profileActivity)
                    } else startActivity(signInScreen)
                }
            }
            true
        }
    }

    override fun setupObservers() {

    }

    override fun onResume() {
        super.onResume()

        // Set the active menu item to current fragment
        when (displayFragmentActivity.getCurrentFragment()) {
            homeFragment -> binding.bottomNavigationView.selectedItemId = R.id.mnuHome
            favoriteFragment -> binding.bottomNavigationView.selectedItemId = R.id.mnuFav
            searchFragment -> binding.bottomNavigationView.selectedItemId = R.id.mnuSearch
            shoppingCartFragment -> binding.bottomNavigationView.selectedItemId = R.id.mnuCart
        }
    }

    // Check if the user is signed in
    private fun isUserSignedIn(): Boolean {
        return AppPreference.get(this).getToken() != null
    }
}
