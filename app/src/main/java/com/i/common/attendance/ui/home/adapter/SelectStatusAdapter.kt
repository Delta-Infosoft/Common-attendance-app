package com.i.common.attendance.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemSelectStatusBinding
import com.i.common.attendance.network.response.StatusList

class SelectStatusAdapter(private val onItemClick: (StatusList) -> Unit) : RecyclerView.Adapter<SelectStatusAdapter.ViewHolder>() {

    val statusList = ArrayList<StatusList>()
    inner class ViewHolder(val binding : RowItemSelectStatusBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(data : StatusList) = with(binding){
            binding.txtViewStatus.text = data.Text
            root.setOnClickListener {
                onItemClick(data)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(RowItemSelectStatusBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(statusList[position])
    }

    override fun getItemCount(): Int {
        return statusList.size
    }


}