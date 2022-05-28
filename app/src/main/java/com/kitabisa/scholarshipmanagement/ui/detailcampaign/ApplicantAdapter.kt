package com.kitabisa.scholarshipmanagement.ui.detailcampaign

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kitabisa.scholarshipmanagement.R
import com.kitabisa.scholarshipmanagement.data.Applicant
import com.kitabisa.scholarshipmanagement.data.Campaign
import com.kitabisa.scholarshipmanagement.databinding.ItemApplicantBinding
import com.kitabisa.scholarshipmanagement.utils.Utils.loadImage

class ApplicantAdapter(private val callback: ApplicantCallback) :
    RecyclerView.Adapter<ApplicantAdapter.ApplicantViewHolder>() {
    private val _data = ArrayList<Applicant>()

    fun setData(applicants: ArrayList<Applicant>?) {
        _data.clear()
        if (applicants != null) {
            _data.addAll(applicants)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicantViewHolder {
        val applicantBinding = ItemApplicantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ApplicantViewHolder(applicantBinding)
    }

    override fun onBindViewHolder(holder: ApplicantViewHolder, position: Int) {
        holder.bind(_data[position])
    }

    override fun getItemCount(): Int = _data.size

    inner class ApplicantViewHolder(private val binding: ItemApplicantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(applicant: Applicant) {
            with(binding) {
                applicantName.text = applicant.name
//                applicantUniversity.text = applicant.university
                applicantCity.text = applicant.city + ","
                applicantProvince.text = applicant.province
                ivProfile.loadImage(applicant.photoUrl, R.drawable.profile_icon)

                if (applicant.dataStatus == "valid"){
                    applicantDataIndicator.setImageResource(R.drawable.data_valid)
                }else{
                    applicantDataIndicator.setImageResource(R.drawable.data_invalid)
                }

                if (applicant.rumahStatus == "valid"){
                    applicantHomeIndicator.setImageResource(R.drawable.rumah_valid)
                }else{
                    applicantHomeIndicator.setImageResource(R.drawable.rumah_invalid)
                }

                if (applicant.status == "accepted"){
                    statusIndicator.setImageResource(R.drawable.accepted)
                }else if(applicant.status == "rejected"){
                    statusIndicator.setImageResource(R.drawable.rejected)
                }else if(applicant.status == "onhold"){
                    statusIndicator.setImageResource(R.drawable.onhold)
                }else{
                    statusIndicator.setImageResource(R.drawable.pending)
                }

                root.setOnClickListener { callback.onApplicantClick(applicant) }
            }
        }
    }

    interface ApplicantCallback {
        fun onApplicantClick(applicant: Applicant)
    }
}