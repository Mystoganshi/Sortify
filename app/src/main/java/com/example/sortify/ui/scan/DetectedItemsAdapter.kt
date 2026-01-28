package com.example.sortify.ui.scan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sortify.databinding.RowDetectedItemBinding
import com.example.sortify.viewmodel.LiveDetection

class DetectedItemsAdapter(
    private val onClick: (classId: Int) -> Unit
) : RecyclerView.Adapter<DetectedItemsAdapter.VH>() {

    private var items: List<LiveDetection> = emptyList()

    fun submit(newItems: List<LiveDetection>) {
        // Show unique items by classId
        items = newItems.groupBy { it.classId }.map { it.value.first() }
        notifyDataSetChanged()
    }

    class VH(val b: RowDetectedItemBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = RowDetectedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val it = items[position]
        holder.b.tvName.text = it.name
        holder.b.tvStatus.text = if (it.recyclable) "Recyclable" else "Non-Recyclable"
        holder.b.root.setOnClickListener { _ -> onClick(it.classId) }
    }
}
