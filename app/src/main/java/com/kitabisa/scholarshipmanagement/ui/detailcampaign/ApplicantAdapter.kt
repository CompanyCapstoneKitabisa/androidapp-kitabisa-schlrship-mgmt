package com.kitabisa.scholarshipmanagement.ui.detailcampaign

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kitabisa.scholarshipmanagement.R
import com.kitabisa.scholarshipmanagement.data.ListApplicantsItem
import com.kitabisa.scholarshipmanagement.databinding.ItemApplicantBinding
import com.kitabisa.scholarshipmanagement.utils.Utils.loadImage

class ApplicantAdapter(private val callback: ApplicantCallback) :
    RecyclerView.Adapter<ApplicantAdapter.ApplicantViewHolder>() {
    private val _data = ArrayList<ListApplicantsItem>()

    fun setData(applicants: ArrayList<ListApplicantsItem>?) {
        _data.clear()
        if (applicants != null) {
            _data.addAll(applicants)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicantViewHolder {
        val applicantBinding =
            ItemApplicantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ApplicantViewHolder(applicantBinding)
    }

    override fun onBindViewHolder(holder: ApplicantViewHolder, position: Int) {
        holder.bind(_data[position])
    }

    override fun getItemCount(): Int = _data.size

    inner class ApplicantViewHolder(private val binding: ItemApplicantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(applicant: ListApplicantsItem) {
            with(binding) {
                applicantName.text = applicant.name
//                applicantUniversity.text = applicant.university
                applicantCity.text = applicant.kota + ","
                applicantProvince.text = applicant.provinsi
                ivProfile.loadImage(applicant.photoURL, R.drawable.profile_icon)

                if (applicant.statusData == "valid") {
                    applicantDataIndicator.setImageResource(R.drawable.data_valid)
                } else {
                    applicantDataIndicator.setImageResource(R.drawable.data_invalid)
                }

                if (applicant.statusRumah == "valid") {
                    applicantHomeIndicator.setImageResource(R.drawable.rumah_valid)
                } else {
                    applicantHomeIndicator.setImageResource(R.drawable.rumah_invalid)
                }

                if (applicant.statusApplicant == "accepted") {
                    statusIndicator.setImageResource(R.drawable.accepted)
                } else if (applicant.statusApplicant == "rejected") {
                    statusIndicator.setImageResource(R.drawable.rejected)
                } else if (applicant.statusApplicant == "onhold") {
                    statusIndicator.setImageResource(R.drawable.onhold)
                } else {
                    statusIndicator.setImageResource(R.drawable.pending)
                }

                score.text = applicant.score.toString()

                root.setOnClickListener { callback.onApplicantClick(applicant) }
            }
        }
    }

    interface ApplicantCallback {
        fun onApplicantClick(applicant: ListApplicantsItem)
    }
}