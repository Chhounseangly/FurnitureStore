package kh.edu.rupp.ite.furniturestore.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.viewbinding.ViewBinding
import com.facebook.shimmer.ShimmerFrameLayout
import kh.edu.rupp.ite.furniturestore.databinding.ActivityMainBinding
import kh.edu.rupp.ite.furniturestore.view.activity.BaseActivity

abstract class BaseFragment<T : ViewBinding>(
    private val bindingFunction: (LayoutInflater, ViewGroup?, Boolean) -> T
) : Fragment() {
    private var _binding: T? = null
    protected val binding: T
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = bindingFunction(inflater, container, false)

        bindUi()
        initFields()
        initActions()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObservers()
    }

    abstract fun bindUi()
    abstract fun initFields()
    abstract fun initActions()
    abstract fun setupListeners()
    abstract fun setupObservers()

    fun showCustomActionBar(context: Context,  layoutRes: Int): View{
        val baseActivity = activity as BaseActivity<*>
        return baseActivity.showCustomActionBar(context, layoutRes)
    }

    fun hideCustomActionBar(context: Context){
        val baseActivity = activity as BaseActivity<*>
        baseActivity.hideCustomActionBar(context)
    }

    //show badge on bottom navigation
    fun setupBadge(itemId: Int, value: Int, binding: ActivityMainBinding) {
        val baseActivity = activity as BaseActivity<*>
        baseActivity.setupBadge(itemId, value, binding)
    }

    //Clear Badge that show on button Navigation
    fun clearBadge(itemId: Int, value: Int, binding: ActivityMainBinding) {
        val baseActivity = activity as BaseActivity<*>
        baseActivity.clearBadge(itemId, binding)
    }

    fun loadingImg(context: Context): CircularProgressDrawable {
        val baseActivity = activity as BaseActivity<*>
        return baseActivity.loadingImg(context)
    }

    fun showLoadingAnimation(viewContainerLoadingId: ShimmerFrameLayout) {
        val baseActivity = activity as BaseActivity<*>
        baseActivity.showLoadingAnimation(viewContainerLoadingId)
    }

    fun hideLoadingAnimation(viewContainerLoadingId: ShimmerFrameLayout) {
        val baseActivity = activity as BaseActivity<*>
        baseActivity.hideLoadingAnimation(viewContainerLoadingId)
    }


}