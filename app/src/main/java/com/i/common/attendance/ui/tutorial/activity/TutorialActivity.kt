package com.i.common.attendance.ui.tutorial.activity

import android.content.Intent
import android.os.Bundle
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseActivity
import com.i.common.attendance.databinding.ActivityTutorialBinding
import com.i.common.attendance.ui.authentication.activity.AuthenticationActivity
import com.i.common.attendance.utils.EncryptedPrefHelper
import com.i.common.attendance.utils.PrefKeys
import com.i.common.attendance.ui.tutorial.adapter.TutorialPagerAdapter
import com.i.common.attendance.ui.tutorial.data.TutorialData
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject

@AndroidEntryPoint
class TutorialActivity : BaseActivity() {

    private lateinit var binding : ActivityTutorialBinding
    @Inject
    lateinit var encryptedPrefHelper: EncryptedPrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        manageOnclickListeners()
        setUpViewPager()
    }

    private fun setUpViewPager() = with(binding) {
         val pages = listOf(
            TutorialData(
                title = getString(R.string.tutorial_label_1),
                description =getString(R.string.tutorial_des_1),
                image = R.drawable.ic_tutorial
            ),
            TutorialData(
                title = getString(R.string.tutorial_label_2),
                description = getString(R.string.tutorial_des_2),
                image = R.drawable.ic_tutorial
            )
        )
        val adapter = TutorialPagerAdapter(this@TutorialActivity, pages)
        viewpagerTutorial.adapter = adapter
        dotsIndicator.attachTo(viewpagerTutorial)
    }

    private fun manageOnclickListeners() = with(binding){
        btnLogin.setOnClickListener {
            encryptedPrefHelper.putBoolean(PrefKeys.IS_TUTORIAL_COMPLETED, true)
            startActivity(Intent(this@TutorialActivity, AuthenticationActivity::class.java))
            finish()
        }
    }
}