package com.i.common.attendance.ui.home.pjc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemWeekDaysBinding

class WeekDaysAdapter : RecyclerView.Adapter<WeekDaysAdapter.WeekDaysViewHolder>() {
    var weekDaysList = ArrayList<String>()

    inner class WeekDaysViewHolder(private val binding : RowItemWeekDaysBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data : String){
            binding.txtViewWeekDays.text = data
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WeekDaysViewHolder {
        return WeekDaysViewHolder(RowItemWeekDaysBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(
        holder: WeekDaysViewHolder,
        position: Int
    ) {
        holder.bind(weekDaysList[position])
    }

    override fun getItemCount(): Int {
        return weekDaysList.size
    }
}