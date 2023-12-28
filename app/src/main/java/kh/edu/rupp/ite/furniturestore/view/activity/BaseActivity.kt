package kh.edu.rupp.ite.furniturestore.view.activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.viewbinding.ViewBinding
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.badge.BadgeDrawable
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivityMainBinding
import kh.edu.rupp.ite.furniturestore.utility.AppPreference
import java.util.Locale

abstract class BaseActivity<T : ViewBinding>(
    private val bindingFunction: (LayoutInflater) -> T
) : AppCompatActivity() {

    private var _binding: T? = null
    val binding: T
        get() = _binding!!

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(updateBaseContextLocale(newBase))
    }

    private fun updateBaseContextLocale(context: Context): Context {
        val language = AppPreference.get(context).getLanguage() ?: "en"
        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)

        return context.createConfigurationContext(configuration)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = bindingFunction(layoutInflater)
        setContentView(binding.root)

        initActions()
        setupListeners()
        setupObservers()
    }

    abstract fun initActions()
    abstract fun setupListeners()
    abstract fun setupObservers()


    fun showCircleLoading(lytLoading: View, loading: ProgressBar) {
        lytLoading.visibility = View.VISIBLE
        loading.visibility = View.VISIBLE
    }

    fun hideCircleLoading(lytLoading: View, loading: ProgressBar) {
        lytLoading.visibility = View.GONE
        loading.visibility = View.GONE
    }


    //show badge on bottom navigation
    fun setupBadge(itemId: Int, value: Int, binding: ActivityMainBinding) {
        val badge: BadgeDrawable = binding.bottomNavigationView.getOrCreateBadge(itemId)
        badge.isVisible = true
        badge.number = value
    }

    //Clear Badge that show on button Navigation
    fun clearBadge(itemId: Int, binding: ActivityMainBinding) {
        val badge: BadgeDrawable = binding.bottomNavigationView.getOrCreateBadge(itemId)
        badge.isVisible = false
        badge.clearNumber()
    }

    fun loadingImg(context: Context): CircularProgressDrawable {
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        // Set the color scheme for the progress spinner (customize the colors)
        circularProgressDrawable.setColorSchemeColors(
            ContextCompat.getColor(context, R.color.red),
            ContextCompat.getColor(context, R.color.black),
            ContextCompat.getColor(context, R.color.blue)
        )
        // Set the background color to be transparent
        circularProgressDrawable.backgroundColor = Color.TRANSPARENT
        circularProgressDrawable.start()
        return circularProgressDrawable
    }

    fun showLoadingAnimation(viewContainerLoadingId: ShimmerFrameLayout) {
        viewContainerLoadingId.visibility = View.VISIBLE
        viewContainerLoadingId.startShimmer()
    }

    fun hideLoadingAnimation(viewContainerLoadingId: ShimmerFrameLayout) {
        viewContainerLoadingId.stopShimmer()
        viewContainerLoadingId.visibility = View.GONE;
    }

    fun prevBack(backBtn: ImageView) {
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun prevBack(backBtn: View) {
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun navigationBetweenEditTexts(
        editText: EditText,
        nextEditText: EditText?,
        onAction: (() -> Unit)? = null
    ) {
        editText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                nextEditText?.requestFocus()
                true
            } else if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                onAction?.invoke()
                true
            } else {
                false
            }
        }
    }
}
