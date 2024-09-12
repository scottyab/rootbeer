package com.scottyab.rootbeer.sample.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scottyab.rootbeer.sample.RootItemResult
import com.scottyab.rootbeer.sample.databinding.ItemRootCheckBinding

class RootItemAdapter : RecyclerView.Adapter<RootItemAdapter.RootItemVH>() {
    private val items: MutableList<RootItemResult> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RootItemVH {
        val inflater = LayoutInflater.from(parent.context)
        return RootItemVH(
            ItemRootCheckBinding.inflate(
                inflater,
                parent,
                false,
            ),
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(
        holder: RootItemVH,
        position: Int,
    ) = holder.bind(items[position])

    fun add(rootItemResult: RootItemResult) {
        items.add(rootItemResult)
        notifyItemInserted(items.size - 1)
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    class RootItemVH(
        private val itemBinding: ItemRootCheckBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: RootItemResult) {
            itemBinding.rootItemText.text = item.text
            itemBinding.rootItemResultIcon.update(isRooted = item.result)
        }
    }
}
