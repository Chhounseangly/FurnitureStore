package kh.edu.rupp.ite.furniturestore.view.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
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

        initActions()
        setupListeners()
        setupObservers()
    }

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

    fun navigationBetweenEditTexts(editText: EditText, nextEditText: EditText?, onAction: (() -> Unit)? = null) {
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
