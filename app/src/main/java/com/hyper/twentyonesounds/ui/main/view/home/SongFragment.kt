package com.hyper.twentyonesounds.ui.main.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.hyper.twentyonesounds.R
import com.hyper.twentyonesounds.databinding.FragmentMainSongBinding
import com.hyper.twentyonesounds.domain.SongUI
import com.hyper.twentyonesounds.ui.main.viewmodel.MainViewModel
import com.hyper.twentyonesounds.utils.extension.addToStack
import com.hyper.twentyonesounds.utils.extension.loadImage
import com.hyper.twentyonesounds.utils.extension.secondsToMinutes
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class SongFragment : Fragment() {

    private var _binding: FragmentMainSongBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, group: ViewGroup?, saved: Bundle?): View {
        _binding = FragmentMainSongBinding.inflate(LayoutInflater.from(group?.context), group, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentSong = requireArguments().getSerializable("SONG") as SongUI

        currentSong.let { song ->
            setupUI(song)
            setupListeners(song)
            setupObservers(song)
        }
    }

    /*-------------------------------
    |       Setting Info to UI      |
    -------------------------------*/

    private fun setupUI(song: SongUI) {

        // Default Interface - No Playing

        binding.run {

            // Controllers - Player
            resetSeekbar(song)

            // Music Info
            tvSongName.text = song.title
            // tvSongNameOnTop.text = song.title
            tvSongArtistName.text = song.artist.name
            ivSongArtistIcon.loadImage(song.artist.image_url, isRounded = true, isCircular = true)
            ivSongIcon.layoutParams.height = resources.displayMetrics.widthPixels
            ivSongIcon.loadImage(song.image_url, false)
            cvSongIcon.bringToFront()
            btnSongReturn.bringToFront()
            btnPlayAndPause.bringToFront()
            btnSkipSong.bringToFront()
            btnPreviousSong.bringToFront()

            if(song.loved) {
                binding.btnLovedSong.setIconFun(R.drawable.ic_favorite)
            } else {
                binding.btnLovedSong.setIconFun(R.drawable.ic_favorite_outlined)
            }
        }
    }

    /*--------------------------------------
    |       Observable from viewModel      |
    --------------------------------------*/

    private fun setupObservers(song: SongUI) {

        viewModel.state.observe(viewLifecycleOwner) { state ->

            updateSeekbar(song, viewModel.currentSecond.value)

            when(state) {
                MainViewModel.SongState.Playing -> handleStatePlaying(song)
                MainViewModel.SongState.Paused -> handleStatePause(song)
                MainViewModel.SongState.Stopped -> handleStateAway()
            }
        }

        viewModel.soundAlert.observe(viewLifecycleOwner) { action ->
            if (action is MainViewModel.Action.SKIPPING && isTheSameFragment(song)) {
                tryOpenMusicTab()
            }
        }

        viewModel.currentSecond.observe(viewLifecycleOwner) { value -> updateSeekbar(song, value) }
    }

    /*----------------------------------
    |       Handle State Functions     |
    ----------------------------------*/

    private fun handleStatePlaying(song: SongUI) {
        if (whatIsTheContextOf(song) == 0) {
            binding.btnPlayAndPause.setImageResource(R.drawable.ic_pause)
        } else {
            binding.btnPlayAndPause.setImageResource(R.drawable.ic_play)
        }
    }

    private fun handleStatePause(song: SongUI) {
        if (whatIsTheContextOf(song) == 0) {
            binding.btnPlayAndPause.setImageResource(R.drawable.ic_play)
        }
    }

    private fun handleStateAway() {
        binding.btnPlayAndPause.setImageResource(R.drawable.ic_play)
    }

    /*--------------------------------
    |       Setting UI Listeners     |
    --------------------------------*/

    private fun setupListeners(song: SongUI) {

        binding.run {

            btnSongReturn.onClick = {
                requireActivity().supportFragmentManager.popBackStack()
            }

            btnLovedSong.onClick = {
                if (song.loved) {
                    lifecycleScope.launch {
                        val success = viewModel.deleteSong(song.id)

                        if(success) {
                            binding.btnLovedSong.setIconFun(R.drawable.ic_favorite_outlined)

                            MotionToast.darkColorToast(requireActivity(),
                                title = "Success",
                                message = "${song.title} removed from Liked Songs",
                                style = MotionToastStyle.SUCCESS,
                                position = MotionToast.GRAVITY_BOTTOM,
                                duration = MotionToast.SHORT_DURATION,
                                font = ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)
                            )
                        }
                    }

                } else {
                    lifecycleScope.launch {
                        val success = viewModel.insertSong(song.id)

                        if (success) {
                            binding.btnLovedSong.setIconFun(R.drawable.ic_favorite)

                            MotionToast.darkColorToast(requireActivity(),
                                title = "Success",
                                message = "${song.title} saved on Liked Songs",
                                style = MotionToastStyle.SUCCESS,
                                position = MotionToast.GRAVITY_BOTTOM,
                                duration = MotionToast.SHORT_DURATION,
                                font = ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)
                            )
                        }
                    }
                }
            }

            sbSongDuration.setOnTouchListener { v, event -> true }

            btnPlayAndPause.setOnClickListener {
                when(whatIsTheContextOf(song)) {
                    -1 -> handleContextNothingPlaying(song)
                    0 -> handleContextThisSongIsPlaying()
                    1 -> handleContextOtherSongIsPlaying(song)
                }
            }

            btnPreviousSong.onClick = {
                if(whatIsTheContextOf(song) == 0) {
                    viewModel.playPrevious()
                    tryOpenMusicTab()
                }
            }

            btnSkipSong.onClick = {
                if(whatIsTheContextOf(song) == 0) {
                    viewModel.skipMusic()
                    tryOpenMusicTab()
                }
            }
        }
    }

    /*------------------------------------
    |       Handle Context Functions     |
    ------------------------------------*/

    private fun handleContextNothingPlaying(song: SongUI) {
        viewModel.setupPlayerFromUI(listOf(song.id))
        viewModel.playMusic()
    }

    private fun handleContextThisSongIsPlaying() {
        if (viewModel.state.value == MainViewModel.SongState.Playing) {
            viewModel.pauseMusic()
        }
        else if (viewModel.state.value == MainViewModel.SongState.Paused) {
            viewModel.playMusic()
        }
    }

    private fun handleContextOtherSongIsPlaying(song: SongUI) {
        viewModel.setupPlayerFromUI(listOf(song.id))
        viewModel.playMusic()
    }

    /*------------------------------------------------
    |       Trying to open the new Song Fragment     |
    ------------------------------------------------*/

    private fun tryOpenMusicTab() {
        val (_, player) = viewModel.getPlayerInfo()
        val (track, positionOnTrack, _) = player

        val song = viewModel.studioData.value?.songs?.get(track[positionOnTrack])

        if (viewModel.playerIsValid && track.isNotEmpty() && song != null) {
            if (isTheSameFragment(song)) return

            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().addToStack(SongFragment::class.java, bundleOf("SONG" to song))
        }
    }

    /*---------------------------------
    |       Player SeekBar config     |
    ---------------------------------*/

    private fun updateSeekbar(song: SongUI, currentSecond: Int? = null) {
        if (whatIsTheContextOf(song) == 0) {
            currentSecond?.let { second ->
                binding.sbSongDuration.progress = second
                binding.tvCurrentTime.text = second.secondsToMinutes()
            }
        } else {
            resetSeekbar(song)
        }
    }

    private fun resetSeekbar(song: SongUI) {
        binding.run {
            tvCurrentTime.text = "00:00"
            tvTotalTime.text = song.duration_seconds.secondsToMinutes()
            sbSongDuration.max = song.duration_seconds
            sbSongDuration.progress = 0
        }
    }

    /*--------------------------------------------------------
    |       Auxiliary functions to help the actions in UI    |
    --------------------------------------------------------*/

    private fun isTheSameFragment(song: SongUI) : Boolean {
        val currentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fcv_main)

        return when (currentFragment) {
            is SongFragment -> (currentFragment.requireArguments().getSerializable("SONG") as SongUI).id == song.id
            else -> false
        }
    }

    private fun whatIsTheContextOf(thisSong: SongUI) : Int {
        val (_, player) = viewModel.getPlayerInfo()
        val (track, positionOnTrack, _) = player

        if(viewModel.studioData.value?.songs?.isEmpty() ?: true) return -1

        return when (viewModel.playerIsValid && track.isNotEmpty() && positionOnTrack >= 0) {
            true -> if (thisSong.id == track[positionOnTrack]) 0 else 1
            else -> -1
        }

        // -1 -> O player está parado (STOPPED)
        //  0 -> A música deste fragmento está tocando
        //  1 -> Outra música está tocando
    }
}


