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
    private val factory: DataViewModelFactory = DataViewModelFactory.getInstance()
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private val detailCampaignViewModel: DetailCampaignViewModel by viewModels {
        factory
    }
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var campaignDetail: CampaignDetail
    private lateinit var customLoadingDialog: CustomLoadingDialog
    private lateinit var idCampaign: String
    private lateinit var campaignName: String
    private lateinit var broadcastReceiver: BroadcastReceiver
    private val firebaseUser = auth.currentUser
    private var tempToken: String = ""
    private var status: String = "pending"
    private var nama: String = ""
    private var provinsi: String = ""
    private var statusRumah: String = ""
    private var statusData: String = ""

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

        idCampaign = intent.getStringExtra(ID_CAMPAIGN).toString()
        campaignName = intent.getStringExtra(NAMA_CAMPAIGN).toString()
        firebaseUser?.getIdToken(true)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                tempToken = task.result.token.toString()
                detailCampaignViewModel.getCampaignDetail(tempToken, idCampaign)
                    .observe(this) { result ->
                        if (result != null) {
                            when (result) {
                                is Resource.Success -> {
                                    campaignDetail = result.data?.Data!!
                                    isDataProcessed()
                                    setDetailCampaignData()
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

    private fun setDetailCampaignData(){
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
    }

    private fun renderLoading(state: Boolean) {
        if (state) {
            customLoadingDialog.show()
            customLoadingDialog.setCancelable(false)
        } else {
            customLoadingDialog.dismiss()
        }
    }

    private fun getApplicantData() {
        firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
            val token = res.token.toString()
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
                    applicantEmptyState(true)
                } else {
                    applicantEmptyState(false)
                }
            }
        }
    }

    private fun setApplicantDataFilterEmpty() {
        status = ""
        nama = ""
        provinsi = ""
        statusRumah = ""
        statusData = ""
    }

    private fun applicantEmptyState(state: Boolean) {
        if (state) {
            binding.apply {
                emptyIcon.visibility = View.VISIBLE
                emptyLabel.visibility = View.VISIBLE
                emptyDesc.text = getString(R.string.use_other_filter)
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

    private fun isDataProcessed(){
        if (campaignDetail.processData == "0") {
            binding.apply {
                emptyIcon.visibility = View.VISIBLE
                emptyLabel.visibility = View.VISIBLE
                emptyDesc.text = getString(R.string.data_in_process)
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
            renderLoading(false)
            binding.root.visibility = View.VISIBLE
        } else {
            triggerData(tempToken)
        }
    }

    private fun setSearchAndFilter() {
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val queryText = query!!.lowercase(Locale.getDefault())
                if (queryText.isNotEmpty()) {
                    nama = queryText
                    getApplicantData()
                } else {
                    nama = ""
                    getApplicantData()
                }
                binding.search.clearFocus()
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query == ""){
                    nama = ""
                    getApplicantData()
                }
                return false
            }
        })

        binding.filter.setOnClickListener { it ->
            val popup = PopupMenu(this, it)
            popup.inflate(R.menu.campaign_detail_menu)

            popup.setOnMenuItemClickListener { item: MenuItem? ->

                when (item!!.itemId) {
                    R.id.download_data -> {
                        firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
                            detailCampaignViewModel.downloadCsv(res.token.toString(), idCampaign)
                                .observe(this) {
                                    if (it != null) {
                                        when (it) {
                                            is Resource.Success -> {
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
            }
            popup.show()
        }

        binding.acceptedCount.setOnClickListener {
            setApplicantDataFilterEmpty()
            status = "accepted"
            getApplicantData()
            showIndicator("accepted")
        }

        binding.rejectedCount.setOnClickListener {
            setApplicantDataFilterEmpty()
            status = "rejected"
            getApplicantData()
            showIndicator("rejected")
        }

        binding.onholdCount.setOnClickListener {
            setApplicantDataFilterEmpty()
            status = "onhold"
            getApplicantData()
            showIndicator("onhold")
        }

        binding.applicantCount.setOnClickListener {
            setApplicantDataFilterEmpty()
            status = "pending"
            getApplicantData()
            showIndicator("pending")
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

    private fun triggerData(token: String) {
        detailCampaignViewModel.triggerDataProcess(token, idCampaign)
            .observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Resource.Success -> {
                            renderLoading(false)
                            Toast.makeText(
                                this, result.data?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            triggerDetailCampaign(token, idCampaign)
                        }
                        is Resource.Error -> {
                            if (result.message.toString().contains("404")) {
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

    private fun triggerDetailCampaign(token: String, id: String) {
        detailCampaignViewModel.getCampaignDetail(
            token, id
        ).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Resource.Success -> {
                        campaignDetail = result.data?.Data!!
                        getApplicantData()
                        setSearchAndFilter()
                        renderLoading(false)
                        binding.root.visibility = View.VISIBLE

                        setDetailCampaignData()
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

    private fun showFilter() {
        @SuppressLint("InflateParams")
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_filter, null)

        bottomSheetDialog = BottomSheetDialog(this@DetailCampaignActivity)
        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.show()

        val radioGroupStatus: RadioGroup = dialogView.findViewById(R.id.status_applicant_group)
        val radioGroupBerkas: RadioGroup = dialogView.findViewById(R.id.status_berkas_group)
        val province: TextInputEditText = dialogView.findViewById(R.id.province)

        when (status) {
            "pending" -> {
                radioGroupStatus.check(R.id.pending)
            }
            "accepted" -> {
                radioGroupStatus.check(R.id.accepted)
            }
            "onhold" -> {
                radioGroupStatus.check(R.id.onhold)
            }
            "rejected" -> {
                radioGroupStatus.check(R.id.rejected)
            }
        }

        if (statusData == "valid" && statusRumah == "valid"){
            radioGroupBerkas.check(R.id.data_dan_rumah_valid)
        }else if(statusRumah == "valid"){
            radioGroupBerkas.check(R.id.rumah_calid)
        }else if(statusData == "valid"){
            radioGroupBerkas.check(R.id.data_valid)
        }

        if (provinsi != ""){
            province.setText(provinsi)
        }

        val clearButton: Button = dialogView.findViewById(R.id.btn_clear_filter)
        clearButton.setOnClickListener {
            bottomSheetDialog.dismiss()
            setApplicantDataFilterEmpty()
            status = "pending"
            showIndicator("pending")
            getApplicantData()
        }

        val applyButton: Button = dialogView.findViewById(R.id.btn_apply)
        applyButton.setOnClickListener {
            bottomSheetDialog.dismiss()
            setApplicantDataFilterEmpty()

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
                    "Pending" -> {
                        status = "pending"
                    }
                    "Accepted" -> {
                        status = "accepted"
                    }
                    "On Hold" -> {
                        status = "onhold"
                    }
                    "Rejected" -> {
                        status = "rejected"
                    }
                }
            } ?: run {
                status = ""
            }

            radioButtonBerkas?.text?.let {
                when {
                    radioButtonBerkas.text.toString() == "Data Valid" -> {
                        statusData = "valid"
                    }
                    radioButtonBerkas.text.toString() == "Home Valid" -> {
                        statusRumah = "valid"
                    }
                    else -> {
                        statusData = "valid"
                        statusRumah = "valid"
                    }
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

            getApplicantData()
        }
    }

    private fun showIndicator(status: String){
        binding.apply {
            remainingIndicator.visibility = View.INVISIBLE
            acceptedIndicator.visibility = View.INVISIBLE
            onholdIndicator.visibility = View.INVISIBLE
            rejectedIndicator.visibility = View.INVISIBLE
        }
        when (status) {
            "pending" -> {
                binding.remainingIndicator.visibility = View.VISIBLE
            }
            "accepted" -> {
                binding.acceptedIndicator.visibility = View.VISIBLE
            }
            "onhold" -> {
                binding.onholdIndicator.visibility = View.VISIBLE
            }
            "rejected" -> {
                binding.rejectedIndicator.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        const val ID_CAMPAIGN = "id_campaign"
        const val NAMA_CAMPAIGN = "nama_campaign"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}