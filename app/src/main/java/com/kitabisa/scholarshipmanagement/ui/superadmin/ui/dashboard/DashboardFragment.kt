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
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.kitabisa.scholarshipmanagement.R
import com.kitabisa.scholarshipmanagement.data.NewCampaignBody
import com.kitabisa.scholarshipmanagement.data.Resource
import com.kitabisa.scholarshipmanagement.databinding.FragmentDashboardBinding
import com.kitabisa.scholarshipmanagement.ui.CustomLoadingDialog
import com.kitabisa.scholarshipmanagement.ui.DataViewModelFactory
import com.kitabisa.scholarshipmanagement.ui.login.LoginActivity
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

    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private lateinit var uploadTask: UploadTask


    private val factory: DataViewModelFactory = DataViewModelFactory.getInstance()

    private val adminViewModel: AdminCampaignViewModel by viewModels {
        factory
    }

    private val firebaseUser = auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        customLoadingDialog = CustomLoadingDialog(requireActivity())

        val root: View = binding.root
        binding.btnPicture.setOnClickListener { startGallery() }

        binding.btnSubmit.setOnClickListener { postCampaign() }

        return root
    }


    private fun postCampaign() {

        var noError = true

        val namaCampaign = binding.nameCampaignInput.editText?.text.toString()
        if (namaCampaign.isEmpty()) {
            binding.nameCampaignInput.error = "Nama Campaign cannot be empty"
            noError = false
        } else {
            binding.nameCampaignInput.isErrorEnabled = false
        }

        val namaPenggalang = binding.namePenggalangInput.editText?.text.toString()
        if (namaPenggalang.isEmpty()) {
            binding.namePenggalangInput.error = "Nama penggalang cannot be empty"
            noError = false

        } else {
            binding.namePenggalangInput.isErrorEnabled = false
        }

        val snk = binding.snkCampaignInput.editText?.text.toString()
        if (snk.isEmpty()) {
            binding.snkCampaignInput.error = "SnK cannot be empty"
            noError = false

        } else {
            binding.snkCampaignInput.isErrorEnabled = false
        }

        val idGsheet = binding.urlInput.editText?.text.toString()
        if (idGsheet.isEmpty()) {
            binding.urlInput.error = "ID GSheet cannot be empty"
            noError = false
        } else {
            binding.urlInput.isErrorEnabled = false
        }

        var imgLink: String?

        if (getFile != null) {
            if (noError) {
                renderLoading(true)

                val file = Uri.fromFile(getFile)
                val imgRef = storageRef.child("Campaigns/${UUID.randomUUID()}.png")
                uploadTask = imgRef.putFile(file)
                uploadTask.addOnFailureListener {
                    Toast.makeText(requireActivity(), "Error : ${it.message}", Toast.LENGTH_SHORT)
                        .show()
                    renderLoading(false)

                }.addOnSuccessListener {
                    Toast.makeText(
                        requireActivity(),
                        "Uploaded to Firebase Storage",
                        Toast.LENGTH_SHORT
                    ).show()
                    it.storage.downloadUrl.addOnCompleteListener { res ->
                        Log.d("IMG", res.result.toString())
                        imgLink = res.result.toString()
                        val campaign =
                            NewCampaignBody(namaCampaign, namaPenggalang, snk, idGsheet, imgLink!!)

                        makeCampaign(campaign)
                    }
                }
            }
        } else {
            Toast.makeText(requireActivity(), "Please select a picture", Toast.LENGTH_SHORT).show()
        }

    }

    private fun makeCampaign(body: NewCampaignBody) {

        firebaseUser?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                tempToken = task.result.token.toString()
                adminViewModel.addCampaign(tempToken, body).observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        when (result) {
                            is Resource.Success -> {
                                renderLoading(false)
                                findNavController().navigate(R.id.navigation_home)
                            }
                            is Resource.Error -> {
                                renderLoading(false)
                                Toast.makeText(
                                    requireActivity(),
                                    result.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                signOut()
                            }
                            is Resource.Loading -> {
                                renderLoading(true)
                            }
                        }
                    }
                }
            }
        }?.addOnFailureListener {
            renderLoading(false)
            Toast.makeText(requireActivity(), it.message.toString(), Toast.LENGTH_SHORT).show()
            signOut()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            binding.imgCampaign.setImageURI(selectedImg)
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

    private fun signOut() {
        GoogleSignIn.getClient(
            requireContext(),
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut()
        auth.signOut()
        requireActivity().run {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}