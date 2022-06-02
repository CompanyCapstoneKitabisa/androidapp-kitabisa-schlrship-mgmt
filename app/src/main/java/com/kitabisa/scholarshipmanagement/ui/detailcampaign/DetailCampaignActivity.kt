package com.kitabisa.scholarshipmanagement.ui.detailcampaign

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.kitabisa.scholarshipmanagement.R
import com.kitabisa.scholarshipmanagement.data.CampaignDetail
import com.kitabisa.scholarshipmanagement.data.ListApplicantsItem
import com.kitabisa.scholarshipmanagement.data.Resource
import com.kitabisa.scholarshipmanagement.databinding.ActivityDetailCampaignBinding
import com.kitabisa.scholarshipmanagement.ui.CustomLoadingDialog
import com.kitabisa.scholarshipmanagement.ui.DataViewModelFactory
import com.kitabisa.scholarshipmanagement.ui.detailapplicant.DetailApplicantActivity
import com.kitabisa.scholarshipmanagement.utils.Utils.loadImage
import java.util.*

class DetailCampaignActivity : AppCompatActivity(), ApplicantAdapter.ApplicantCallback {

    private lateinit var binding: ActivityDetailCampaignBinding
    private val applicantAdapter = ApplicantAdapter(this)
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private var tempToken: String = ""
    val tempListApplicant = ArrayList<ListApplicantsItem>()
    val tempListApplicant2 = ArrayList<ListApplicantsItem>()
    lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var campaignDetail: CampaignDetail
    private lateinit var customLoadingDialog: CustomLoadingDialog
    var listApplicant = ArrayList<ListApplicantsItem>()
    private lateinit var idCampaign: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCampaignBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.root.visibility = View.GONE

        customLoadingDialog = CustomLoadingDialog(this)
        val factory: DataViewModelFactory = DataViewModelFactory.getInstance()
        val detailCampaignViewModel: DetailCampaignViewModel by viewModels {
            factory
        }

        val firebaseUser = auth.currentUser

        idCampaign = intent.getStringExtra(ID_CAMPAIGN).toString() // 2

        //comment code to use local data
        firebaseUser?.getIdToken(true)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                tempToken = task.result.token.toString()
                detailCampaignViewModel.getCampaignDetail(
                    tempToken,
                    idCampaign
                ).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Resource.Success -> {
                                campaignDetail = result.data?.Data!!

                                if(campaignDetail.processData == "0" || campaignDetail.processPageNumber == "0"){
                                    finish()
                                    Toast.makeText(
                                        this,
                                        "Data Pada Campaign ${campaignDetail.name} Masih Diproses",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }else{
                                    binding.apply {
                                        campaignName.text = campaignDetail.name
                                        applicantCount.text =
                                            campaignDetail.pendingApplicants.toString()
                                        acceptedCount.text =
                                            campaignDetail.acceptedApplicants.toString()
                                        onholdCount.text =
                                            campaignDetail.onHoldApplicants.toString()
                                        rejectedCount.text =
                                            campaignDetail.rejectedApplicants.toString()
                                        ivCampaignPhoto.loadImage(
                                            campaignDetail.photoUrl,
                                            R.drawable.ic_image
                                        )
                                    }
//                                    Toast.makeText(
//                                        this,
//                                        result.data.message,
//                                        Toast.LENGTH_SHORT
//                                    ).show()
                                }
                            }
                            is Resource.Error -> {
                                finish()
                                Toast.makeText(
                                    this,
                                    result.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is Resource.Loading -> {
                                renderLoading(true)
                                binding.root.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }?.addOnFailureListener {
            renderLoading(false)
            finishAffinity()
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }

        firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
            detailCampaignViewModel.getAllApplicant(res.token.toString(), idCampaign)
                .observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Resource.Success -> {
                                listApplicant = result.data?.listApplicants!!
                                tempListApplicant.clear()
                                for (applicant in listApplicant) {
                                    if (applicant.statusApplicant.lowercase(Locale.getDefault()).contains("pending")) {
                                        tempListApplicant.add(applicant)
                                    }
                                }
                                applicantAdapter.setData(tempListApplicant)
                                renderLoading(false)
                                binding.root.visibility = View.VISIBLE
//                                Toast.makeText(
//                                    this,
//                                    result.data.message,
//                                    Toast.LENGTH_SHORT
//                                ).show()
                            }
                            is Resource.Error -> {
                                finish()
                                Toast.makeText(
                                    this,
                                    result.data?.error.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is Resource.Loading -> {
                                renderLoading(true)
                                binding.root.visibility = View.GONE
                            }
                        }
                    }
                }
        }
        //end

        binding.apply {
            val layoutManager = LinearLayoutManager(this@DetailCampaignActivity)
            rvApplicant.layoutManager = layoutManager
            rvApplicant.setHasFixedSize(true)
            rvApplicant.adapter = applicantAdapter
        }


        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                tempListApplicant2.clear()
                val queryText = query!!.lowercase(Locale.getDefault())
                if (queryText.isNotEmpty()) {
                    for (applicant in tempListApplicant) {
                        if (applicant.name.lowercase(Locale.getDefault()).contains(queryText)) {
                            tempListApplicant2.add(applicant)
                        }
                    }

                    applicantAdapter.setData(tempListApplicant2)
                } else {
                    tempListApplicant2.addAll(tempListApplicant)
                    applicantAdapter.setData(tempListApplicant2)
                }
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                Log.v("search", "onchange masuk")
                tempListApplicant2.clear()
                val queryText = query!!.lowercase(Locale.getDefault())
                if (queryText.isNotEmpty()) {
                    for (applicant in tempListApplicant) {
                        if (applicant.name.lowercase(Locale.getDefault()).contains(queryText)) {
                            tempListApplicant2.add(applicant)
                        }
                    }

                    applicantAdapter.setData(tempListApplicant2)
                } else {
                    tempListApplicant2.addAll(tempListApplicant)
                    applicantAdapter.setData(tempListApplicant2)
                }
                return false
            }

        })

        binding.filter.setOnClickListener {
            @SuppressLint("InflateParams")
            val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_filter, null)

            bottomSheetDialog = BottomSheetDialog(this@DetailCampaignActivity)
            bottomSheetDialog.setContentView(dialogView)
            bottomSheetDialog.show()

            val radioGroupStatus: RadioGroup = dialogView.findViewById(R.id.status_applicant_group)
            val radioGroupBerkas: RadioGroup = dialogView.findViewById(R.id.status_berkas_group)
            val province: TextInputEditText = dialogView.findViewById(R.id.province)

            val applyButton: Button = dialogView.findViewById(R.id.btn_apply)
            applyButton.setOnClickListener {
                bottomSheetDialog.dismiss()

                val selectedOptionStatus: Int = radioGroupStatus.checkedRadioButtonId
                val radioButtonStatus: RadioButton? = selectedOptionStatus.let { it1 ->
                    dialogView.findViewById(
                        it1
                    )
                }

                val selectedOptionBerkas: Int = radioGroupBerkas.checkedRadioButtonId
                val radioButtonBerkas: RadioButton? = selectedOptionBerkas.let { it1 ->
                    dialogView.findViewById(
                        it1
                    )
                }

                tempListApplicant.clear()
                radioButtonStatus?.text?.let {
                    var applicantStatusFilter = ""
                    when (radioButtonStatus.text) {
                        "Belum Direview" -> {
                            applicantStatusFilter = "pending"
                        }
                        "Diterima" -> {
                            applicantStatusFilter = "accepted"
                        }
                        "Onhold" -> {
                            applicantStatusFilter = "onhold"
                        }
                        "Ditolak" -> {
                            applicantStatusFilter = "rejected"
                        }
                    }

                    for (applicant in listApplicant) {
                        if (applicant.statusApplicant.lowercase(Locale.getDefault())
                                .contains(applicantStatusFilter)
                        ) {
                            tempListApplicant.add(applicant)
                        }
                    }
                } ?: run {
                    tempListApplicant.addAll(listApplicant)
                }


                radioButtonBerkas?.text?.let {
                    if (radioButtonBerkas.text.toString() == "Data Valid") {
                        for (applicant in tempListApplicant) {
                            if (applicant.statusData.lowercase(Locale.getDefault()) == "valid") {
                                tempListApplicant2.add(applicant)
                            }
                        }
                    } else if (radioButtonBerkas.text.toString() == "Rumah Valid") {
                        for (applicant in tempListApplicant) {
                            if (applicant.statusRumah.lowercase(Locale.getDefault()) == "valid") {
                                tempListApplicant2.add(applicant)
                            }
                        }
                    } else {
                        for (applicant in tempListApplicant) {
                            if (applicant.statusRumah.lowercase(Locale.getDefault()) == "valid" && applicant.statusData.lowercase(
                                    Locale.getDefault()
                                ) == "valid"
                            ) {
                                tempListApplicant2.add(applicant)
                            }
                        }
                    }
                } ?: run {
                    tempListApplicant2.addAll(tempListApplicant)
                }

                tempListApplicant.clear()
                province.text?.let {
                    for (applicant in tempListApplicant2) {
                        if (applicant.provinsi.lowercase(Locale.getDefault())
                                .contains(province.text.toString().lowercase())
                        ) {
                            tempListApplicant.add(applicant)
                        }
                    }
                } ?: run {
                    tempListApplicant.addAll(tempListApplicant2)
                }

                tempListApplicant2.clear()
                applicantAdapter.setData(tempListApplicant)
            }
        }

        binding.acceptedCount.setOnClickListener {
            tempListApplicant.clear()
            for (applicant in listApplicant) {
                if (applicant.statusApplicant.lowercase(Locale.getDefault()).contains("accepted")) {
                    tempListApplicant.add(applicant)
                }
            }
            applicantAdapter.setData(tempListApplicant)
        }

        binding.rejectedCount.setOnClickListener {
            tempListApplicant.clear()
            for (applicant in listApplicant) {
                if (applicant.statusApplicant.lowercase(Locale.getDefault()).contains("rejected")) {
                    tempListApplicant.add(applicant)
                }
            }
            applicantAdapter.setData(tempListApplicant)
        }

        binding.onholdCount.setOnClickListener {
            tempListApplicant.clear()
            for (applicant in listApplicant) {
                if (applicant.statusApplicant.lowercase(Locale.getDefault()).contains("onhold")) {
                    tempListApplicant.add(applicant)
                }
            }
            applicantAdapter.setData(tempListApplicant)
        }

        binding.applicantCount.setOnClickListener {
            tempListApplicant.clear()
            for (applicant in listApplicant) {
                if (applicant.statusApplicant.lowercase(Locale.getDefault()).contains("pending")) {
                    tempListApplicant.add(applicant)
                }
            }
            applicantAdapter.setData(tempListApplicant)
        }
    }

    override fun onApplicantClick(applicant: ListApplicantsItem) {
        val applicantDetailIntent = Intent(this, DetailApplicantActivity::class.java)
        applicantDetailIntent.putExtra(DetailApplicantActivity.ID_APPLICANT, applicant.id)
        applicantDetailIntent.putExtra(ID_CAMPAIGN, idCampaign)
        startActivity(applicantDetailIntent)
    }

    private fun renderLoading(state: Boolean) {
        if (state) {
            customLoadingDialog.show()
            customLoadingDialog.setCancelable(false)
        } else {
            customLoadingDialog.dismiss()
        }
    }

    companion object {
        const val ID_CAMPAIGN = "id_campaign"
    }
}