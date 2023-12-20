package kh.edu.rupp.ite.furniturestore.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import kh.edu.rupp.ite.furniturestore.R

abstract class BaseActivity<T : ViewBinding>(
    private val bindingFunction: (LayoutInflater) -> T
) : AppCompatActivity() {

    private var _binding: T? = null
    protected val binding: T
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = bindingFunction(layoutInflater)
        setContentView(binding.root)

        bindUi()
        initFields()
        initActions()
        setupListeners()
        setupObservers()
    }

    abstract fun bindUi()
    abstract fun initFields()
    abstract fun initActions()
    abstract fun setupListeners()
    abstract fun setupObservers()

    fun showLoadingAnimation(viewContainerLoadingId: ShimmerFrameLayout) {
        viewContainerLoadingId.startShimmer()
    }

    fun hideLoadingAnimation(viewContainerLoadingId: ShimmerFrameLayout) {
        viewContainerLoadingId.stopShimmer()
        viewContainerLoadingId.visibility = View.GONE;
    }

    @SuppressLint("ResourceAsColor")
    fun showSnackBar(context: Context, view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)

        // If you want to customize the Snackbar, you can use snackbar.getView()
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))

        val params = snackbarView.layoutParams as ViewGroup.MarginLayoutParams
        params.marginStart = context.resources.getDimensionPixelSize(R.dimen.padding_10)
        params.marginEnd = context.resources.getDimensionPixelSize(R.dimen.padding_10)
        snackbarView.layoutParams = params

        // Show the Snackbar
        snackbar.show()
    }

    fun prevBack(backBtn: ImageView) {
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun prevBack(backBtn: LinearLayout) {
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
