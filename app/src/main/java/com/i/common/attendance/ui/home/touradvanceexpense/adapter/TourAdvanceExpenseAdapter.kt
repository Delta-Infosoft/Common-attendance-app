package com.i.common.attendance.ui.home.touradvanceexpense.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.BuildConfig
import com.i.common.attendance.databinding.RowItemTourAdvanceExpenseBinding
import com.i.common.attendance.network.response.TourAdvanceExpense
import com.i.common.attendance.utils.Constants

class TourAdvanceExpenseAdapter(private val onItemClick: ((TourAdvanceExpense) -> Unit)? = null) : ListAdapter<TourAdvanceExpense, TourAdvanceExpenseAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowItemTourAdvanceExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(private val binding: RowItemTourAdvanceExpenseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TourAdvanceExpense) = with(binding){
            when (BuildConfig.FLAVOR) {
                "flotech","singla","algo","mascot" -> {
                    tvEmpNameName.text = item.EmployeeName
                    txtViewDate.text = Constants.convertDateFormat(item.RequestDt ?: "", "M/d/yyyy hh:mm:ss a", "dd-MMM-yyyy")
                    txtAdvanceAmount.text = item.AdvanceAmount?.toDoubleOrNull()
                        ?.toInt()
                        ?.toString()
                        ?: "0"
                    txtRemark.text = item.Remarks

                    root.setOnClickListener {
                        onItemClick?.invoke(item)
                    }
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TourAdvanceExpense>() {

        override fun areItemsTheSame(
            oldItem: TourAdvanceExpense,
            newItem: TourAdvanceExpense
        ): Boolean {

            return oldItem.AdvanceExpenseId == newItem.AdvanceExpenseId
        }

        override fun areContentsTheSame(
            oldItem: TourAdvanceExpense,
            newItem: TourAdvanceExpense
        ): Boolean {

            return oldItem == newItem
        }
    }
}