package kh.edu.rupp.ite.furniturestore.view.fragments

import androidx.fragment.app.Fragment
import kh.edu.rupp.ite.furniturestore.view.activity.BaseActivity

open class BaseFragment: Fragment() {
    fun showLoading() {
        val baseActivity = activity as BaseActivity<*>
        baseActivity.showLoading()
    }

    fun hideLoading() {
        val baseActivity = activity as BaseActivity<*>
        baseActivity.hideLoading()
    }

    fun showLongToast(message: String) {
        val baseActivity = activity as BaseActivity<*>
        baseActivity.showLongToast(message)
    }
}