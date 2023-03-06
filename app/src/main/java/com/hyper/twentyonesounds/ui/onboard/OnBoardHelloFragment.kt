package com.hyper.twentyonesounds.ui.onboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hyper.twentyonesounds.databinding.FragmentOnboardHelloBinding

class OnBoardHello : Fragment() {

    private var _binding : FragmentOnboardHelloBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, group: ViewGroup?, saved: Bundle?
    ): View {
        _binding = FragmentOnboardHelloBinding.inflate(inflater, group, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}