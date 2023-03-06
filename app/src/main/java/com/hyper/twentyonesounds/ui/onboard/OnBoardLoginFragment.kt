package com.hyper.twentyonesounds.ui.onboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hyper.twentyonesounds.databinding.FragmentOnboardLoginBinding
import com.hyper.twentyonesounds.ui.main.MainActivity

class OnBoardLogin : Fragment() {

    private var _binding : FragmentOnboardLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, group: ViewGroup?, saved: Bundle?
    ): View {
        _binding = FragmentOnboardLoginBinding.inflate(inflater, group, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSkipLogin.setOnClickListener {
            val intent = Intent(requireContext(),  MainActivity::class.java)
            activity?.finish()
            startActivity(intent)
        }
    }
}