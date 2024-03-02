package com.softwill.alpha.institute.transport.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ItemTransportDetailsBinding
import com.softwill.alpha.institute.transport.model.TransportDetails


class TransportDetailsAdapter(
    private val context: Context,
    private var mList: ArrayList<TransportDetails>,
) :
    RecyclerView.Adapter<TransportDetailsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): TransportDetailsAdapter.ViewHolder {
        val binding: ItemTransportDetailsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_transport_details,
            parent,
            false
        )
        return ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(mList[position]) {



                binding.tvTitle.text = title
                binding.tvDesc.text = desc


            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(val binding: ItemTransportDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*fun removeItem(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
        notifyDataSetChanged()
    }*/

}