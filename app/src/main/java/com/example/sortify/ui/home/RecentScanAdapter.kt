package com.example.sortify.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sortify.data.ScanWithItems
import com.example.sortify.databinding.RowRecentScanBinding
import java.util.concurrent.TimeUnit

class RecentScanAdapter(
    private val onDelete: (scanId: Long) -> Unit = {}
) : RecyclerView.Adapter<RecentScanAdapter.VH>() {

    private var items: List<ScanWithItems> = emptyList()

    fun submit(newItems: List<ScanWithItems>) {
        items = newItems
        notifyDataSetChanged()
    }

    class VH(val b: RowRecentScanBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = RowRecentScanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val scanWithItems = items[position]
        val ts = scanWithItems.scan.timestamp

        holder.b.tvTime.text = timeAgo(ts)

        val recyclableCount = scanWithItems.items
            .filter { it.recyclable }
            .sumOf { it.count }

        val nonCount = scanWithItems.items
            .filter { !it.recyclable }
            .sumOf { it.count }

        val names = scanWithItems.items
            .take(3)
            .joinToString(", ") { it.name } +
                if (scanWithItems.items.size > 3) "…" else ""

        holder.b.tvSummary.text = names
        holder.b.tvCounts.text = "Recyclable: $recyclableCount | Non-Recyclable: $nonCount"

        holder.b.btnDelete.setOnClickListener {
            onDelete(scanWithItems.scan.id)
        }
    }

    private fun timeAgo(ts: Long): String {
        val diff = System.currentTimeMillis() - ts
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            else -> "${days}d ago"
        }
    }
}
