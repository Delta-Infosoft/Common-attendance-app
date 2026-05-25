package com.i.common.attendance.ui.home.carairapproval.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.BuildConfig
import com.i.common.attendance.databinding.RowItemSelectStatusBinding
import com.i.common.attendance.network.response.GetCities

class SelectCityAdapter(
    private val onItemClick: (GetCities) -> Unit
) : ListAdapter<GetCities, SelectCityAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSelectStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: GetCities) = with(binding) {
            txtViewStatus.text =
                if (BuildConfig.FLAVOR == "unnati") {
                    data.city
                } else {
                    data.name
                }

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

    class DiffCallback : DiffUtil.ItemCallback<GetCities>() {

        override fun areItemsTheSame(oldItem: GetCities, newItem: GetCities): Boolean {

            return if (BuildConfig.FLAVOR == "unnati") {
                oldItem.cityId == newItem.cityId
            } else {
                oldItem.tadaCityId == newItem.tadaCityId
            }
        }

        override fun areContentsTheSame(oldItem: GetCities, newItem: GetCities): Boolean {

            return if (BuildConfig.FLAVOR == "unnati") {
                oldItem.city == newItem.city
            } else {
                oldItem == newItem
            }
        }
    }
}