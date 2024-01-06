package kh.edu.rupp.ite.furniturestore.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.edit
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

        //display custom appbar for all fragment
        showCustomActionBar(this, R.layout.fragment_action_bar)

        initActions()
        setupListeners()
        setupObservers()
    }

    abstract fun initActions()
    abstract fun setupListeners()
    abstract fun setupObservers()
    private fun isUserSignedIn(): Boolean {
        return AppPreference.get(this).getToken() != null
    }

    // Function to change the language
    private fun changeLanguage(languageCode: String) {
        AppPreference.get(this).setLanguage(languageCode)

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }


    @SuppressLint("CommitPrefEdits")
    fun showCustomActionBar(context: Context, layoutRes: Int): View {
        val customView = LayoutInflater.from(context).inflate(layoutRes, null)

        customView?.let { it ->
            supportActionBar?.apply {
                // Ensure supportActionBar is not null
                displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
                setDisplayShowCustomEnabled(true)
                supportActionBar!!.customView = it
            }
            val historyBtn = it.findViewById<ImageButton>(R.id.history_btn)
            val setting = it.findViewById<Toolbar>(R.id.setting)

            if (historyBtn != null || setting != null) {
                if (!isUserSignedIn()) {
                    historyBtn.visibility = View.GONE
                    setting.visibility = View.VISIBLE
                    val language = resources.configuration.locale.language
                    val themes = getSharedPreferences("Mode", Context.MODE_PRIVATE)
                    setting.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.language -> {
                                if (language == "en") {
                                    changeLanguage("km")
                                } else {
                                    changeLanguage("en")
                                }
                                recreate()
                            }

                            R.id.themes_mode -> {
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
                        true
                    }


                } else {
                    val historyIntent = Intent(context, HistoryActivity::class.java)
                    historyBtn.setOnClickListener {
                        context.startActivity(historyIntent)
                    }
                }
            }
        }

        return supportActionBar!!.customView

    }

    fun hideCustomActionBar(context: Context) {
        supportActionBar?.setDisplayShowCustomEnabled(false)
        supportActionBar?.customView = null
    }

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
