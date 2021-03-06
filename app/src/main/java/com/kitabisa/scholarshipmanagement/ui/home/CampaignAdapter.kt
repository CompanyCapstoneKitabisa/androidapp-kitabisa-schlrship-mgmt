package com.kitabisa.scholarshipmanagement.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kitabisa.scholarshipmanagement.R
import com.kitabisa.scholarshipmanagement.data.Campaign
import com.kitabisa.scholarshipmanagement.databinding.ItemCampaignBinding
import com.kitabisa.scholarshipmanagement.utils.Utils.loadImage

class CampaignAdapter(private val callback: CampaignCallback) :
    RecyclerView.Adapter<CampaignAdapter.CampaignViewHolder>() {
    private val _data = ArrayList<Campaign>()

    fun setData(campaigns: ArrayList<Campaign>?) {
        _data.clear()
        if (campaigns != null) {
            _data.addAll(campaigns)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignViewHolder {
        val campaignBinding =
            ItemCampaignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CampaignViewHolder(campaignBinding)
    }

    override fun onBindViewHolder(holder: CampaignViewHolder, position: Int) {
        holder.bind(_data[position])
    }

    override fun getItemCount(): Int = _data.size

    inner class CampaignViewHolder(private val binding: ItemCampaignBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(campaign: Campaign) {
            with(binding) {
                campaignName.text = campaign.name
                digalangOleh.text = "Digalang oleh " + campaign.penggalangDana
                ivCampaignImage.loadImage(campaign.photoUrl, R.drawable.ic_image)
                root.setOnClickListener { callback.onStoryClick(campaign) }
            }
        }
    }

    interface CampaignCallback {
        fun onStoryClick(campaign: Campaign)
    }
}