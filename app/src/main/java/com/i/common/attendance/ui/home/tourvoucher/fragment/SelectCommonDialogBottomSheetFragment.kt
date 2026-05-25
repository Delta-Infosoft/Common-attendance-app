package com.i.common.attendance.ui.home.tourvoucher.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.i.common.attendance.databinding.BottomSheetCommonDialogBinding
import com.i.common.attendance.utils.Constants.setSafeOnClickListener

class SelectCommonDialogBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetCommonDialogBinding
    private var dismissCallback: ((String) -> Unit)? = null

    companion object {
        private const val ARG_ERROR_TEXT = "ARG_ERROR_TEXT"
        private const val ARG_PAGE_TEXT = "ARG_PAGE_TEXT"

        fun newInstance(errorText: String, pageText: String): SelectCommonDialogBottomSheetFragment {
            val fragment = SelectCommonDialogBottomSheetFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_ERROR_TEXT, errorText)
                putString(ARG_PAGE_TEXT, pageText)
            }
            return fragment
        }
    }

    fun setDismissCallback(callback: (String) -> Unit) {
        dismissCallback = callback
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            setCanceledOnTouchOutside(false)
            setCancelable(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetCommonDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        moveOnClickListeners()
    }

    private fun setData() = with(binding) {
        txtViewErrorText.text =
            arguments?.getString(ARG_ERROR_TEXT).orEmpty()

        txtViewPageName.text =
            arguments?.getString(ARG_PAGE_TEXT).orEmpty()
    }

    private fun moveOnClickListeners() = with(binding) {
        btnYes.setSafeOnClickListener {
            dismissCallback?.invoke("Yes")
            dismiss()
        }

        btnNo.setSafeOnClickListener {
            dismissCallback?.invoke("No")
            dismiss()
        }
    }
}
