package com.hyper.twentyonesounds.ui.main.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.lifecycle.lifecycleScope
import com.hyper.twentyonesounds.R
import com.hyper.twentyonesounds.databinding.FragmentMainAlbumBinding
import com.hyper.twentyonesounds.domain.AlbumUI
import com.hyper.twentyonesounds.ui.main.adapter.home.SongInListAdapter
import com.hyper.twentyonesounds.ui.main.viewmodel.MainViewModel
import com.hyper.twentyonesounds.utils.extension.*
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class AlbumFragment : Fragment()
{
    private var _binding : FragmentMainAlbumBinding? = null
    private val binding get () = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, group: ViewGroup?, saved: Bundle?): View {
        _binding = FragmentMainAlbumBinding.inflate(LayoutInflater.from(group?.context), group, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val album = requireArguments().getSerializable("ALBUM") as AlbumUI

        setupUI(album)
        setupAlbumListeners(album)
        setupAlbumObservers(album)
    }

    /*-----------------------------------------
    |       Setting the UI with albumInfo     |
    -----------------------------------------*/

    private fun setupUI(currentAlbum: AlbumUI) {

        if(!viewModel.studioIsValid) return

        val songsInAlbum = currentAlbum.songs.toSongsUI(viewModel.studioData.value?.songs!!)

        binding.run {
            val songAdapter = SongInListAdapter { song ->
                val args = bundleOf("SONG" to song)
                requireActivity().addToStack(SongFragment::class.java, args)
            }

            songAdapter.submitList(songsInAlbum)
            rvMusicsAlbum.adapter = songAdapter
            cvAlbum.loadImage(currentAlbum.image_url)

            val (minutes, seconds) = songsInAlbum.sumOf{ it.duration_seconds }.secondsToMinutes().split(":")

            tvAlbumName.text = currentAlbum.title
            val songsCount = currentAlbum.songs.count()

            tvAlbumInfo.text = buildString {
                if (currentAlbum.itsLovely) {
                    append(currentAlbum.likes)
                    append(" ")
                    append(resources.getString(R.string.like_tittle))
                    append(" • ")
                }

                if (songsCount <= 0 || currentAlbum.songs.isEmpty()) {
                    append(resources.getString(R.string.no_songs_yet_desc))
                } else  {
                    append(songsCount)
                    append(" ")
                    append(
                        if(songsCount > 1)
                            resources.getString(R.string.song_plural_title)
                        else
                            resources.getString(R.string.song_singular_title)
                    )
                    append(" • ")
                    append(
                        if (minutes.toInt() == 0) {
                            seconds + "s"
                        } else {
                            minutesToHours(minutes.toInt())
                        }
                    )
                }
            }

            if (!currentAlbum.itsLovely) {
                binding.btnFavoriteInAlbum.visibility = View.GONE
            }

            if (currentAlbum.loved) {
                setBtnLovedEnable()
            }
        }
    }

    private fun setBtnLovedEnable() {
        binding.btnFavoriteInAlbum.setIconFun(R.drawable.ic_favorite)
    }

    private fun setBtnLovedDisable() {
        binding.btnFavoriteInAlbum.setIconFun(R.drawable.ic_favorite_outlined)
    }

    /*---------------------------------------
    |       Setting the album listeners     |
    ---------------------------------------*/

    private fun setupAlbumListeners(currentAlbum: AlbumUI) {

        binding.btnFavoriteInAlbum.onClick = {
            if (currentAlbum.itsLovely) {
                if (currentAlbum.loved) {
                    lifecycleScope.launch {
                        val success = viewModel.deleteAlbum(currentAlbum.id)

                        if(success) {
                            setBtnLovedDisable()

                            MotionToast.darkColorToast(requireActivity(),
                                title = "Success",
                                message = "${currentAlbum.title} removed from Liked Albums",
                                style = MotionToastStyle.SUCCESS,
                                position = MotionToast.GRAVITY_BOTTOM,
                                duration = MotionToast.SHORT_DURATION,
                                font = ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)
                            )
                        }
                    }

                } else {
                    lifecycleScope.launch {
                        val success = viewModel.insertAlbum(currentAlbum.id)

                        if (success) {
                            setBtnLovedEnable()

                            MotionToast.darkColorToast(requireActivity(),
                                title = "Success",
                                message = "${currentAlbum.title} saved on Liked Albums",
                                style = MotionToastStyle.SUCCESS,
                                position = MotionToast.GRAVITY_BOTTOM,
                                duration = MotionToast.SHORT_DURATION,
                                font = ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)
                            )
                        }
                    }
                }
            }
        }

        binding.ivBackAlbum.onClick = {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnPlayAndPauseInAlbum.onClick = {

            val (_, player) = viewModel.getPlayerInfo()
            val (track, _, _) = player

            if (track == currentAlbum.songs) {
                when (viewModel.state.value as MainViewModel.SongState) {
                    MainViewModel.SongState.Playing -> viewModel.pauseMusic()
                    MainViewModel.SongState.Paused-> viewModel.playMusic()
                    MainViewModel.SongState.Stopped -> viewModel.playMusic()
                }
            } else {
                viewModel.setupPlayerFromUI(currentAlbum.songs)
                viewModel.playMusic()
            }
        }
    }

    /*---------------------------------
    |       Album State Observers     |
    ---------------------------------*/

    private fun setupAlbumObservers(currentAlbum: AlbumUI){

        viewModel.state.observe(viewLifecycleOwner) { state ->

            val (_, player) = viewModel.getPlayerInfo()
            val (track, _, _) = player

            when (state) {
                MainViewModel.SongState.Playing -> {
                    if (track == currentAlbum.songs) {
                        binding.btnPlayAndPauseInAlbum.setIconFun(R.drawable.ic_pause)
                    } else {
                        binding.btnPlayAndPauseInAlbum.setIconFun(R.drawable.ic_play)
                    }
                }
                MainViewModel.SongState.Paused-> {
                    if (track == currentAlbum.songs) {
                        binding.btnPlayAndPauseInAlbum.setIconFun(R.drawable.ic_play)
                    }
                }
                MainViewModel.SongState.Stopped -> {
                    binding.btnPlayAndPauseInAlbum.setIconFun(R.drawable.ic_play)
                }
            }
        }
    }
}