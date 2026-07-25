package com.i.common.attendance.ui.home.ledgerreport.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemSelectStatusBinding
import com.i.common.attendance.network.response.GetDivision

class SelectDivisionAdapter(
    private val onItemClick: (GetDivision) -> Unit
) : ListAdapter<GetDivision, SelectDivisionAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSelectStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: GetDivision) = with(binding) {
            txtViewStatus.text = data.BranchName

            root.setOnClickListener {
                onItemClick(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RowItemSelectStatusBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<GetDivision>() {
        override fun areItemsTheSame(oldItem: GetDivision, newItem: GetDivision): Boolean {
            return oldItem.DivisionId == newItem.DivisionId
        }

        override fun areContentsTheSame(oldItem: GetDivision, newItem: GetDivision): Boolean {
            return oldItem == newItem
        }
    }
}