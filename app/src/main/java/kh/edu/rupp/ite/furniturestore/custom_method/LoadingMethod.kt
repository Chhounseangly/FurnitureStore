package kh.edu.rupp.ite.furniturestore.custom_method

import android.view.View
import com.facebook.shimmer.ShimmerFrameLayout

class LoadingMethod {
    //hide loading
    fun hideLoadingAnimation(viewContainerLoadingId: ShimmerFrameLayout){
        viewContainerLoadingId.stopShimmer()
        viewContainerLoadingId.visibility = View.GONE;
    }

    //show loading
    fun showLoadingAnimation(viewContainerLoadingId: ShimmerFrameLayout){
        viewContainerLoadingId.startShimmer()
    }

}