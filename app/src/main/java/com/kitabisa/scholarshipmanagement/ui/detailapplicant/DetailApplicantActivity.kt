package com.kitabisa.scholarshipmanagement.ui.detailapplicant

import android.Manifest
import android.app.Dialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.auth.FirebaseAuth
import com.jsibbold.zoomage.ZoomageView
import com.kitabisa.scholarshipmanagement.R
import com.kitabisa.scholarshipmanagement.data.FetchedData
import com.kitabisa.scholarshipmanagement.data.Resource
import com.kitabisa.scholarshipmanagement.data.UpdateApplicantStatusBody
import com.kitabisa.scholarshipmanagement.databinding.ActivityDetailApplicantBinding
import com.kitabisa.scholarshipmanagement.databinding.DialogDataLainnyaPesertaBinding
import com.kitabisa.scholarshipmanagement.ui.CustomLoadingDialog
import com.kitabisa.scholarshipmanagement.ui.DataViewModelFactory
import com.kitabisa.scholarshipmanagement.ui.detailcampaign.DetailCampaignActivity
import com.kitabisa.scholarshipmanagement.utils.Utils.loadImage
import java.io.File

class DetailApplicantActivity : AppCompatActivity() {

    private lateinit var activityDetailApplicantBinding: ActivityDetailApplicantBinding
    private lateinit var detailApplicantViewModel: DetailApplicantViewModel
    private lateinit var customLoadingDialog: CustomLoadingDialog
    private lateinit var moreDataDialog: Dialog
    private lateinit var dialogDataLainnyaPesertaBinding: DialogDataLainnyaPesertaBinding
    private lateinit var broadcastReceiver: BroadcastReceiver

    private var tempToken: String = ""
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

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
        activityDetailApplicantBinding = ActivityDetailApplicantBinding.inflate(layoutInflater)
        setContentView(activityDetailApplicantBinding.root)
        supportActionBar?.hide()

        detailApplicantViewModel = obtainViewModel(this)
        customLoadingDialog = CustomLoadingDialog(this)
        moreDataDialog = Dialog(this)

        val idApplicant = intent.getStringExtra(ID_APPLICANT).toString()
        val idCampaign = intent.getStringExtra(DetailCampaignActivity.ID_CAMPAIGN).toString()

        activityDetailApplicantBinding.root.visibility = View.GONE
        val firebaseUser = auth.currentUser

        renderLoading(true)

        firebaseUser?.getIdToken(true)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                renderLoading(false)
                tempToken = task.result.token.toString()
                detailApplicantViewModel.getDetailApplicant(
                    tempToken,
                    idApplicant
                ).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Resource.Loading -> {
                                renderLoading(true)
                                activityDetailApplicantBinding.root.visibility = View.GONE
                            }
                            is Resource.Success -> {
                                if (result.data?.fetchedData != null) {
                                    renderData(result.data.fetchedData)
                                    renderLoading(false)
                                    activityDetailApplicantBinding.root.visibility = View.VISIBLE
                                } else {
                                    renderLoading(false)
                                    startActivity(
                                        Intent(
                                            this@DetailApplicantActivity,
                                            DetailCampaignActivity::class.java
                                        )
                                    )
                                    finish()
                                }
                            }
                            is Resource.Error -> {
                                finish()
                                Toast.makeText(
                                    this,
                                    result.data?.error.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }?.addOnFailureListener {
            renderLoading(false)
            startActivity(Intent(this@DetailApplicantActivity, DetailCampaignActivity::class.java))
            finishAffinity()
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }


        activityDetailApplicantBinding.btnSubmit.setOnClickListener {
            val tempStatus =
                when ((activityDetailApplicantBinding.pilihanMenu.editText as? AutoCompleteTextView)?.text.toString()) {
                    "Accept" -> "accepted"
                    "Reject" -> "rejected"
                    "Hold" -> "onHold"
                    else -> ""
                }

            val tempReviewer = firebaseUser?.email

            val tempNotes =
                activityDetailApplicantBinding.etCatatanReviewer.editText?.text.toString()

            val body = UpdateApplicantStatusBody(tempReviewer.toString(), tempStatus, tempNotes)

            detailApplicantViewModel.setApplicantStatus(
                tempToken, idApplicant,
                body
            ).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Resource.Loading -> {
                            renderLoading(true)
                        }
                        is Resource.Success -> {
                            renderLoading(false)

                            val campaignDetailIntent = Intent(this@DetailApplicantActivity, DetailCampaignActivity::class.java)
                            campaignDetailIntent.putExtra(DetailCampaignActivity.ID_CAMPAIGN, idCampaign)
                            startActivity(campaignDetailIntent)
                            finish()
                            Toast.makeText(
                                this,
                                result.data?.message?.statusApplicant.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is Resource.Error -> {
                            renderLoading(false)
                            Toast.makeText(
                                this,
                                result.data?.error.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context, p1: Intent) {
                Toast.makeText(
                    p0,
                    "Download Complete",
                    Toast.LENGTH_SHORT
                ).show()
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

    private fun obtainViewModel(activity: AppCompatActivity): DetailApplicantViewModel {
        val factory = DataViewModelFactory.getInstance()
        return ViewModelProvider(activity, factory).get(DetailApplicantViewModel::class.java)
    }

    private fun renderData(data: FetchedData) {
        activityDetailApplicantBinding.apply {
            ivProfile.loadImage(data.bioDiri.fotoDiri, R.drawable.profile_icon)

            tvNama.text = data.bioDiri.nama
            tvUniversitas.text =
                data.bioPendidikan.tingkatPendidikan.plus(" - ${data.bioPendidikan.NPSN}")
            tvJurusanKelas.text = data.bioPendidikan.jurusan
            tvNim.text = data.bioPendidikan.NIM

            btnLainnya.setOnClickListener { renderDialog(data) }

            headerCeritaLatarBelakang.headerText.text = "Cerita Latar Belakang"
            headerCeritaLatarBelakang.headerIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            tvCeritaLatarBelakang.text = data.motivationLetter.ceritaLatarBelakang
            headerCeritaLatarBelakang.headerIcon.setOnClickListener {
                expand(tvCeritaLatarBelakang, headerCeritaLatarBelakang.headerIcon)
            }

            headerCeritaPerjuangan.headerText.text = "Cerita Memperjuangkan Pendidikan"
            headerCeritaPerjuangan.headerIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            tvCeritaPerjuangan.text = data.motivationLetter.ceritaPerjuangan
            headerCeritaPerjuangan.headerIcon.setOnClickListener {
                expand(tvCeritaPerjuangan, headerCeritaPerjuangan.headerIcon)
            }

            headerCeritaPentingBeasiswa.headerText.text = "Seberapa Penting Beasiswa Ini"
            headerCeritaPentingBeasiswa.headerIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            tvCeritaBeasiswa.text = data.motivationLetter.ceritaPentingnyaBeasiswa
            headerCeritaPentingBeasiswa.headerIcon.setOnClickListener {
                expand(tvCeritaBeasiswa, headerCeritaPentingBeasiswa.headerIcon)
            }

            headerRincianBeasiswa.headerText.text = "Rincian Beasiswa"
            headerRincianBeasiswa.headerIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            rincianBeasiswa.tvPilihanBantuan.text = data.pengajuanBantuan.kebutuhan
            rincianBeasiswa.tvTotalBiaya.text = data.pengajuanBantuan.totalBiaya
            rincianBeasiswa.tvRincianBiaya.text = data.pengajuanBantuan.ceritaPenggunaanDana
            renderZoomImage(
                data.pengajuanBantuan.fotoBuktiTunggakan,
                rincianBeasiswa.ivFotoBuktiTagihan
            )
            headerRincianBeasiswa.headerIcon.setOnClickListener {
                expand(rincianBeasiswa.root, headerRincianBeasiswa.headerIcon)
            }

            headerFotoRumah.headerText.text = "Foto Rumah"
            headerFotoRumah.headerIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            renderZoomImage(data.pengajuanBantuan.fotoRumah, ivFotoRumah)
            headerFotoRumah.headerIcon.setOnClickListener {
                expand(ivFotoRumah, headerFotoRumah.headerIcon)
            }

            headerBuktiIpkRapot.headerText.text = "Bukti IPK / Rapot"
            headerBuktiIpkRapot.headerIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            renderZoomImage(data.bioPendidikan.fotoIPKAtauRapor, ivBuktiIpk)
            headerBuktiIpkRapot.headerIcon.setOnClickListener {
                if (ivBuktiIpk.visibility == View.GONE) {
                    ivBuktiIpk.visibility = View.VISIBLE
                    headerBuktiIpkRapot.headerIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                } else {
                    ivBuktiIpk.visibility = View.GONE
                    headerBuktiIpkRapot.headerIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                }
            }

            headerKegiatanAktif.headerText.text = "Kegiatan Aktif"
            headerKegiatanAktif.headerIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            renderZoomImage(data.motivationLetter.fotoBuktiKegiatan, ivFotoKegiatan)
            tvCeritaKegiatan.text = data.motivationLetter.ceritakegiatanYangDigeluti
            headerKegiatanAktif.headerIcon.setOnClickListener {
                if (ivFotoKegiatan.visibility == View.GONE && tvCeritaKegiatan.visibility == View.GONE) {
                    ivFotoKegiatan.visibility = View.VISIBLE
                    tvCeritaKegiatan.visibility = View.VISIBLE
                    headerKegiatanAktif.headerIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                } else {
                    ivFotoKegiatan.visibility = View.GONE
                    tvCeritaKegiatan.visibility = View.GONE
                    headerKegiatanAktif.headerIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                }
            }

            val items = listOf("No Status", "Accept", "Reject", "Hold")
            if (data.statusApplicant.isEmpty()) {
                setStatusValue(pilihanMenu.editText, items, 0)
            } else {
                when (data.statusApplicant) {
                    "accepted" -> {
                        setStatusValue(pilihanMenu.editText, items, 1)
                    }
                    "rejected" -> {
                        setStatusValue(pilihanMenu.editText, items, 2)
                    }
                    "onHold" -> {
                        setStatusValue(pilihanMenu.editText, items, 3)
                    }
                }
            }

            val adapter = ArrayAdapter(this@DetailApplicantActivity, R.layout.list_pilihan, items)
            (pilihanMenu.editText as? AutoCompleteTextView)?.setAdapter(adapter)

            if (data.notes.isNotEmpty()) {
                etCatatanReviewer.editText?.text =
                    Editable.Factory.getInstance().newEditable(data.notes)
            }
        }

        activityDetailApplicantBinding.tvLampiranDokumen.setOnClickListener {
            if (!isPermissionsGranted()) {
                ActivityCompat.requestPermissions(
                    this@DetailApplicantActivity,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            } else {
                downloadDocuments(data)
            }
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

    private fun expand(view: View, imageView: ImageView) {
        val arrowUp = R.drawable.ic_baseline_keyboard_arrow_up_24
        val arrowDown = R.drawable.ic_baseline_keyboard_arrow_down_24
        if (view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
            imageView.setImageResource(arrowUp)
        } else {
            view.visibility = View.GONE
            imageView.setImageResource(arrowDown)
        }
    }

    private fun renderZoomImage(imageURL: String, zoomageView: ZoomageView) {
        Glide.with(applicationContext)
            .asBitmap()
            .load(imageURL)
            .placeholder(R.drawable.ic_image)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    zoomageView.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    zoomageView.setImageDrawable(placeholder)
                }
            })
    }

    private fun renderDialog(data: FetchedData) {
        dialogDataLainnyaPesertaBinding = DialogDataLainnyaPesertaBinding.inflate(layoutInflater)
        moreDataDialog.setContentView(dialogDataLainnyaPesertaBinding.root)
        moreDataDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogDataLainnyaPesertaBinding.apply {
            tvNama.text = data.bioDiri.nama
            tvNpsn.text = data.bioPendidikan.NPSN
            tvJurusanKelas.text = data.bioPendidikan.jurusan
            tvNim.text = data.bioPendidikan.NIM
            tvNik.text = data.bioDiri.NIK
            tvNomorTelepon.text = data.bioDiri.noTlp
            tvSosialMedia.text = data.bioDiri.sosmedAcc
            tvAlamat.text =
                data.bioDiri.alamat.plus(", ${data.bioDiri.kotaKabupaten}, ${data.bioDiri.provinsi}")

            Glide.with(this@DetailApplicantActivity)
                .load(data.bioDiri.fotoDiri)
                .placeholder(R.drawable.profile_icon)
                .into(ivProfile)

            btnOk.setOnClickListener { moreDataDialog.dismiss() }
        }

        moreDataDialog.show()
    }

    private fun downloadDocuments(data: FetchedData) {
        try {
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val documentsLink = Uri.parse(data.lampiranTambahan)
            val request = DownloadManager.Request(documentsLink)
            val tempName = data.bioDiri.nama.plus(" LampiranDokumen")
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                .setMimeType("application/pdf")
                .setAllowedOverRoaming(false)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(tempName)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    File.separator + tempName + ".pdf"
                )
            downloadManager.enqueue(request)
        } catch (e: Exception) {
            Toast.makeText(this, "Download Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setStatusValue(et: EditText?, items: List<String>, index: Int) {
        (et as? AutoCompleteTextView)?.text =
            Editable.Factory.getInstance().newEditable(items[index])
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private const val REQUEST_CODE_PERMISSIONS = 10
        const val ID_APPLICANT = "id_applicant"
    }
}