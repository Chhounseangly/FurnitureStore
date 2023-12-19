package kh.edu.rupp.ite.furniturestore.view.fragments

import androidx.fragment.app.Fragment
import com.facebook.shimmer.ShimmerFrameLayout
import kh.edu.rupp.ite.furniturestore.view.activity.BaseActivity

open class BaseFragment: Fragment() {
    fun showLoadingAnimation(viewContainerLoadingId: ShimmerFrameLayout) {
        val baseActivity = activity as BaseActivity<*>
        baseActivity.showLoadingAnimation(viewContainerLoadingId)
    }

    fun hideLoadingAnimation(viewContainerLoadingId: ShimmerFrameLayout) {
        val baseActivity = activity as BaseActivity<*>
        baseActivity.hideLoadingAnimation(viewContainerLoadingId)
    }

    fun showLongToast(message: String) {
        val baseActivity = activity as BaseActivity<*>
        baseActivity.showLongToast(message)
    }
}