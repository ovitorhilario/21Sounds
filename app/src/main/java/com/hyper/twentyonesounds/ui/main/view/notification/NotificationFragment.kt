package com.hyper.twentyonesounds.ui.main.view.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.hyper.twentyonesounds.databinding.FragmentMainNotificationsBinding
import com.hyper.twentyonesounds.domain.SongUI
import com.hyper.twentyonesounds.domain.StudioUI
import com.hyper.twentyonesounds.ui.main.adapter.home.SongInNotificationAdapter
import com.hyper.twentyonesounds.ui.main.view.home.SongFragment
import com.hyper.twentyonesounds.utils.extension.addToStack
import com.hyper.twentyonesounds.utils.extension.toSongsUI

class NotificationFragment : Fragment() {

    private var _binding: FragmentMainNotificationsBinding? = null
    private val binding get () = _binding!!

    override fun onCreateView(inflater: LayoutInflater, group: ViewGroup?, saved: Bundle?): View {
        _binding = FragmentMainNotificationsBinding.inflate(LayoutInflater.from(group?.context), group, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    /*-----------------------------------------
    |       Setting the listeners from UI     |
    -----------------------------------------*/

    private fun setupListeners() {

        binding.btnNotificationReturn.onClick = {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.chipGroupFilter.setOnCheckedStateChangeListener { chip, isChecked ->

            val studio = requireArguments().getSerializable("STUDIO") as StudioUI
            val songs = studio.albums[0].songs.toSongsUI(studio.songs)
            val podcasts = studio.albums[1].songs.toSongsUI(studio.songs)

            when(chip.checkedChipId) {
                binding.chipSongs.id -> setupAdapter(0, songs)
                binding.chipPodcasts.id -> setupAdapter(1, podcasts)
            }
        }

        binding.chipGroupFilter.check(binding.chipSongs.id)
    }

    /*----------------------------------------------------------
    |       Setting Adapter to show the songs and podcasts     |
    ----------------------------------------------------------*/

    private fun setupAdapter(type: Int, songsOrPodcasts: List<SongUI>) {
        binding.rvMusicsAndPodcasts.adapter = SongInNotificationAdapter(resources, type, songsOrPodcasts) { song ->
            requireActivity().addToStack(SongFragment::class.java, bundleOf("SONG" to song))
        }
    }
}