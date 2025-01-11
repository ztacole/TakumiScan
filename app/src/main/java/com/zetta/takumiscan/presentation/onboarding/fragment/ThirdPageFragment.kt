package com.zetta.takumiscan.presentation.onboarding.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zetta.takumiscan.databinding.FragmentThirdPageBinding
import com.zetta.takumiscan.presentation.auth.RegisterActivity
import com.zetta.takumiscan.data.local.CacheController

class ThirdPageFragment : Fragment() {
    private lateinit var binding: FragmentThirdPageBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentThirdPageBinding.inflate(layoutInflater)

        binding.btnStart.setOnClickListener {
            Intent(requireActivity(), RegisterActivity::class.java).also {
                CacheController(requireContext()).setStatus("Not Registered")
                startActivity(it)
                requireActivity().finish()
            }
        }

        return binding.root
    }
}