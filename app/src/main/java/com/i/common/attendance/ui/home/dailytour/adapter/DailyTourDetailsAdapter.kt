package com.i.common.attendance.ui.home.dailytour.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.BuildConfig
import com.i.common.attendance.databinding.RowItemDailyTourBinding
import com.i.common.attendance.network.response.DailTourList
import com.i.common.attendance.utils.Constants

class DailyTourDetailsAdapter(private val onItemClick: ((DailTourList) -> Unit)? = null) : ListAdapter<DailTourList, DailyTourDetailsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowItemDailyTourBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(private val binding: RowItemDailyTourBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DailTourList) = with(binding){
            when (BuildConfig.FLAVOR) {
                "flotech","singla","algo" -> {
                    tvDealerName.text = item.DealerName
                    txtViewDate.text = Constants.convertDateFormat(item.Dt ?: "", "M/d/yyyy hh:mm:ss a", "dd-MMM-yyyy")
                    txtViewUserName.text = item.Name1
                    txtViewCompanyDealer.text = item.DealerCategory
                    txtViewMobileNumber.text = item.MobileNo1
                    tvFromLocation.text = item.FromPlace ?: ""
                    tvToLocation.text = item.ToPlace ?: ""

                    root.setOnClickListener {
                        onItemClick?.invoke(item)
                    }
                }
                "mascot"->{
                    tvDealerName.text = item.DealerName
                    txtViewDate.text = Constants.convertDateFormat(item.Dt ?: "", "M/d/yyyy hh:mm:ss a", "dd-MMM-yyyy")
                    txtViewUserName.text = item.Name1
                    txtViewCompanyDealer.text = item.DealerCategory
                    txtViewMobileNumber.text = item.MobileNo1
                    tvFromLocation.text = "${item.FromPlace ?: ""} - ${Constants.convertDateFormat(item.StartTime ?: "", "dd-MMM-yyyy hh:mm:ss a", "hh:mm a")}"
                    tvToLocation.text = "${item.ToPlace ?: ""} - ${Constants.convertDateFormat(item.EndTime ?: "", "dd-MMM-yyyy hh:mm:ss a", "hh:mm a")}"

                    root.setOnClickListener(null)
                }
                else -> {
                    tvDealerName.text = item.DealerName
                    txtViewDate.text = Constants.convertDateFormat(item.Date ?: "", "dd-MMM-yyyy hh:mm:ss a", "dd-MMM-yyyy")
                    txtViewUserName.text = item.Name
                    txtViewCompanyDealer.text = item.DealerCategory
                    txtViewMobileNumber.text = item.MobileNo
                    tvFromLocation.text = "${item.FromPlace ?: ""} - ${Constants.convertDateFormat(item.StartTime ?: "", "dd-MMM-yyyy hh:mm:ss a", "hh:mm a")}"
                    tvToLocation.text = "${item.ToPlace ?: ""} - ${Constants.convertDateFormat(item.EndTime ?: "", "dd-MMM-yyyy hh:mm:ss a", "hh:mm a")}"

                    root.setOnClickListener(null)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DailTourList>() {

        override fun areItemsTheSame(
            oldItem: DailTourList,
            newItem: DailTourList
        ): Boolean {

            return oldItem.Date == newItem.Date &&
                    oldItem.StartTime == newItem.StartTime
        }

        override fun areContentsTheSame(
            oldItem: DailTourList,
            newItem: DailTourList
        ): Boolean {

            return oldItem == newItem
        }
    }
}