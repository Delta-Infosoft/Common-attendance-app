package com.i.common.attendance.ui.authentication.activity

import android.os.Bundle
import com.i.common.attendance.base.BaseActivity
import com.i.common.attendance.databinding.ActivityAuthenticationBinding
import com.i.common.attendance.ui.authentication.fragment.LoginFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthenticationActivity : BaseActivity() {

    private lateinit var binding : ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadFragment(LoginFragment(), isAdd = true, isAddBackStack = false)
    }

}