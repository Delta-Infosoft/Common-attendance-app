package com.i.common.attendance.ui.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.R
import com.i.common.attendance.databinding.RowItemCalenderBinding

class DateAdapter(
    private val dates: MutableList<DateModel>,
    private val onDateClick: (DateModel) -> Unit
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    inner class DateViewHolder(private val binding: RowItemCalenderBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: DateModel) = with(binding) {
            tvDay.text = data.day
            tvDate.text = data.date

            if (data.isSelected) {
                container.setBackgroundResource(R.drawable.bg_date_selected)
                tvDay.setTextColor(Color.WHITE)
                tvDate.setTextColor(Color.WHITE)
            } else {
                container.setBackgroundResource(R.drawable.bg_date_unselected)
                tvDay.setTextColor(Color.BLACK)
                tvDate.setTextColor(Color.BLACK)
            }

            root.setOnClickListener {
                dates.forEach { it.isSelected = false }
                data.isSelected = true
                notifyDataSetChanged()
                onDateClick(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        return DateViewHolder(
            RowItemCalenderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(dates[position])
    }

    override fun getItemCount() = dates.size

    fun updateData(newDates: List<DateModel>) {
        dates.clear()
        dates.addAll(newDates)
        notifyDataSetChanged()
    }
}

