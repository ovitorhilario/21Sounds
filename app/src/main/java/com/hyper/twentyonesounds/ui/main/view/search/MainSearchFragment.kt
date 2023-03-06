package com.hyper.twentyonesounds.ui.main.view.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import com.hyper.twentyonesounds.databinding.FragmentMainSearchBinding
import com.hyper.twentyonesounds.domain.SongUI
import com.hyper.twentyonesounds.ui.main.adapter.home.SongInListAdapter
import com.hyper.twentyonesounds.ui.main.view.notification.NotificationFragment
import com.hyper.twentyonesounds.ui.main.view.home.ProfileFragment
import com.hyper.twentyonesounds.ui.main.view.home.SongFragment
import com.hyper.twentyonesounds.ui.main.viewmodel.MainViewModel
import com.hyper.twentyonesounds.utils.extension.addToStack

class MainSearchFragment : Fragment() {

    private var _binding : FragmentMainSearchBinding? = null
    private val binding get () = _binding!!
    private val viewModel : MainViewModel by activityViewModels()
    private lateinit var songAdapter: SongInListAdapter
    private val queryResult = MutableLiveData<List<SongUI>?>()

    override fun onCreateView(inflater: LayoutInflater, group: ViewGroup?, saved: Bundle?): View {
        _binding = FragmentMainSearchBinding.inflate(inflater, group, false)
        return binding.root
    }

    override fun onViewCreated(view: View, saved: Bundle?) {
        super.onViewCreated(view, saved)

        initAdapter()
        setupAuxiliaryMessageForQuery()
        setupListeners()
    }

    private fun initAdapter() {
        songAdapter = SongInListAdapter (
            actionOpenSongPage = { song ->
            requireActivity().addToStack(SongFragment::class.java, bundleOf("SONG" to song))
        })

        binding.rvSearchResult.adapter = songAdapter
    }

    private fun setupListeners() {
        binding.inptSearch.editText?.doOnTextChanged { text, _, _, _ ->
            updateAdapter(text.toString().lowercase())
        }

        binding.tbSearch.setOnClickAction = {
            viewModel.studioData.observe(viewLifecycleOwner) { studio ->
                if (studio.songs.isNotEmpty()) {
                    requireActivity().addToStack(NotificationFragment::class.java, bundleOf("STUDIO" to studio))
                }
            }
        }

        binding.tbSearch.setOnClickProfile = {
            requireActivity().addToStack(ProfileFragment::class.java, null)
        }
    }

    private fun updateAdapter(query: String) {

        binding.tvInitialText.visibility = View.GONE

        if (query.isBlank() || query.isEmpty() || query == "") {
            songAdapter.submitList(emptyList())
            queryResult.value = emptyList()

        } else {
            val result = viewModel.studioData.value?.songs
                ?.filter { it.title.lowercase().contains(query) || it.artist.name.lowercase().contains(query) }
                ?.distinctBy { it.id }

            if ((result?.size ?: 0) > 0 && !result.isNullOrEmpty()) {
                songAdapter.submitList(result)
                queryResult.value = result
            } else {
                songAdapter.submitList(emptyList())
                queryResult.value = emptyList()
            }
        }
    }

    private fun setupAuxiliaryMessageForQuery() {
        queryResult.observe(viewLifecycleOwner) { song ->
            if (song.isNullOrEmpty()) {
                binding.tvWithoutResult.visibility = View.VISIBLE
            } else {
                binding.tvWithoutResult.visibility = View.GONE
            }
        }
    }
}