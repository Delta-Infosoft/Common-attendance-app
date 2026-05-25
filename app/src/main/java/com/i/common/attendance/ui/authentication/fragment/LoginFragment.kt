package com.i.common.attendance.ui.authentication.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.InputFilter
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.i.common.attendance.BuildConfig
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentLoginBinding
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.Constants.getTrimmedText
import com.i.common.attendance.utils.Constants.hideKeyboard
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import com.i.common.attendance.utils.PrefKeys
import com.i.common.attendance.ui.authentication.viewmodel.AuthenticationViewModel
import com.i.common.attendance.utils.Constants.isValidIp
import com.i.delta.attendanceappv2.ui.authentication.viewmodel.ForgotPasswordState
import com.i.delta.attendanceappv2.ui.authentication.viewmodel.LoginState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthenticationViewModel by viewModels()
    @Inject lateinit var sharedPref: EncryptedPrefHelper
    /**
     * STEP 1: Permission launcher
     */
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                checkLocationEnabled()
            } else {
                showToast("All permissions are required to start tracking")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleDrawerByFlavor()
        manageLog()
        requestFirebaseToken()
        setupClickListeners()
        observeLogin()
        setupForgotPasswordText()

        // STEP 2: Start permission + location flow
        startPermissionFlow()

        observeForgotPassword()
    }

    private fun handleDrawerByFlavor() = with(binding){
        when (BuildConfig.FLAVOR) {
            "unnati" -> {
                editTextCompanyName.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            }
            "waterman"-> {
                editTextCompanyName.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            }
            "duke" -> {
                editTextCompanyName.visibility = View.GONE
                editTextMobileNo.apply {
                    hint = "Enter employee id"
                    filters = arrayOf(InputFilter.LengthFilter(4))
                }
                editTextCompanyName.setText("dukeapiiattandence.deltasoftware.in")
            }
            "flotech","singla","algo","mascot" -> {
                editTextCompanyName.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            }
        }
    }

    private fun manageLog() {
        Log.e("getDeviceName", Constants.getDeviceName())
        Log.e("getAndroidVersion", Constants.getAndroidVersion())
        Log.e("isGpsEnabled", Constants.isGpsEnabled(requireContext()).toString())
        Log.e("getBatteryLevel", Constants.getBatteryLevel(requireContext()).toString())
        Log.e("isNetworkAvailable", Constants.isNetworkAvailable(requireContext()).toString())
        Log.e("getAppVersion", Constants.getAppVersion(requireContext()))
    }

    /**
     * STEP 3: Permission flow entry point
     */
    private fun startPermissionFlow() {
        if (hasRequiredPermissions()) {
            checkLocationEnabled()
        } else {
            requestRequiredPermissions()
        }
    }

    /**
     * STEP 4: Permission check
     */
    private fun hasRequiredPermissions(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true

        return fineLocation && notification
    }

    /**
     * STEP 5: Ask permissions
     */
    private fun requestRequiredPermissions() {
        val permissions = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissions.isNotEmpty()) {
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }

    /**
     * STEP 6: Check if location is ON
     */
    private fun checkLocationEnabled() {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isEnabled) {
            showEnableLocationDialog()
        }
    }

    /**
     * STEP 7: Ask user to turn ON location
     */
    private fun showEnableLocationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Location Required")
            .setMessage("Please turn on location to capture real attendance location.")
            .setCancelable(false)
            .setPositiveButton("Turn On") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel") { _, _ ->
                showToast("Location is mandatory for attendance")
            }
            .show()
    }

    /**
     * UI Clicks
     */
    private fun setupClickListeners() = with(binding) {
        btnLogin.setSafeOnClickListener {
            if (hasRequiredPermissions()) {
                checkLocationEnabled()
                val isDuke = BuildConfig.FLAVOR == "duke"
                if(editTextCompanyName.getTrimmedText().isEmpty()){
                    showToast("Please enter ip")
                } else if(!Constants.isValidIp(editTextCompanyName.getTrimmedText())){
                    showToast("Please enter valid IP address")
                } else if(editTextMobileNo.getTrimmedText().isEmpty()){
                    showToast("Please enter mobile no")
                } else if(!isDuke && editTextMobileNo.getTrimmedText().length!=10){
                    showToast("Please enter valid mobile no")
                }else{
                    if (isValidIp(ip = editTextCompanyName.getTrimmedText())) {
                        sharedPref.saveBaseUrl(editTextCompanyName.getTrimmedText())
                    } else {
                        showToast("Invalid IP Address")
                        return@setSafeOnClickListener
                    }

                    hideKeyboard(it)
                    callLoginApi()
                }
            } else {
                startPermissionFlow()
            }
        }

        imgViewPasswordToggle.setSafeOnClickListener {
            togglePasswordVisibility(
                editText = editTextPassword,
                imageView = imgViewPasswordToggle,
                showIcon = R.drawable.ic_toggle_hide,
                hideIcon = R.drawable.ic_toggle_hide
            )
        }

    }

    private fun callLoginApi() = with(binding) {
        viewModel.login(
            userName = editTextMobileNo.getTrimmedText(),
            /*password = editTextPassword.getTrimmedText(),*/
            /*ip = editTextCompanyName.getTrimmedText()*/
        )
    }

    /**
     * Observe login state
     */
    private fun observeLogin() {
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                LoginState.Loading -> {
                    setLoginEnabled(false)
                    binding.constViewApprovalCode.visibility = View.GONE
                    showLoader()
                }

                is LoginState.Success -> {
                    binding.constViewApprovalCode.visibility = View.GONE
                    hideLoader()
                    navigateToHome()
                }

                is LoginState.ApprovalRequired -> {
                    // ✅ SHOW approval layout
                    hideLoader()
                    setLoginEnabled(true)

                    binding.constViewApprovalCode.visibility = View.VISIBLE
                    binding.editTextApprovalCode.setText(Constants.getDeviceId(requireContext()))
                    showToast(state.message)
                }

                is LoginState.Error -> {
                    binding.constViewApprovalCode.visibility = View.GONE
                    hideLoader()
                    setLoginEnabled(true)
                    showToast(state.message)
                }

                LoginState.Idle -> {
                    binding.constViewApprovalCode.visibility = View.GONE
                    hideLoader()
                    setLoginEnabled(true)
                }
            }
        }
    }

    private fun navigateToHome() {
        sharedPref.putBoolean(PrefKeys.IS_LOGIN, true)
        startActivity(Intent(requireContext(), HomeActivity::class.java))
        requireActivity().finish()
    }

    private fun observeForgotPassword() {
        viewModel.forgotState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ForgotPasswordState.Loading -> {
                    showLoader()
                }

                is ForgotPasswordState.Message -> {
                    hideLoader()
                    showToast(state.message)
                    hideKeyboard(requireView())
                }

                is ForgotPasswordState.Success -> {
                    hideLoader()
                    showForgotPasswordDialog(state.password)
                }

                is ForgotPasswordState.Error -> {
                    hideLoader()
                    showToast(state.message)
                }

                else -> Unit
            }
        }
    }

    /**
     * Firebase token
     */
    private fun requestFirebaseToken() {
        try {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                if (token != null) {
                    sharedPref.saveFCMToken(token)
                }
                Log.d("FCM", token)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun togglePasswordVisibility(editText: AppCompatEditText, imageView: AppCompatImageView, showIcon: Int, hideIcon: Int) {
        val isPasswordVisible = editText.transformationMethod !is PasswordTransformationMethod

        if (isPasswordVisible) {
            // Hide password
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            imageView.setImageResource(hideIcon)
        } else {
            // Show password
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            imageView.setImageResource(showIcon)
        }

        // Move cursor to end
        editText.setSelection(editText.text.toString().length)
    }

    private fun setupForgotPasswordText() {
        try {
            val fullText = getString(R.string.label_forget_password)
            val clickableText = "Help"

            val spannable = SpannableString(fullText)

            val startIndex = fullText.indexOf(clickableText)
            val endIndex = startIndex + clickableText.length

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val mobileNo = binding.editTextMobileNo.getTrimmedText()

                    when {
                        mobileNo.isEmpty() -> {
                            showToast("Please enter mobile number")
                        }
                        mobileNo.length != 10 -> {
                            showToast("Please enter valid mobile number")
                        }
                        else -> {
                            hideKeyboard(widget)
                            viewModel.forgotPassword(mobileNo)
                        }
                    }
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(
                        binding.root.context,
                        R.color.primary
                    )
                    ds.isUnderlineText = false
                }
            }

            spannable.setSpan(
                clickableSpan,
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            binding.tctViewForgotPassword.text = spannable
            binding.tctViewForgotPassword.movementMethod = LinkMovementMethod.getInstance()
            binding.tctViewForgotPassword.highlightColor = Color.TRANSPARENT
        }catch (e: Exception){

        }
    }

    private fun showLoader() {
        requireActivity().window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        requireActivity().window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        binding.progressBar.visibility = View.GONE
    }

    private fun setLoginEnabled(enabled: Boolean) {
        binding.btnLogin.isEnabled = enabled
        binding.btnLogin.alpha = if (enabled) 1f else 0.6f
    }

    private fun showForgotPasswordDialog(password: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Forgot Password Confirmation")
            .setMessage("Your Password is : $password")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                binding.editTextPassword.setText(password)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}