package com.i.common.attendance.ui.home.dealerwisereport.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemDealerWiseReportBinding
import com.i.common.attendance.network.response.DealerWiseTarget

class DealerWiseTargetAdapter(
    private val onQtyChanged: (DealerWiseTarget, String) -> Unit
) : ListAdapter<DealerWiseTarget, DealerWiseTargetAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemDealerWiseReportBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var textWatcher: android.text.TextWatcher? = null

        fun bind(data: DealerWiseTarget) = with(binding) {

            txtViewType.text = data.Text
            txtViewAvgRate.text = data.AvgRate

            val avgRate = data.AvgRate?.toDoubleOrNull() ?: 0.0

            textWatcher?.let { txtViewQty.removeTextChangedListener(it) }

            val qtyText = if (data.qty == 0.0) "0" else {
                if (data.qty % 1.0 == 0.0) data.qty.toLong().toString() else data.qty.toString()
            }
            if (txtViewQty.text.toString() != qtyText) {
                txtViewQty.setText(qtyText)
            }

            val total = avgRate * data.qty
            txtViewTotal.text = String.format("%.2f", total)

            textWatcher = txtViewQty.doAfterTextChanged { editable ->

                val qty = editable?.toString()?.toDoubleOrNull() ?: 0.0
                data.qty = qty
                val calculatedTotal = avgRate * qty

                txtViewTotal.text = String.format("%.2f", calculatedTotal)

                onQtyChanged(data, editable?.toString().orEmpty())
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            RowItemDealerWiseReportBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<DealerWiseTarget>() {

        override fun areItemsTheSame(
            oldItem: DealerWiseTarget,
            newItem: DealerWiseTarget
        ): Boolean {
            return oldItem.TextListId == newItem.TextListId
        }

        override fun areContentsTheSame(
            oldItem: DealerWiseTarget,
            newItem: DealerWiseTarget
        ): Boolean {
            return oldItem == newItem
        }
    }
}
