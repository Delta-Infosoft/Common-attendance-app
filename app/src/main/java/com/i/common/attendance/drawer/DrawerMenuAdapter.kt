package com.i.common.attendance.drawer

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.BuildConfig
import com.i.common.attendance.R
import com.i.common.attendance.databinding.ItemDrawerMenuBinding
import com.i.common.attendance.ui.home.activity.DrawerMenuConfig

/**
 * Adapter for the navigation drawer menu.
 * Receives an ordered list — what comes in is exactly what renders.
 * No visibility toggling, no overlap issues.
 */
class DrawerMenuAdapter(
    private var items: List<DrawerMenuConfig.MenuItem>,
    private val onItemClick: (DrawerMenuConfig.MenuItem) -> Unit,
) : RecyclerView.Adapter<DrawerMenuAdapter.MenuViewHolder>() {

    // NEW: holds badge counts keyed by menu item (e.g. pending leave approvals)
    private val badgeCounts = mutableMapOf<DrawerMenuConfig.MenuItem, Int>()

    inner class MenuViewHolder(
        private val binding: ItemDrawerMenuBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DrawerMenuConfig.MenuItem) {
            binding.txtViewMenuLabel.setText(
                DrawerMenuConfig.labelFor(BuildConfig.FLAVOR, item)
                    ?: throw IllegalStateException("No label registered for $item")
            )
            // Change text color for ACTION_REQUIRED
            val colorRes = if (item == DrawerMenuConfig.MenuItem.ACTION_REQUIRED) {
                R.color.red_1
            } else {
                R.color.black // your default text color
            }

            binding.txtViewMenuLabel.setTextColor(ContextCompat.getColor(binding.root.context, colorRes))

            // Badge ONLY for LEAVE_APPROVAL — every other item explicitly hides it
            if (item == DrawerMenuConfig.MenuItem.LEAVE_APPROVAL) {
                val count = badgeCounts[item] ?: 0
                if (count > 0) {
                    binding.txtViewBadgeCount.visibility = android.view.View.VISIBLE
                    binding.txtViewBadgeCount.text = if (count > 99) "99+" else count.toString()
                } else {
                    binding.txtViewBadgeCount.visibility = android.view.View.GONE
                }
            } else {
                binding.txtViewBadgeCount.visibility = android.view.View.GONE
            }

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemDrawerMenuBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    /** Call this if you need to dynamically update the list at runtime. */
    fun submitList(newItems: List<DrawerMenuConfig.MenuItem>) {
        items = newItems
        notifyDataSetChanged()
    }
    // NEW: call this whenever you get a fresh count (e.g. from your API response)
    fun updateBadgeCount(item: DrawerMenuConfig.MenuItem, count: Int) {
        badgeCounts[item] = count
        val index = items.indexOf(item)
        //Log.d("LEAVE_BADGE", "6️⃣ Adapter.updateBadgeCount item=$item count=$count index=$index")
        if (index != -1) notifyItemChanged(index)
    }

}