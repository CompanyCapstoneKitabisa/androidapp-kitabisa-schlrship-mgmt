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
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
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
import com.kitabisa.scholarshipmanagement.data.Result
import com.kitabisa.scholarshipmanagement.databinding.ActivityDetailApplicantBinding
import com.kitabisa.scholarshipmanagement.databinding.DialogDataLainnyaPesertaBinding
import com.kitabisa.scholarshipmanagement.ui.CustomLoadingDialog
import com.kitabisa.scholarshipmanagement.ui.DataViewModelFactory
import java.io.File

class DetailApplicantActivity : AppCompatActivity() {

    private lateinit var activityDetailApplicantBinding: ActivityDetailApplicantBinding
    private lateinit var detailApplicantViewModel: DetailApplicantViewModel
    private lateinit var customLoadingDialog: CustomLoadingDialog
    private lateinit var moreDataDialog: Dialog
    private lateinit var dialogDataLainnyaPesertaBinding: DialogDataLainnyaPesertaBinding

    private lateinit var broadcastReceiver: BroadcastReceiver

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
        activityDetailApplicantBinding.root.visibility = View.GONE
        val firebaseUser = auth.currentUser

        firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
            detailApplicantViewModel.getDetailApplicant(
                res.token.toString(),
                "KWoaqHDcweHL8X3ez5wn"
            )
        }

        detailApplicantViewModel = obtainViewModel(this)

        customLoadingDialog = CustomLoadingDialog(this)
        moreDataDialog = Dialog(this)

        detailApplicantViewModel.fetchedData.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    renderLoading(true)
                    activityDetailApplicantBinding.root.visibility = View.GONE
                }
                is Result.Success -> {
                    renderData(result.data)
                    renderLoading(false)
                    activityDetailApplicantBinding.root.visibility = View.VISIBLE
                }
                is Result.Error -> {
                    finish()
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
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

        Glide.with(this@DetailApplicantActivity)
            .load(data.photo)
            .into(activityDetailApplicantBinding.ivProfile)

        activityDetailApplicantBinding.apply {
            tvNama.text = data.name
            tvUniversitas.text = data.university
            tvJurusanAngkatan.text = data.jurusan.plus(" - ${data.angkatan}")
            tvNim.text = data.NIM

            btnLainnya.setOnClickListener { renderDialog(data) }

            headerCeritaPerjuangan.headerText.text = "Cerita Kondisi Perjuangan"
            headerCeritaPerjuangan.headerIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            tvCeritaPerjuangan.text = data.ceritaKondisi
            headerCeritaPerjuangan.headerIcon.setOnClickListener {
                expand(tvCeritaPerjuangan, headerCeritaPerjuangan.headerIcon)
            }

            headerCeritaPentingBeasiswa.headerText.text = "Seberapa Penting Beasiswa Ini"
            headerCeritaPentingBeasiswa.headerIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            tvCeritaBeasiswa.text = data.ceritaSeberapaPenting
            headerCeritaPentingBeasiswa.headerIcon.setOnClickListener {
                expand(tvCeritaBeasiswa, headerCeritaPentingBeasiswa.headerIcon)
            }

            headerRincianBeasiswa.headerText.text = "Rincian Beasiswa"
            headerRincianBeasiswa.headerIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            headerRincianBeasiswa.headerIcon.setOnClickListener {
                if (data.pilihanBantuanBiaya.contains("pendidikan")) {
                    beasiswaPendidikan.tvPilihanBantuan.text = data.pilihanBantuanBiaya
                    beasiswaPendidikan.tvJumlahBiaya.text = data.jumlahBiayaUKT
                    beasiswaPendidikan.tvDeadlinePembayaran.text = data.deadlinePembayaran
                    expand(beasiswaPendidikan.root, headerRincianBeasiswa.headerIcon)
                } else if (data.pilihanBantuanBiaya.contains("penunjang")) {
                    beasiswaPenunjang.tvPilihanBantuan.text = data.pilihanBantuanBiaya
                    /* BELOM SELESAI */
                }
            }

            headerFotoRumah.headerText.text = "Foto Rumah"
            headerFotoRumah.headerIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            renderImage(data.fotoRumah, ivFotoRumah)
            headerFotoRumah.headerIcon.setOnClickListener {
                expand(ivFotoRumah, headerFotoRumah.headerIcon)
            }

            headerBuktiIpkIp.headerText.text = "Bukti IPK & IP"
            headerBuktiIpkIp.headerIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            renderImage(data.buktiIPK, ivBuktiIpk)
            renderImage(data.buktiIP, ivBuktiIp)
            headerBuktiIpkIp.headerIcon.setOnClickListener {
                if (ivBuktiIpk.visibility == View.GONE && ivBuktiIp.visibility == View.GONE) {
                    ivBuktiIpk.visibility = View.VISIBLE
                    ivBuktiIp.visibility = View.VISIBLE
                    headerBuktiIpkIp.headerIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                } else {
                    ivBuktiIpk.visibility = View.GONE
                    ivBuktiIp.visibility = View.GONE
                    headerBuktiIpkIp.headerIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                }
            }

            headerKegiatanAktif.headerText.text = "Kegiatan Aktif"
            headerKegiatanAktif.headerIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            renderImage(data.fotoKegiatan, ivFotoKegiatan)
            tvCeritaKegiatan.text = data.ceritaKegiatan
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
            val adapter = ArrayAdapter(this@DetailApplicantActivity, R.layout.list_pilihan, items)
            (pilihanMenu.editText as? AutoCompleteTextView)?.setAdapter(adapter)

            tvLampiranDokumen.setOnClickListener {
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

    private fun renderImage(imageURL: String, zoomageView: ZoomageView) {
        Glide.with(applicationContext)
            .asBitmap()
            .load(imageURL)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    zoomageView.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun renderDialog(data: FetchedData) {
        dialogDataLainnyaPesertaBinding = DialogDataLainnyaPesertaBinding.inflate(layoutInflater)
        moreDataDialog.setContentView(dialogDataLainnyaPesertaBinding.root)
        moreDataDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogDataLainnyaPesertaBinding.apply {
            tvNama.text = data.name
            tvTempatKuliah.text = data.university
            tvJurusanAngkatan.text = data.jurusan.plus(" ${data.angkatan}")
            tvNim.text = data.NIM
            tvNik.text = data.NIK
            tvNomorTelepon.text = data.noPonsel
            tvSosialMedia.text = data.sosmedAcc
            tvAlamat.text =
                data.alamat.plus(", ${data.kelurahan}, ${data.kecamatan}, ${data.kotaKabupaten}, ${data.provinsi}")

            Glide.with(this@DetailApplicantActivity)
                .load(data.photo)
                .into(ivProfile)

            btnOk.setOnClickListener { moreDataDialog.dismiss() }
        }

        moreDataDialog.show()
    }

    private fun downloadDocuments(data: FetchedData) {
        try {
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val documentsLink = Uri.parse(data.lampiranDokumen)
            val request = DownloadManager.Request(documentsLink)
            val tempName = data.name.plus(" LampiranDokumen")
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

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}