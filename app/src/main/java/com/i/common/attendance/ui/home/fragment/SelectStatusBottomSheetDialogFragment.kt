package com.i.common.attendance.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.i.common.attendance.databinding.BottomSheetFragmentSelectStatusBinding
import com.i.common.attendance.ui.home.adapter.SelectStatusAdapter
import com.i.common.attendance.ui.home.viewmodel.GetTextListState
import com.i.common.attendance.ui.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class SelectStatusBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFragmentSelectStatusBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private var dismissCallback: ((String) -> Unit)? = null
    fun setDismissCallback(callback: (String) -> Unit) {
        dismissCallback = callback
    }

    private lateinit var statusAdapter: SelectStatusAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetFragmentSelectStatusBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        statusAdapter = SelectStatusAdapter { selectedStatus ->
            dismissCallback?.invoke((selectedStatus.Text.toString()))
            dismissNow()
        }

        binding.recyclerViewStatus.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
            adapter = statusAdapter
        }
    }

    private fun observeLiveData(){
        homeViewModel.getTextListAPI()
        homeViewModel.textListState.observe(viewLifecycleOwner) { state ->
            when (state) {

                is GetTextListState.Loading -> {
                    // show loader
                    showLoader()
                }

                is GetTextListState.Success -> {
                    hideLoader()
                    statusAdapter.statusList.clear()
                    statusAdapter.statusList.addAll(state.list)
                    statusAdapter.notifyDataSetChanged()
                }

                is GetTextListState.Empty -> {
                    hideLoader()
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }

                is GetTextListState.Error -> {
                    hideLoader()
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }

                else -> Unit
            }
        }
    }

    private fun showLoader() {
        binding.progressBarStatus.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        binding.progressBarStatus.visibility = View.GONE
    }
}