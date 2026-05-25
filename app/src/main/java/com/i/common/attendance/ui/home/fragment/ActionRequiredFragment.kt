package com.i.common.attendance.ui.home.fragment

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.ActivityAutoStartRequiredBinding
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.utils.Constants.setSafeOnClickListener

class ActionRequiredFragment : BaseFragment() {

    private lateinit var binding : ActivityAutoStartRequiredBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityAutoStartRequiredBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val instructionText = getString(
            R.string.auto_start_instruction,
            getAppName(),
            getAppName()
        )

        binding.txtInstructions.text = HtmlCompat.fromHtml(instructionText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        manageToolBar()
        moveOnClickListeners()
    }

    private fun getAppName(): String {
        return try {
            requireContext()
                .packageManager
                .getApplicationLabel(requireContext().applicationInfo)
                .toString()
        } catch (e: Exception) {
            getString(R.string.app_name)
        }
    }


    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toolbar_title_action_required))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }

    private fun moveOnClickListeners() = with(binding){
        btnOpenSettings.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${requireContext().packageName}")
            }
            startActivity(intent)
        }
        btnEnableAutoStart.setSafeOnClickListener {
            showAutoStartDialogIfNeeded()
        }
    }

    private fun showAutoStartDialogIfNeeded() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_title_enable_auto_start))
            .setMessage(
                "For reliable attendance tracking, Please allow this app to auto-start in background.\n\n" +
                        "Steps:\n" +
                        "Settings → Apps → Auto-start/Background autostart → Delta iAttendance → Allow"
            )
            .setPositiveButton("Enable") { _, _ ->
                openAutoStartSettings()
            }
            .setNegativeButton("Later") { _, _ ->
            }
            .setCancelable(false)
            .show()
    }

    private fun openAutoStartSettings() {
        try {
            when (Build.MANUFACTURER.lowercase()) {

                "vivo" -> {
                    startActivity(
                        Intent().apply {
                            component = ComponentName(
                                "com.vivo.permissionmanager",
                                "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                            )
                        }
                    )
                }

                "xiaomi", "redmi", "poco" -> {
                    startActivity(
                        Intent().apply {
                            component = ComponentName(
                                "com.miui.securitycenter",
                                "com.miui.permcenter.autostart.AutoStartManagementActivity"
                            )
                        }
                    )
                }

                "oppo" -> {
                    startActivity(
                        Intent().apply {
                            component = ComponentName(
                                "com.coloros.safecenter",
                                "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                            )
                        }
                    )
                }

                "realme" -> {
                    startActivity(
                        Intent().apply {
                            component = ComponentName(
                                "com.realme.securitycenter",
                                "com.realme.securitycenter.permission.startup.StartupAppListActivity"
                            )
                        }
                    )
                }

                else -> {
                    startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.parse("package:${requireContext().packageName}")
                        }
                    )
                }
            }
        } catch (e: Exception) {
            startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.parse("package:${requireContext().packageName}"))
            )
        }
    }
}