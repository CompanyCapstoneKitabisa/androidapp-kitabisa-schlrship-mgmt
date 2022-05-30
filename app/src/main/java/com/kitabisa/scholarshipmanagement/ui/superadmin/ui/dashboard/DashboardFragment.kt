package com.kitabisa.scholarshipmanagement.ui.superadmin.ui.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.kitabisa.scholarshipmanagement.data.NewCampaignBody
import com.kitabisa.scholarshipmanagement.data.Resource
import com.kitabisa.scholarshipmanagement.databinding.FragmentDashboardBinding
import com.kitabisa.scholarshipmanagement.ui.CustomLoadingDialog
import com.kitabisa.scholarshipmanagement.ui.DataViewModelFactory
import com.kitabisa.scholarshipmanagement.ui.superadmin.AdminCampaignViewModel
import com.kitabisa.scholarshipmanagement.utils.Utils.uriToFile
import java.io.File
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    // This property is only valid between onCreateView and
    // onDestroyView.

    private lateinit var customLoadingDialog: CustomLoadingDialog
    private var tempToken: String = ""
    private val binding get() = _binding!!
    private var getFile: File? = null
    private lateinit var selectedImg: Uri

    val storage = Firebase.storage
    val storageRef = storage.reference
    private lateinit var uploadTask: UploadTask


    val factory: DataViewModelFactory = DataViewModelFactory.getInstance()

    val adminViewModel: AdminCampaignViewModel by viewModels {
        factory
    }

    val firebaseUser = auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        customLoadingDialog = CustomLoadingDialog(requireContext())

        val root: View = binding.root
        binding.btnPicture?.setOnClickListener{ startGallery() }

        binding.btnSubmit.setOnClickListener { postCampaign() }

        return root
    }


    private fun postCampaign(){

        var noError = true

        val namaCampaign = binding.nameCampaignInput.editText?.text.toString()
        if(namaCampaign.isNullOrEmpty()){
            binding.nameCampaignInput.error = "email cannot be empty"
            noError = false
        }else{
            binding.nameCampaignInput.isErrorEnabled = false
        }

        val namaPenggalang = binding.namePenggalangInput.editText?.text.toString()
        if(namaPenggalang.isNullOrEmpty()){
            binding.namePenggalangInput.error = "nama penggalang cannot be empty"
            noError = true

        }else{
            binding.namePenggalangInput.isErrorEnabled = false
        }

        val snk = binding.snkCampaignInput?.editText?.text.toString()
        if(snk.isNullOrEmpty()){
            binding.snkCampaignInput?.error = "SnK cannot be empty"
            noError = true

        }else{
            binding.snkCampaignInput?.isErrorEnabled = false
        }

        val idGsheet = binding.urlInput.editText?.text.toString()
        if(idGsheet.isNullOrEmpty()){
            binding.urlInput.error = "Google ID cannot be empty"
            noError = true
        }else{
            binding.urlInput.isErrorEnabled = false
        }

        var imgLink: String? = null

        if(getFile != null){
            var file = Uri.fromFile(getFile)
            val imgRef = storageRef.child("Campaigns/${UUID.randomUUID()}.png")
            uploadTask = imgRef.putFile(file)
            uploadTask.addOnFailureListener{
                Toast.makeText(requireContext(), "IT FAILED ${it.message}", Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener { it ->
                Toast.makeText(requireContext(), "it is success", Toast.LENGTH_SHORT).show()
                it.storage.downloadUrl.addOnCompleteListener{ res ->
                    Log.d("IMG", res.result.toString())
                    imgLink = res.result.toString()
                    val campaign = NewCampaignBody(namaCampaign, namaPenggalang, snk, idGsheet, imgLink!!)

                    makeCampaign(campaign)
                }
            }
        }else{
            Toast.makeText(requireContext(), "please select a picture", Toast.LENGTH_SHORT).show()
            noError = true
        }

        if(noError){
            Log.d("body", imgLink.toString())

        }

    }

    fun makeCampaign(body: NewCampaignBody){

        firebaseUser?.getIdToken(true)?.addOnCompleteListener{task ->
            if(task.isSuccessful){
                tempToken = task.result.token.toString()
                Log.d("CAMPAIGNTOKEN", tempToken)
                adminViewModel.addCampaign(tempToken, body!!).observe(viewLifecycleOwner) { result ->
                    if(result != null){
                        when(result){
                            is Resource.Success -> {
                                Toast.makeText(requireContext(), "Campaign Added", Toast.LENGTH_SHORT).show()
                            }
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), "Campaign Failed", Toast.LENGTH_SHORT).show()
                            }
                            is Resource.Loading -> {
                                Toast.makeText(requireContext(), "Campaign Loading", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }?.addOnFailureListener{
            Toast.makeText(requireContext(), "Failed to make API CALL", Toast.LENGTH_SHORT).show()

        }
    }


    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            selectedImg = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireContext())
            getFile = myFile
            Toast.makeText(requireContext(), selectedImg.toString(), Toast.LENGTH_SHORT).show()
            binding.imgCampaign?.setImageURI(selectedImg)
        }
    }

        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}