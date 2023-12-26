package kh.edu.rupp.ite.furniturestore.view.activity

import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivityMainBinding
import kh.edu.rupp.ite.furniturestore.displayFragment.DisplayFragmentActivity
import kh.edu.rupp.ite.furniturestore.utility.AppPreference
import kh.edu.rupp.ite.furniturestore.view.activity.auth.SignInActivity
import kh.edu.rupp.ite.furniturestore.view.fragments.FavoriteFragment
import kh.edu.rupp.ite.furniturestore.view.fragments.HomeFragment
import kh.edu.rupp.ite.furniturestore.view.fragments.SearchFragment
import kh.edu.rupp.ite.furniturestore.view.fragments.ShoppingCartFragment
import kh.edu.rupp.ite.furniturestore.viewmodel.BadgesQuantityStoring


class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private lateinit var displayFragmentActivity: DisplayFragmentActivity
    private lateinit var signInScreen: Intent

    private var homeFragment = HomeFragment()
    private var searchFragment = SearchFragment()
    private var favoriteFragment = FavoriteFragment()
    private var shoppingCartFragment = ShoppingCartFragment()

    private val badgesQuantityStoring: BadgesQuantityStoring by viewModels()

    override fun initActions() {
        // Initialize DisplayFragmentActivity to manage fragment transactions
        displayFragmentActivity = DisplayFragmentActivity(supportFragmentManager)
        // Display the home fragment initially
        displayFragmentActivity.displayFragment(homeFragment)

        if (intent.action == Intent.ACTION_VIEW) {
            val data = intent.data

            // Handle the case when the user signs in with Google
            if (data != null && data.scheme == getString(R.string.app_scheme)) {

                // Check Status
                val status = data.getQueryParameter("status")
                if (status == "500") {
                    val error = data.getQueryParameter("error")
                    Log.e("MainActivity", "Error: $error")
                    Snackbar.make(
                        binding.root,
                        getString(R.string.sign_in_failed),
                        Snackbar.LENGTH_LONG
                    ).show()
                    return
                }
                Snackbar.make(
                    binding.root,
                    getString(R.string.sign_in_success),
                    Snackbar.LENGTH_LONG
                ).show()

                // Save the token to shared preferences
                val token = data.getQueryParameter("token")
                AppPreference.get(this).setToken(token ?: "")
            }
        }

        handleHistoryButton()
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
                    //clear badge of menu Favorite
                    badgesQuantityStoring.clearQtyFav()
                    clearBadge(R.id.mnuFav, binding)
                    if (isUserSignedIn()) {
                        displayFragmentActivity.displayFragment(favoriteFragment)
                    } else startActivity(signInScreen)
                }

                R.id.mnuSearch -> {
                    displayFragmentActivity.displayFragment(searchFragment)
                }

                R.id.mnuCart -> {
                    //clear badge of menu Cart
                    badgesQuantityStoring.clearQtyShoppingCart()
                    clearBadge(R.id.mnuCart, binding)
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


    private fun handleHistoryButton() {
        val historyBtn = binding.historyBtn
        historyBtn.setOnClickListener {
            val historyIntent = Intent(this, HistoryActivity::class.java)
            startActivity(historyIntent)
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

