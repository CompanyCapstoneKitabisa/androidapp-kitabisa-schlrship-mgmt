package com.kitabisa.scholarshipmanagement.ui.detailcampaign

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.kitabisa.scholarshipmanagement.R
import com.kitabisa.scholarshipmanagement.data.Applicant
import com.kitabisa.scholarshipmanagement.data.Campaign
import com.kitabisa.scholarshipmanagement.data.CampaignDetail
import com.kitabisa.scholarshipmanagement.data.Resource
import com.kitabisa.scholarshipmanagement.databinding.ActivityDetailCampaignBinding
import com.kitabisa.scholarshipmanagement.databinding.ActivityHomeBinding
import com.kitabisa.scholarshipmanagement.databinding.BottomSheetFilterBinding
import com.kitabisa.scholarshipmanagement.ui.DataViewModelFactory
import com.kitabisa.scholarshipmanagement.ui.detailapplicant.DetailApplicantActivity
import com.kitabisa.scholarshipmanagement.ui.home.CampaignAdapter
import com.kitabisa.scholarshipmanagement.ui.home.HomeViewModel
import com.kitabisa.scholarshipmanagement.utils.Utils.loadImage
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.contains as contains1

class DetailCampaignActivity : AppCompatActivity(), ApplicantAdapter.ApplicantCallback {

    private lateinit var binding: ActivityDetailCampaignBinding
    private lateinit var bottomSheetBinding: BottomSheetFilterBinding
    private val applicantAdapter = ApplicantAdapter(this)
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    val tempListApplicant = ArrayList<Applicant>()
    val tempListApplicant2 = ArrayList<Applicant>()
    lateinit var bottomSheetDialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCampaignBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val factory: DataViewModelFactory = DataViewModelFactory.getInstance()
        val detailCampaignViewModel: DetailCampaignViewModel by viewModels {
            factory
        }

        val firebaseUser = auth.currentUser

//        firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
//            detailCampaignViewModel.getCampaignDetail(res.token.toString(), ID_CAMPAIGN).observe(this) { result ->
//                if (result != null) {
//                    when (result) {
//                        is Resource.Success -> {
//                            Log.v("Test", result.data?.Data.toString())
//                        }
//                        is Resource.Error -> {
//                            Log.v("Test", "Gagal Mendapatkan Data Detail Campaign\"")
//                        }
//                        else -> {}
//                    }
//                }
//            }
//        }
//
//        firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
//            detailCampaignViewModel.getAllApplicant(res.token.toString(), ID_CAMPAIGN).observe(this) { result ->
//                if (result != null) {
//                    when (result) {
//                        is Resource.Success -> {
//                            applicantAdapter.setData(result.data?.listApplicants)
//                            Log.v("Test", result.data?.listApplicants.toString())
//                        }
//                        is Resource.Error -> {
//                            Log.v("Test", "Gagal Mendapatkan Data Detail Campaign\"")
//                        }
//                        else -> {}
//                    }
//                }
//            }
//        }
//
//        binding.apply {
//            val layoutManager = LinearLayoutManager(this@DetailCampaignActivity)
//            rvApplicant.layoutManager = layoutManager
//            rvApplicant.setHasFixedSize(true)
//            rvApplicant.adapter = applicantAdapter
//        }



        //local data (hapus ini nanti)
        val campaignDetail = CampaignDetail("Beasiswa Narasi", "Narasi", "https://campuspedia.id/news/wp-content/uploads/2021/08/Beasiswa-Celengan-Narasi.jpg", 3023, 76, 367, 57)

        val listApplicant = ArrayList<Applicant>()

        for (y in 1..3) {
            val applicant = Applicant(y.toString(), "Nyoman Jyotisa", "Universitas Udayana", "pending", "Gianyar", "Bali", "valid", "valid", "https://1.bp.blogspot.com/-oIdHWQIe0lY/Vt7KVnjR7WI/AAAAAAAAAUo/1whO1HjqUYs/s320/Contoh%2BPas%2BFoto.png")
            listApplicant.add(applicant)
            val accepted = Applicant(y.toString(), "Wayan Made", "Universitas Indonesia", "accepted", "Denpasar", "Bali", "invalid", "valid", "https://jasafotosemarang.files.wordpress.com/2016/10/rizky.jpg")
            listApplicant.add(accepted)
            val rejected = Applicant(y.toString(), "Paul Lennon", "Universitas Diponogoro", "rejected", "Jakarta Selatan", "Jakarta", "invalid", "invalid", "https://www.superprof.co.id/gambar/kursus-pelatihan/mini-saya-mengajar-akuntansi-sukabumi-dan-saya-sebagai-mahasiswa-akuntansi-universitas-nusa-putra.jpg")
            listApplicant.add(rejected)
            val onhold = Applicant(y.toString(), "Ketut Garing", "Universitas Warmadewa", "onhold", "Kintamani", "Bali", "valid", "invalid", "")
            listApplicant.add(onhold)
        }

        tempListApplicant.addAll(listApplicant)
        applicantAdapter.setData(tempListApplicant)

        binding.apply {
            campaignName.text = campaignDetail.name
            applicantCount.text = campaignDetail.applicantsCount.toString()
            acceptedCount.text = campaignDetail.acceptedApplicants.toString()
            onholdCount.text = campaignDetail.onholdApplicants.toString()
            rejectedCount.text = campaignDetail.rejectedApplicants.toString()
            ivCampaignPhoto.loadImage(campaignDetail.photoUrl)


            val layoutManager = LinearLayoutManager(this@DetailCampaignActivity)
            rvApplicant.layoutManager = layoutManager
            rvApplicant.setHasFixedSize(true)
            rvApplicant.adapter = applicantAdapter
        }

        //end

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
             tempListApplicant.clear()
                val queryText = query!!.lowercase(Locale.getDefault())
                if (queryText.isNotEmpty()){
                    for (applicant in listApplicant) {
                        if (applicant.name.lowercase(Locale.getDefault()).contains(queryText)){
                            tempListApplicant.add(applicant)
                        }
                    }

                    applicantAdapter.setData(tempListApplicant)
                }else{
                    tempListApplicant.clear()
                    tempListApplicant.addAll(listApplicant)
                    applicantAdapter.setData(tempListApplicant)
                }
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                Log.v("search", "onchange masuk")
                tempListApplicant.clear()
                val queryText = query!!.lowercase(Locale.getDefault())
                if (queryText.isNotEmpty()){
                    for (applicant in listApplicant) {
                        if (applicant.name.lowercase(Locale.getDefault()).contains(queryText)){
                            tempListApplicant.add(applicant)
                        }
                    }

                    applicantAdapter.setData(tempListApplicant)
                }else{
                    tempListApplicant.clear()
                    tempListApplicant.addAll(listApplicant)
                    applicantAdapter.setData(tempListApplicant)
                }
                return false
            }

        })

        binding.filter.setOnClickListener {
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

                val selectedOptionStatus: Int? = radioGroupStatus.checkedRadioButtonId
                val radioButtonStatus: RadioButton? = selectedOptionStatus?.let { it1 ->
                    dialogView.findViewById(
                        it1
                    )
                }

                val selectedOptionBerkas: Int? = radioGroupBerkas.checkedRadioButtonId
                val radioButtonBerkas: RadioButton? = selectedOptionBerkas?.let { it1 ->
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
                        if (applicant.status.lowercase(Locale.getDefault()).contains(applicantStatusFilter)){
                            tempListApplicant.add(applicant)
                        }
                    }
                } ?: run {
                    tempListApplicant.addAll(listApplicant)
                }


                radioButtonBerkas?.text?.let {
                    if (radioButtonBerkas.text.toString() == "Data Valid"){
                        for (applicant in tempListApplicant) {
                            if (applicant.data_status.lowercase(Locale.getDefault()) == "valid"){
                                tempListApplicant2.add(applicant)
                            }
                        }
                    }else if (radioButtonBerkas.text.toString() == "Rumah Valid"){
                        for (applicant in tempListApplicant) {
                            if (applicant.rumah_status.lowercase(Locale.getDefault()) == "valid"){
                                tempListApplicant2.add(applicant)
                            }
                        }
                    }else{
                        for (applicant in tempListApplicant) {
                            if (applicant.rumah_status.lowercase(Locale.getDefault()) == "valid" && applicant.data_status.lowercase(Locale.getDefault()) == "valid"){
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
                        if (applicant.province.lowercase(Locale.getDefault()).contains(province.text.toString().lowercase())){
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
                if (applicant.status.lowercase(Locale.getDefault()).contains("accepted")){
                    tempListApplicant.add(applicant)
                }
            }
            applicantAdapter.setData(tempListApplicant)
        }

        binding.rejectedCount.setOnClickListener {
            tempListApplicant.clear()
            for (applicant in listApplicant) {
                if (applicant.status.lowercase(Locale.getDefault()).contains("rejected")){
                    tempListApplicant.add(applicant)
                }
            }
            applicantAdapter.setData(tempListApplicant)
        }

        binding.onholdCount.setOnClickListener {
            tempListApplicant.clear()
            for (applicant in listApplicant) {
                if (applicant.status.lowercase(Locale.getDefault()).contains("onhold")){
                    tempListApplicant.add(applicant)
                }
            }
            applicantAdapter.setData(tempListApplicant)
        }

        binding.applicantCount.setOnClickListener {
            applicantAdapter.setData(listApplicant)
        }
    }

    companion object {
        const val ID_CAMPAIGN = "id_campaign"
    }

    override fun onApplicantClick(applicant: Applicant) {
        val applicantDetailIntent = Intent(this, DetailApplicantActivity::class.java)
        applicantDetailIntent.putExtra(DetailApplicantActivity.ID_APPLICANT, applicant.id)
        startActivity(applicantDetailIntent)
    }
}