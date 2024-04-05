package cloud.mariapps.chatapp.base

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T, VB : ViewBinding>(
    private val bindingInflater: (inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean) -> VB,
    diffCallback: DiffUtil.ItemCallback<T>,
) : ListAdapter<T, BaseAdapter<T, VB>.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: VB = bindingInflater.invoke(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = ViewHolder(binding)
        viewHolder.setClickListener()
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: T) {
            bindData(binding, item)
        }

        fun setClickListener() {
            clickListener(binding, holderCallback = { adapterPosition })
            binding.root.setOnClickListener {
                Log.i("Prudhvi", "setClickListener: $adapterPosition")
            }
        }
    }

    abstract fun bindData(binding: VB, item: T)

    abstract fun clickListener(binding: VB, holderCallback: () -> Int)

    val (() -> Int).position: Int
        get() {
            return this.invoke()
        }

}