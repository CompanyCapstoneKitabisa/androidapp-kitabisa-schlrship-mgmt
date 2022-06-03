package com.kitabisa.scholarshipmanagement.ui.superadmin.ui.home

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import com.kitabisa.scholarshipmanagement.ui.login.LoginActivity
import com.kitabisa.scholarshipmanagement.ui.superadmin.AdminCampaignAdapter
import com.kitabisa.scholarshipmanagement.ui.superadmin.AdminCampaignViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private lateinit var adminViewModel: AdminCampaignViewModel
    private lateinit var customLoadingDialog: CustomLoadingDialog
    private var listCampaign = ArrayList<Campaign>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var tempToken: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        customLoadingDialog = CustomLoadingDialog(requireContext())
        setupLogoutFunc(binding.logout)
        adminViewModel = obtainViewModel(requireActivity() as AppCompatActivity)
        renderLoading(true)
        binding.root.visibility = View.GONE

        val firebaseUser = auth.currentUser
        firebaseUser?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                tempToken = task.result.token.toString()
                adminViewModel.getCampaign(tempToken).observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        when (result) {
                            is Resource.Success -> {
                                if (result.data?.listCampaign != null) {
                                    listCampaign = result.data.listCampaign
                                    setDataAdapter(listCampaign)
                                    renderLoading(false)
                                    binding.root.visibility = View.VISIBLE
                                    Toast.makeText(
                                        requireActivity(), result.data.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    renderLoading(false)
                                    binding.tvDataNull.visibility = View.VISIBLE
                                    binding.root.visibility = View.VISIBLE
                                }
                            }
                            is Resource.Error -> {
                                Toast.makeText(
                                    requireActivity(),
                                    result.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                signOut()
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
            Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            signOut()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupLogoutFunc(id: ImageView) {
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

            override fun onProccessClicked(data: Campaign) {
                if (tempToken.isNotEmpty()) {
                    adminViewModel.triggerDataProcess(tempToken, data.id)
                        .observe(viewLifecycleOwner) { result ->
                            if (result != null) {
                                when (result) {
                                    is Resource.Success -> {
                                        renderLoading(false)
                                        Toast.makeText(
                                            requireActivity(), result.data?.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        triggerPagingFunction(tempToken, data.id)
                                    }
                                    is Resource.Error -> {
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
            }
        })
    }

    private fun obtainViewModel(activity: AppCompatActivity): AdminCampaignViewModel {
        val factory = DataViewModelFactory.getInstance()
        return ViewModelProvider(activity, factory).get(AdminCampaignViewModel::class.java)
    }

    private fun triggerPagingFunction(token: String, id: String) {
        adminViewModel.triggerPagingData(token, id)
            .observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is Resource.Success -> {
                            renderLoading(false)
                            Toast.makeText(
                                requireActivity(), result.data?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is Resource.Error -> {
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
}