package com.hyper.twentyonesounds.ui.main.view.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hyper.twentyonesounds.R
import com.hyper.twentyonesounds.databinding.FragmentMainLibraryBinding
import com.hyper.twentyonesounds.domain.AlbumUI
import com.hyper.twentyonesounds.ui.main.view.home.AlbumFragment
import com.hyper.twentyonesounds.ui.main.view.notification.NotificationFragment
import com.hyper.twentyonesounds.ui.main.view.home.ProfileFragment
import com.hyper.twentyonesounds.ui.main.viewmodel.MainViewModel
import com.hyper.twentyonesounds.ui.main.adapter.library.AlbumInLibraryAdapter
import com.hyper.twentyonesounds.ui.main.model.library.AlbumList
import com.hyper.twentyonesounds.utils.extension.addToStack

class MainLibraryFragment : Fragment(R.layout.fragment_main_library) {

    private var _binding : FragmentMainLibraryBinding? = null
    private val binding get () = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, group: ViewGroup?, saved: Bundle?): View {
        _binding = FragmentMainLibraryBinding.inflate(inflater, group, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupData()
        setupListeners()
    }

    private fun setupListeners() {
        binding.tbLibrary.setOnClickAction = {
            viewModel.studioData.observe(viewLifecycleOwner) { studio ->
                if (studio.songs.isNotEmpty()) {
                    requireActivity().addToStack(NotificationFragment::class.java, bundleOf("STUDIO" to studio))
                }
            }
        }

        binding.tbLibrary.setOnClickProfile = {
            requireActivity().addToStack(ProfileFragment::class.java, null)
        }
    }

    private fun setupData() {
        viewModel.studioData.observe(viewLifecycleOwner) { studio ->
            val albumsLibrary = listOf(
                AlbumType.SONGS to AlbumUI (
                    id = "user_data_liked_songs",
                    title = resources.getString(R.string.loved_songs_title),
                    likes = 0,
                    image_url = "https://i1.sndcdn.com/artworks-y6qitUuZoS6y8LQo-5s2pPA-t500x500.jpg",
                    songs = (studio.songs.filter { it.loved }.map { it.id }),
                    loved = false,
                    itsLovely = false
                ),
                AlbumType.SONGS to AlbumUI (
                    id = "user_data_all_songs",
                    title = resources.getString(R.string.all_songs_title),
                    likes = 0,
                    image_url = "https://preview.redd.it/rnqa7yhv4il71.jpg?width=640&crop=smart&auto=webp&s=819eb2bda1b35c7729065035a16e81824132e2f1",
                    songs = studio.songs.map { it.id },
                    loved = false,
                    itsLovely = false
                ),
                AlbumType.ALBUMS to AlbumList (
                    id = "user_data_liked_albums",
                    title = resources.getString(R.string.loved_albums_title),
                    description = "",
                    image_url = "https://i1.sndcdn.com/artworks-y6qitUuZoS6y8LQo-5s2pPA-t500x500.jpg",
                    albums = studio.albums.filter { it.loved }
                ),
                AlbumType.ALBUMS to AlbumList (
                    id = "user_data_all_albums",
                    title = resources.getString(R.string.all_albums_title),
                    description = "",
                    image_url = "https://preview.redd.it/rnqa7yhv4il71.jpg?width=640&crop=smart&auto=webp&s=819eb2bda1b35c7729065035a16e81824132e2f1",
                    albums = studio.albums
                )
            )

            setupAdapter(albumsLibrary)
        }
    }

    private fun setupAdapter(multipleAlbums: List<Pair<AlbumType, Any>>) {
        binding.rvLibrarySavedSounds.adapter = AlbumInLibraryAdapter(resources, multipleAlbums,
            actionOpenAlbumList = { album ->
                val args = bundleOf("ALBUM_LIST" to album)
                requireActivity().addToStack(MainLibraryAlbumListFragment::class.java, args)
            },
            actionOpenAlbumUI = { album ->
                val args = bundleOf("ALBUM" to album)
                requireActivity().addToStack(AlbumFragment::class.java, args)
            }
        )
    }

    sealed class AlbumType {
        object SONGS: AlbumType()
        object ALBUMS: AlbumType()
    }
}