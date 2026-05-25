package com.i.common.attendance.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.i.common.attendance.R
import com.i.common.attendance.utils.EncryptedPrefHelper
import com.i.common.attendance.utils.PrefKeys
import com.i.common.attendance.ui.authentication.activity.AuthenticationActivity
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.tutorial.activity.TutorialActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    @Inject lateinit var encryptedPrefHelper: EncryptedPrefHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        navigationToTutorialsScreen()
    }

    fun navigationToTutorialsScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            val isTutorialCompleted = encryptedPrefHelper.getBoolean(PrefKeys.IS_TUTORIAL_COMPLETED, false)
            val isLogin = encryptedPrefHelper.getBoolean(PrefKeys.IS_LOGIN, false)
            if (isTutorialCompleted) {
                if (isLogin) {
                    val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this@SplashActivity, AuthenticationActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                val intent = Intent(this@SplashActivity, TutorialActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 2000)
    }
}