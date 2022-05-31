package com.kitabisa.scholarshipmanagement.ui.superadmin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kitabisa.scholarshipmanagement.R
import com.kitabisa.scholarshipmanagement.data.Campaign
import com.kitabisa.scholarshipmanagement.databinding.ItemCampaignAdminBinding
import com.kitabisa.scholarshipmanagement.databinding.ItemCampaignBinding
import com.kitabisa.scholarshipmanagement.utils.Utils.loadImage


class AdminCampaignAdapter(private val listCampaign: ArrayList<Campaign>) :
    RecyclerView.Adapter<AdminCampaignAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val ItemCampaignAdminBinding =
            ItemCampaignAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(ItemCampaignAdminBinding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listCampaign[position])
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listCampaign[position]) }
    }

    override fun getItemCount(): Int {
        return listCampaign.size
    }

    inner class ListViewHolder(private val binding: ItemCampaignAdminBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(campaign: Campaign) {
            with(binding) {
                campaignName.text = campaign.name
                digalangOleh.text = "Digalang oleh " + campaign.penggalangDana
                ivCampaignImage.loadImage(campaign.photoUrl, R.drawable.ic_image)
                btnProcessData.setOnClickListener {

                }
            }
        }

    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Campaign)
    }
}