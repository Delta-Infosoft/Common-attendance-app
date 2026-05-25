package com.i.common.attendance.ui.tutorial.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentTutorialBinding
import com.i.common.attendance.ui.tutorial.data.TutorialData

class TutorialFragment : BaseFragment() {

    private lateinit var binding: FragmentTutorialBinding

    companion object {
        private const val ARG_DATA = "arg_data"

        fun newInstance(data: TutorialData): TutorialFragment {
            return TutorialFragment().apply {
                arguments = bundleOf(ARG_DATA to data)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTutorialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = arguments?.getParcelable<TutorialData>(ARG_DATA) ?: return

        setData(data)
    }

    private fun setData(data: TutorialData) = with(binding){
        txtViewLabel.text = data.title
        txtViewDes.text = data.description
        imgViewTutorial.setImageResource(data.image)
    }
}
