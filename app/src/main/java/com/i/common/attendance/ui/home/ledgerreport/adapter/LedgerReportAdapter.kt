package com.i.common.attendance.ui.home.ledgerreport.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemLedgerReportDateBinding
import com.i.common.attendance.network.response.LedgerPdfData

class LedgerReportAdapter : ListAdapter<LedgerPdfData, LedgerReportAdapter.ViewHolder>(DiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowItemLedgerReportDateBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: RowItemLedgerReportDateBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: LedgerPdfData) = with(binding) {
            txtViewDate.text = item.TransactionDt ?: "-"
            txtViewClosingBalance.text = item.TransactionTypeName ?: "Closing Balance"
            txtViewAmount.text = "₹${item.DrAmt?.ifEmpty { "0.00" } ?: "0.00"} Dr"
            txtViewAmountDr.text = "₹${item.CrAmt?.ifEmpty { "0.00" } ?: "0.00"} Cr"
        }
    }

    class DiffCallBack : DiffUtil.ItemCallback<LedgerPdfData>() {
        override fun areItemsTheSame(oldItem: LedgerPdfData, newItem: LedgerPdfData): Boolean {
            return oldItem.TransactionId == newItem.TransactionId &&
                    oldItem.TransactionLnId == newItem.TransactionLnId
        }
        override fun areContentsTheSame(oldItem: LedgerPdfData, newItem: LedgerPdfData): Boolean {
            return oldItem == newItem
        }
    }
}