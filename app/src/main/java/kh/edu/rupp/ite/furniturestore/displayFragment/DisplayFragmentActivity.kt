package kh.edu.rupp.ite.furniturestore.displayFragment

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import kh.edu.rupp.ite.furniturestore.R

class DisplayFragmentActivity(private val fragmentManager: FragmentManager): AppCompatActivity() {

    // Maintain a reference to the currently visible fragment
    private var currentFragment: Fragment? = null

    // Function to display fragments without reloading
    fun displayFragment(fragment: Fragment) {
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Hide all existing fragments
        for (existingFragment in fragmentManager.fragments) {
            fragmentTransaction.hide(existingFragment)
            fragmentTransaction.setMaxLifecycle(existingFragment, Lifecycle.State.STARTED)
        }

        // Check if the fragment is already added
        if (!fragment.isAdded) {
            fragmentTransaction.add(R.id.lytFragment, fragment)
        } else {
            fragmentTransaction.setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
            fragmentTransaction.show(fragment)
        }

        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        currentFragment = fragment
    }

    // Function to get the current fragment
    fun getCurrentFragment(): Fragment? {
        return currentFragment
    }
}