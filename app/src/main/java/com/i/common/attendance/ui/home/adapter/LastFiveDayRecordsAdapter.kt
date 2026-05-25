package com.i.common.attendance.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItem5DayRecordsHomeBinding
import com.i.common.attendance.network.response.Records
import com.i.common.attendance.utils.EncryptedPrefHelper


class LastFiveDayRecordsAdapter(private val sharedPrefHelper: EncryptedPrefHelper,private val isFromHome: Boolean = true) : ListAdapter<Records, LastFiveDayRecordsAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: RowItem5DayRecordsHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Records) = with(binding) {
            val user = sharedPrefHelper.getUser()
            txtViewName.text = user?.UsersName?.takeIf { it.isNotBlank() } ?: "N/A"
            txtViewInTime.text = item.InTime?.takeIf { it.isNotBlank() } ?: "--"
            txtViewOutTime.text = item.OutTime?.takeIf { it.isNotBlank() } ?: "--"
            txtViewStatus.text = item.Status?.takeIf { it.isNotBlank() } ?: "N/A"
            if (!isFromHome) {
                val black = ContextCompat.getColor(root.context, android.R.color.black)
                txtViewName.setTextColor(black)
                txtViewInTime.setTextColor(black)
                txtViewOutTime.setTextColor(black)
                txtViewStatus.setTextColor(black)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowItem5DayRecordsHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Records>() {
            override fun areItemsTheSame(oldItem: Records, newItem: Records): Boolean {
                return oldItem.AutoId == newItem.AutoId
            }

            override fun areContentsTheSame(oldItem: Records, newItem: Records): Boolean {
                return oldItem == newItem
            }
        }
    }
}