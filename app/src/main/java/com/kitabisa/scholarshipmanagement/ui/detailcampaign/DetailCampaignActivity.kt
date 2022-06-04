package com.kitabisa.scholarshipmanagement.ui.detailcampaign

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import java.io.File
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
    private lateinit var campaignName: String
    private lateinit var broadcastReceiver: BroadcastReceiver
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!isPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun isPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCampaignBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.root.visibility = View.GONE

        customLoadingDialog = CustomLoadingDialog(this)

        idCampaign = intent.getStringExtra(ID_CAMPAIGN).toString() // 2
        campaignName = intent.getStringExtra(NAMA_CAMPAIGN).toString()
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
                                            startActivity(
                                                Intent(
                                                    this@DetailCampaignActivity,
                                                    HomeActivity::class.java
                                                )
                                            )
                                            finish()
                                        }
                                    }
                                } else {
                                    triggerData(tempToken, campaignDetail.applicantsCount)
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

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context, p1: Intent) {
                Toast.makeText(
                    p0,
                    "Download Complete",
                    Toast.LENGTH_SHORT
                ).show()
                renderLoading(false)
            }
        }
        registerReceiver(
            broadcastReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
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
        const val NAMA_CAMPAIGN = "nama_campaign"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private fun getData(token: String) {
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

    private fun setSearchAndFilter() {
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
            val popup = PopupMenu(this, it)
            popup.inflate(R.menu.campaign_detail_menu)

            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

                when (item!!.itemId) {
                    R.id.download_data -> {
                        firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
                            detailCampaignViewModel.downloadCsv(res.token.toString(), idCampaign)
                                .observe(this) {
                                    if (it != null) {
                                        when (it) {
                                            is Resource.Success -> {
                                                Log.d("URLCSV", it.data?.fileDownload.toString())
                                                if (!isPermissionsGranted()) {
                                                    ActivityCompat.requestPermissions(
                                                        this@DetailCampaignActivity,
                                                        REQUIRED_PERMISSIONS,
                                                        REQUEST_CODE_PERMISSIONS
                                                    )
                                                } else {
                                                    downloadAccCsv(
                                                        it.data?.fileDownload?.get(0).toString(),
                                                        campaignName
                                                    )
                                                }
                                            }
                                            is Resource.Error -> {
                                                finish()
                                                Toast.makeText(
                                                    this,
                                                    it.message.toString(),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            is Resource.Loading -> {
                                                renderLoading(true)
                                            }
                                        }
                                    }
                                }
                        }
                    }
                    R.id.show_filter -> {
                        showFilter()
                    }
                }
                true
            })
            popup.show()
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

    private fun downloadAccCsv(url: String, campaignName: String) {
        try {
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val documentsLink = Uri.parse(url)
            val request = DownloadManager.Request(documentsLink)
            val tempName = "$campaignName Accepted Results"
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                .setMimeType("text/csv")
                .setAllowedOverRoaming(false)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(tempName)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    File.separator + tempName + ".csv"
                )
            downloadManager.enqueue(request)
        } catch (e: Exception) {
            renderLoading(false)
            Toast.makeText(this, "Download Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun triggerData(token: String, applicantsCount: Int){
        detailCampaignViewModel.triggerDataProcess(token, idCampaign, applicantsCount)
            .observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Resource.Success -> {
                            renderLoading(false)
                            Toast.makeText(
                                this, result.data?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            triggerPagingFunction(token, idCampaign)
                        }
                        is Resource.Error -> {
                            if(result.message.toString().contains("404")) {
                                Toast.makeText(
                                    this,
                                    result.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                renderLoading(false)
                            } else {
                                Toast.makeText(
                                    this,
                                    result.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is Resource.Loading -> {
                            renderLoading(true)
                        }
                    }
                }
            }
    }

    private fun triggerPagingFunction(token: String, id: String) {
        detailCampaignViewModel.triggerPagingData(token, id)
            .observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Resource.Success -> {
                            getData(tempToken)
                            renderLoading(false)
                            binding.root.visibility = View.VISIBLE
                            setSearchAndFilter()
                        }
                        is Resource.Error -> {
                            renderLoading(false)
                            Toast.makeText(
                                this,
                                result.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is Resource.Loading -> {
                            renderLoading(true)
                        }
                    }
                }
            }
    }

    private fun showFilter(){
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
}