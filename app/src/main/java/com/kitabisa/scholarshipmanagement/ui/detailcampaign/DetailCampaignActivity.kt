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
    private var status: String = "pending"
    private var nama: String = ""
    private var provinsi: String = ""
    private var statusRumah: String = ""
    private var statusData: String = ""
    val factory: DataViewModelFactory = DataViewModelFactory.getInstance()
    private val detailCampaignViewModel: DetailCampaignViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCampaignBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.root.visibility = View.GONE

        customLoadingDialog = CustomLoadingDialog(this)


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
                                    binding.apply {
                                        emptyIcon.visibility = View.VISIBLE
                                        emptyLabel.visibility = View.VISIBLE
                                        emptyDesc.text = "Applicant Data is in Process, Please Try Again Later"
                                        emptyDesc.visibility = View.VISIBLE
                                        btnEmpty.visibility = View.VISIBLE
                                        btnEmpty.setOnClickListener{
                                            finish()
                                        }
                                    }
                                }

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
                                Toast.makeText(
                                    this,
                                    result.data.message,
                                    Toast.LENGTH_SHORT
                                ).show()

                                renderLoading(false)
                                binding.root.visibility = View.VISIBLE

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
            getData(res.token.toString())
            renderLoading(false)
            binding.root.visibility = View.VISIBLE
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
                renderLoading(true)
                binding.root.visibility = View.GONE
                val queryText = query!!.lowercase(Locale.getDefault())
                if (queryText.isNotEmpty()) {
                    nama = queryText
                    firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
                        getData(res.token.toString())
                        renderLoading(false)
                        binding.root.visibility = View.VISIBLE
                    }
                } else {
                    nama = ""
                    firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
                        getData(res.token.toString())
                        renderLoading(false)
                        binding.root.visibility = View.VISIBLE
                    }
                }
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
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
                renderLoading(true)
                binding.root.visibility = View.GONE
                bottomSheetDialog.dismiss()
                setDataEmpty()

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

                radioButtonStatus?.text?.let {
                    when (radioButtonStatus.text) {
                        "Belum Direview" -> {
                            status = "pending"
                        }
                        "Diterima" -> {
                            status = "accepted"
                        }
                        "Onhold" -> {
                            status = "onhold"
                        }
                        "Ditolak" -> {
                            status = "rejected"
                        }
                    }
                } ?: run {
                    status = ""
                }

                radioButtonBerkas?.text?.let {
                    if (radioButtonBerkas.text.toString() == "Data Valid") {
                        statusData = "valid"
                    } else if (radioButtonBerkas.text.toString() == "Rumah Valid") {
                        statusRumah = "valid"
                    } else {
                        statusData = "valid"
                        statusRumah = "valid"
                    }
                } ?: run {
                    statusData = ""
                    statusRumah = ""
                }

                province.text?.let {
                    provinsi = province.text.toString()
                } ?: run {
                    provinsi = ""
                }

                firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
                    getData(res.token.toString())
                    renderLoading(false)
                    binding.root.visibility = View.VISIBLE
                }
            }
        }

        binding.acceptedCount.setOnClickListener {
            renderLoading(true)
            binding.root.visibility = View.GONE
            setDataEmpty()
            status = "accepted"
            firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
                getData(res.token.toString())
                renderLoading(false)
                binding.root.visibility = View.VISIBLE
            }
        }

        binding.rejectedCount.setOnClickListener {
            renderLoading(true)
            binding.root.visibility = View.GONE
            setDataEmpty()
            status = "rejected"
            firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
                getData(res.token.toString())
                renderLoading(false)
                binding.root.visibility = View.VISIBLE
            }
        }

        binding.onholdCount.setOnClickListener {
            renderLoading(true)
            binding.root.visibility = View.GONE
            setDataEmpty()
            status = "onhold"
            firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
                getData(res.token.toString())
                renderLoading(false)
                binding.root.visibility = View.VISIBLE
            }
        }

        binding.applicantCount.setOnClickListener {
            renderLoading(true)
            binding.root.visibility = View.GONE
            setDataEmpty()
            status = "pending"
            firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
                getData(res.token.toString())
                renderLoading(false)
                binding.root.visibility = View.VISIBLE
            }
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

    private fun getData(token: String) {
        Log.v("di jyo getData", idCampaign)
        val adapter = ApplicantAdapter(this)
        binding.rvApplicant.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        detailCampaignViewModel.getAllApplicant(status, nama, provinsi, statusRumah, statusData, token, idCampaign).observe(this) {
            adapter.submitData(lifecycle, it)
        }
//        if (adapter.itemCount == 0){
//            showEmptyApplicant()
//        }
    }

    private fun setDataEmpty(){
        status = ""
        nama = ""
        provinsi = ""
        statusRumah = ""
        statusData = ""
    }

    private fun showEmptyApplicant(){
        binding.apply {
            emptyIcon.visibility = View.VISIBLE
            emptyLabel.visibility = View.VISIBLE
            emptyDesc.text = "Try to use other filter setting"
            emptyDesc.visibility = View.VISIBLE
            btnEmpty.visibility = View.GONE
        }
    }
}