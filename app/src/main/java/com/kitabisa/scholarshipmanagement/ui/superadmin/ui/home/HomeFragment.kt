package com.kitabisa.scholarshipmanagement.ui.superadmin.ui.home

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.kitabisa.scholarshipmanagement.data.Campaign
import com.kitabisa.scholarshipmanagement.data.Resource
import com.kitabisa.scholarshipmanagement.databinding.FragmentHomeBinding
import com.kitabisa.scholarshipmanagement.ui.CustomLoadingDialog
import com.kitabisa.scholarshipmanagement.ui.DataViewModelFactory
import com.kitabisa.scholarshipmanagement.ui.detailcampaign.DetailCampaignActivity
import com.kitabisa.scholarshipmanagement.ui.login.LoginActivity
import com.kitabisa.scholarshipmanagement.ui.superadmin.AdminCampaignAdapter
import com.kitabisa.scholarshipmanagement.ui.superadmin.AdminCampaignViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private lateinit var customLoadingDialog: CustomLoadingDialog
    private var listCampaign = ArrayList<Campaign>()


    private val binding get() = _binding!!
    private var tempToken: String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        customLoadingDialog = CustomLoadingDialog(requireContext())
        renderLoading(true)
        setupLogoutFunc(binding.logout)
        val factory: DataViewModelFactory = DataViewModelFactory.getInstance()

        val adminViewModel: AdminCampaignViewModel by viewModels {
            factory
        }

        val firebaseUser = auth.currentUser

        firebaseUser?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                tempToken = task.result.token.toString()
                adminViewModel.getCampaign(tempToken).observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        when (result) {
                            is Resource.Success -> {
                                if(result.data != null) {
                                    listCampaign = result.data!!.listCampaign
                                    //campaignAdapter.setData(listCampaign)
                                    setDataAdapter(listCampaign)
                                    renderLoading(false)
                                    binding.root.visibility = View.VISIBLE
                                }else{
                                    Toast.makeText(requireContext(), "Failed to get data", Toast.LENGTH_SHORT).show()
                                    renderLoading(false)
                                    binding.root.visibility = View.VISIBLE
                                }
                            }
                            is Resource.Error -> {
                                renderLoading(false)
                                Toast.makeText(
                                    requireContext(),
                                    result.data?.error.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is Resource.Loading -> {

                            }
                        }
                    }
                }
            }
        }?.addOnFailureListener {
            renderLoading(false)
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            signOut()

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setupLogoutFunc(id: ImageView) {
        id.setOnClickListener {
            signOut()
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

    private fun renderLoading(state: Boolean) {
        if (state) {
            customLoadingDialog.show()
            customLoadingDialog.setCancelable(false)
        } else {
            customLoadingDialog.dismiss()
        }
    }

    private fun setDataAdapter(data: ArrayList<Campaign>) {
        if (context?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.rvCampaign.layoutManager = GridLayoutManager(requireContext(), 2)
        } else {
            binding.rvCampaign.layoutManager = LinearLayoutManager(requireContext())
        }

        val listCampaignAdapter = AdminCampaignAdapter(data)
        binding.rvCampaign.adapter = listCampaignAdapter
        listCampaignAdapter.setOnItemClickCallback(object :
            AdminCampaignAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Campaign) {
                val campaignDetailIntent = Intent(requireContext(), DetailCampaignActivity::class.java)
                campaignDetailIntent.putExtra(DetailCampaignActivity.ID_CAMPAIGN, data.id)
                startActivity(campaignDetailIntent)
            }
        })
    }
}