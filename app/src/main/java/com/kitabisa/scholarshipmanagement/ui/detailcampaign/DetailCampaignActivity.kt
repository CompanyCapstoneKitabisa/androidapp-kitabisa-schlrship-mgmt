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
import com.kitabisa.scholarshipmanagement.ui.home.HomeActivity
import com.kitabisa.scholarshipmanagement.utils.Utils.loadImage
import java.util.*

class DetailCampaignActivity : AppCompatActivity(), ApplicantAdapter.ApplicantCallback {

    private lateinit var binding: ActivityDetailCampaignBinding
    private val applicantAdapter = ApplicantAdapter(this)
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private var tempToken: String = ""
    lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var campaignDetail: CampaignDetail
    private lateinit var customLoadingDialog: CustomLoadingDialog
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
    val firebaseUser = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCampaignBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.root.visibility = View.GONE

        customLoadingDialog = CustomLoadingDialog(this)

        idCampaign = intent.getStringExtra(ID_CAMPAIGN).toString() // 2

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

                                if (campaignDetail.processData == "0") {
                                    binding.apply {
                                        emptyIcon.visibility = View.VISIBLE
                                        emptyLabel.visibility = View.VISIBLE
                                        emptyDesc.text =
                                            "Applicant Data is in Process, Please Try Again Later"
                                        emptyDesc.visibility = View.VISIBLE
                                        btnEmpty.visibility = View.VISIBLE
                                        btnEmpty.setOnClickListener {
                                            startActivity(Intent(this@DetailCampaignActivity, HomeActivity::class.java))
                                            finish()
                                        }
                                    }
                                } else {
                                    getData(tempToken)
                                    renderLoading(false)
                                    binding.root.visibility = View.VISIBLE
                                    setSearchAndFilter()
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

        binding.apply {
            val layoutManager = LinearLayoutManager(this@DetailCampaignActivity)
            rvApplicant.layoutManager = layoutManager
            rvApplicant.setHasFixedSize(true)
            rvApplicant.adapter = applicantAdapter
        }
    }

    override fun onApplicantClick(applicant: ListApplicantsItem) {
        val applicantDetailIntent = Intent(this, DetailApplicantActivity::class.java)
        applicantDetailIntent.putExtra(DetailApplicantActivity.ID_APPLICANT, applicant.id)
        applicantDetailIntent.putExtra(ID_CAMPAIGN, idCampaign)
        startActivity(applicantDetailIntent)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, HomeActivity::class.java))
        super.onBackPressed()
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

        val options: HashMap<String, String> = HashMap()
        if (status != "") {
            options["status"] = status
        }
        if (nama != "") {
            options["nama"] = nama
        }
        if (provinsi != "") {
            options["provinsi"] = provinsi
        }
        if (statusRumah != "") {
            options["statusRumah"] = statusRumah
        }
        if (statusData != "") {
            options["statusData"] = statusData
        }

        detailCampaignViewModel.getAllApplicant(options, token, idCampaign).observe(this) {
            adapter.submitData(lifecycle, it)
        }
        adapter.addLoadStateListener { loadState ->
            Log.v("item count jyo", adapter.itemCount.toString())
            if (loadState.append.endOfPaginationReached && adapter.itemCount < 1) {
                showEmptyApplicant(true)
            } else {
                showEmptyApplicant(false)
            }
        }
    }

    private fun setDataEmpty() {
        status = ""
        nama = ""
        provinsi = ""
        statusRumah = ""
        statusData = ""
    }

    private fun showEmptyApplicant(state: Boolean) {

        if (state) {
            binding.apply {
                emptyIcon.visibility = View.VISIBLE
                emptyLabel.visibility = View.VISIBLE
                emptyDesc.text = "Try to use other filter setting"
                emptyDesc.visibility = View.VISIBLE
                btnEmpty.visibility = View.GONE
            }
        } else {
            binding.apply {
                emptyIcon.visibility = View.GONE
                emptyLabel.visibility = View.GONE
                emptyDesc.visibility = View.GONE
                btnEmpty.visibility = View.GONE
            }
        }
    }

    fun setSearchAndFilter() {
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val queryText = query!!.lowercase(Locale.getDefault())
                if (queryText.isNotEmpty()) {
                    nama = queryText
                    firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
                        getData(res.token.toString())
                    }
                } else {
                    nama = ""
                    firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
                        getData(res.token.toString())
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
                }
            }
        }

        binding.acceptedCount.setOnClickListener {
            setDataEmpty()
            status = "accepted"
            firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
                getData(res.token.toString())
            }
        }

        binding.rejectedCount.setOnClickListener {
            setDataEmpty()
            status = "rejected"
            firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
                getData(res.token.toString())
            }
        }

        binding.onholdCount.setOnClickListener {
            setDataEmpty()
            status = "onhold"
            firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
                getData(res.token.toString())
            }
        }

        binding.applicantCount.setOnClickListener {
            setDataEmpty()
            status = "pending"
            firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
                getData(res.token.toString())
            }
        }
    }
}