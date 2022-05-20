package com.kitabisa.scholarshipmanagement.ui.detailapplicant

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.kitabisa.scholarshipmanagement.R
import com.kitabisa.scholarshipmanagement.data.FetchedData
import com.kitabisa.scholarshipmanagement.data.Result
import com.kitabisa.scholarshipmanagement.databinding.ActivityDetailApplicantBinding
import com.kitabisa.scholarshipmanagement.ui.CustomLoadingDialog
import com.kitabisa.scholarshipmanagement.ui.DataViewModelFactory

class DetailApplicantActivity : AppCompatActivity() {

    private lateinit var activityDetailApplicantBinding: ActivityDetailApplicantBinding
    private lateinit var detailApplicantViewModel: DetailApplicantViewModel
    private lateinit var customLoadingDialog: CustomLoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDetailApplicantBinding = ActivityDetailApplicantBinding.inflate(layoutInflater)
        setContentView(activityDetailApplicantBinding.root)
        supportActionBar?.hide()

        detailApplicantViewModel = obtainViewModel(this)
        detailApplicantViewModel.getDetailApplicant("KWoaqHDcweHL8X3ez5wn")

        customLoadingDialog = CustomLoadingDialog(this)

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
    }

    private fun obtainViewModel(activity: AppCompatActivity): DetailApplicantViewModel {
        val factory = DataViewModelFactory.getInstance()
        return ViewModelProvider(activity, factory).get(DetailApplicantViewModel::class.java)
    }

    private fun renderData(data: FetchedData) {

        Glide.with(this)
            .load(data.photo)
            .into(activityDetailApplicantBinding.ivProfile)

        activityDetailApplicantBinding.apply {
            tvNama.text = data.name
            tvUniversitas.text = data.university
            tvJurusanAngkatan.text = data.jurusan.plus(" - ${data.angkatan}")
            tvNim.text = data.NIM

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
            Glide.with(applicationContext)
                .asBitmap()
                .load(data.fotoRumah)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        ivFotoRumah.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })


            headerFotoRumah.headerIcon.setOnClickListener {
                expand(ivFotoRumah, headerFotoRumah.headerIcon)
            }

            cardviewBuktiIpkIp.visibility = View.GONE
            cardviewKegiatanAktif.visibility = View.GONE

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
}