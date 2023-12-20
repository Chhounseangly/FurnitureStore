package kh.edu.rupp.ite.furniturestore.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import kh.edu.rupp.ite.furniturestore.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    private val splashTimeOut = 500

    override fun bindUi() {
        Handler().postDelayed(Runnable {
            val homeIntent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(homeIntent)
            finish()
        }, splashTimeOut.toLong())
    }

    override fun initFields() {

    }

    override fun initActions() {

    }

    override fun setupListeners() {

    }

    override fun setupObservers() {

    }
}
