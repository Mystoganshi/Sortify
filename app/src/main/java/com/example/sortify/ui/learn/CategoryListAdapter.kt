package com.example.sortify.ui.learn

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sortify.databinding.RowLearnItemBinding
import com.example.sortify.model.WasteInfo  // <-- if red, change to the correct WasteInfo import

class CategoryListAdapter(
    private val onClick: (WasteInfo) -> Unit
) : RecyclerView.Adapter<CategoryListAdapter.VH>() {

    private val items = mutableListOf<WasteInfo>()

    fun submit(newItems: List<WasteInfo>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = RowLearnItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding, onClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class VH(
        private val b: RowLearnItemBinding,
        private val onClick: (WasteInfo) -> Unit
    ) : RecyclerView.ViewHolder(b.root) {

        fun bind(item: WasteInfo) {
            b.tvName.text = item.name
            b.tvStatus.text = if (item.recyclable) "Recyclable" else "Non-Recyclable"

            b.root.setOnClickListener { onClick(item) }
        }
    }
}
