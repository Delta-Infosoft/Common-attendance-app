package com.i.common.attendance.ui.home.myportfolio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemPortfolioListBinding
import com.i.common.attendance.network.response.ViewPortFolioModel

class PortfolioListAdapter(private val onCallClick: (String) -> Unit,
                           private val onMapClick: (lat: String, long: String) -> Unit,
                           private val onEditClick: (ViewPortFolioModel) -> Unit ) : ListAdapter<ViewPortFolioModel, PortfolioListAdapter.DealerViewHolder>(DiffCallback()) {
    inner class DealerViewHolder(private val binding: RowItemPortfolioListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ViewPortFolioModel) = with(binding) {
            tvCname.text = item.CompanyName?: "N/A"
            tvCity.text = item.City?: "N/A"
            tvPersonName.text = item.ContactPersonName?: "N/A"
            phoneNumber.text = item.ContactPersonMobileNo?: "N/A"

            phoneNumber.setOnClickListener {
                val number = item.ContactPersonMobileNo ?: return@setOnClickListener
                onCallClick.invoke(number)
            }

            imgLocation.setOnClickListener {
                val lat = item.Lat ?: ""
                val long = item.Long ?: ""
                onMapClick.invoke(lat, long)
            }

            imgEdit.setOnClickListener {
                onEditClick.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealerViewHolder {
        val binding = RowItemPortfolioListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DealerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DealerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // ---------------- DIFF UTIL ----------------

    class DiffCallback : DiffUtil.ItemCallback<ViewPortFolioModel>() {

        override fun areItemsTheSame(oldItem: ViewPortFolioModel, newItem: ViewPortFolioModel): Boolean {
            return oldItem.ContactPersonEmailId == newItem.ContactPersonEmailId
        }

        override fun areContentsTheSame(oldItem: ViewPortFolioModel, newItem: ViewPortFolioModel): Boolean {
            return oldItem == newItem
        }
    }
}
