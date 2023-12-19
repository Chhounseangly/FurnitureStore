package kh.edu.rupp.ite.furniturestore.view.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

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
        setupListeners()
        setupObservers()
        initActions()
    }

    abstract fun bindUi()
    abstract fun setupListeners()
    abstract fun setupObservers()
    abstract fun initActions()

    fun showLoading() {

    }

    fun hideLoading() {

    }

    fun showDialog() {

    }

    fun showLongToast(message: String) {

    }

    fun showShortToast(message: String) {

    }
}