package kh.edu.rupp.ite.furniturestore.view.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.facebook.shimmer.ShimmerFrameLayout

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

    fun showDialog() {

    }

    fun showLongToast(message: String) {

    }

    fun showShortToast(message: String) {

    }
}
