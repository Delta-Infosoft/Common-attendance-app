package com.i.common.attendance.ui.home.orderbook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemOrderBookListFlotechBinding
import com.i.common.attendance.network.response.OrderItem

class OrderListAdapter(private val onItemClick: ((OrderItem) -> Unit)? = null) : ListAdapter<OrderItem, OrderListAdapter.OrderViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(RowItemOrderBookListFlotechBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    inner class OrderViewHolder(private val binding: RowItemOrderBookListFlotechBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OrderItem) = with(binding) {
            txtViewCustomerName.text = item.vendorLgrName.orEmpty()
            txtViewQty.text = item.no.orEmpty()
            txtViewDeliveryDate.text = item.dt.orEmpty()
            tvTotal.text = "Total : ${item.grandTotalAmt ?: "0"} ₹"
            tvStatus.text = item.print ?: "N/A"

            // Clicks
            root.setOnClickListener {
                onItemClick?.invoke(item)
            }

            tvReport.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<OrderItem>() {

        override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem.soId == newItem.soId
        }

        override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem == newItem
        }
    }
}