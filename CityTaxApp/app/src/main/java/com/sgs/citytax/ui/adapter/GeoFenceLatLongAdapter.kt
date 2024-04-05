package com.sgs.citytax.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.GeoFenceMapItemBinding
import com.sgs.citytax.model.GeoFenceLatLong
import com.sgs.citytax.ui.fragments.GeoFenceMapFragment

class GeoFenceLatLongAdapter(var adapterListener: GeoFenceMapFragment.AdapterListener?) : RecyclerView.Adapter<GeoFenceLatLongAdapter.GeoFenceViewHolder>() {
    var binding: GeoFenceMapItemBinding? = null
    var listGeoFenceLatLong:ArrayList<GeoFenceLatLong> = arrayListOf()

    class GeoFenceViewHolder(val binding: GeoFenceMapItemBinding?, val adapterListener: GeoFenceMapFragment.AdapterListener?) : RecyclerView.ViewHolder(binding?.root!!) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Any, position: Int){
            binding?.slNo?.text = "${position+1}) "
            binding?.mGeoFenceVM = data as GeoFenceLatLong?
            binding?.executePendingBindings()
            binding?.remove?.setOnClickListener{
                adapterListener?.removeMarker(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeoFenceViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.geo_fence_map_item, parent, false)
        return GeoFenceViewHolder(binding, adapterListener)
    }

    override fun getItemCount(): Int {
        return listGeoFenceLatLong.size
    }

    fun updateList(list:ArrayList<GeoFenceLatLong>){
        listGeoFenceLatLong.clear()
        listGeoFenceLatLong.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: GeoFenceViewHolder, position: Int) {
        if (listGeoFenceLatLong.size > 0 && listGeoFenceLatLong.lastIndex == position) {
            holder.binding?.remove?.visibility = VISIBLE
        } else {
            holder.binding?.remove?.visibility = GONE
        }
        holder.bind(listGeoFenceLatLong.get(position), position)
    }
}