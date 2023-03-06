package com.hyper.twentyonesounds.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hyper.twentyonesounds.R
import com.hyper.twentyonesounds.databinding.FragmentInternetErrorBinding
import com.hyper.twentyonesounds.ui.main.MainActivity

class InternetErrorFragment : Fragment(R.layout.fragment_internet_error) {

    private var _binding : FragmentInternetErrorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, group: ViewGroup?, saved: Bundle?): View {
        _binding = FragmentInternetErrorBinding.inflate(inflater, group, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnTryAgain.setOnClickListener {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            requireActivity().finish()
            requireActivity().startActivity(intent)
        }
    }
}