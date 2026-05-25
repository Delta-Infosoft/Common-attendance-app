package com.i.common.attendance.ui.home.pjc.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemSelectStatusBinding
import com.i.common.attendance.network.response.LoadDropDownList

class SelectDropDownValueForPjcAdapter(
    private val onItemClick: (LoadDropDownList) -> Unit
) : ListAdapter<LoadDropDownList, SelectDropDownValueForPjcAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSelectStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: LoadDropDownList) = with(binding) {
            txtViewStatus.text = data.Name

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
        Log.d("ADAPTER_TEST", "Binding: ${getItem(position).Name}")
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<LoadDropDownList>() {
        override fun areItemsTheSame(oldItem: LoadDropDownList, newItem: LoadDropDownList): Boolean {
            return oldItem.Id == newItem.Id
        }

        override fun areContentsTheSame(oldItem: LoadDropDownList, newItem: LoadDropDownList): Boolean {
            return oldItem == newItem
        }
    }
}