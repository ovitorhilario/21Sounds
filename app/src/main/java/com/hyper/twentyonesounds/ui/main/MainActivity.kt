package com.hyper.twentyonesounds.ui.main

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.util.newStringBuilder
import com.hyper.twentyonesounds.R
import com.hyper.twentyonesounds.application.MainApplication
import com.hyper.twentyonesounds.databinding.ActivityMainBinding
import com.hyper.twentyonesounds.domain.*
import com.hyper.twentyonesounds.domain.lovedAlbumsUseCase.DeleteLovedAlbumUseCase
import com.hyper.twentyonesounds.domain.lovedAlbumsUseCase.GetAllLovedAlbumsUseCase
import com.hyper.twentyonesounds.domain.lovedAlbumsUseCase.InsertLovedAlbumUseCase
import com.hyper.twentyonesounds.domain.lovedSongsUseCase.DeleteLovedSongUseCase
import com.hyper.twentyonesounds.domain.lovedSongsUseCase.GetAllLovedSongsUseCase
import com.hyper.twentyonesounds.domain.lovedSongsUseCase.InsertLovedSongUseCase
import com.hyper.twentyonesounds.domain.studioUseCase.GetStudioUseCase
import com.hyper.twentyonesounds.network.NetworkChecker
import com.hyper.twentyonesounds.ui.main.view.home.MainHomeFragment
import com.hyper.twentyonesounds.ui.main.model.home.UserPreferences
import com.hyper.twentyonesounds.ui.main.view.InternetErrorFragment
import com.hyper.twentyonesounds.ui.main.view.home.SongFragment
import com.hyper.twentyonesounds.ui.main.viewmodel.MainViewModel
import com.hyper.twentyonesounds.ui.main.viewmodel.MainViewModelFactory
import com.hyper.twentyonesounds.ui.main.view.library.MainLibraryFragment
import com.hyper.twentyonesounds.ui.main.view.search.MainSearchFragment
import com.hyper.twentyonesounds.utils.extension.addToStack
import com.hyper.twentyonesounds.utils.extension.loadImage
import com.hyper.twentyonesounds.utils.extension.replaceStack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class MainActivity : AppCompatActivity() {

    val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    val userPrefFlow: Flow<UserPreferences> by lazy {
        dataStore.data.map { currentPreferences ->
            UserPreferences(
                currentPreferences[PREF_PROFILE_NAME] ?: "",
                currentPreferences[PREF_PROFILE_ID] ?: "",
                currentPreferences[PREF_PROFILE_EMAIL] ?: "",
            )
        }
    }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel: MainViewModel by viewModels {
        val rep = (application as MainApplication).repository
        MainViewModelFactory(
            GetStudioUseCase(rep),
            // Songs
            GetAllLovedSongsUseCase(rep),
            InsertLovedSongUseCase(rep),
            DeleteLovedSongUseCase(rep),
            // Albums
            GetAllLovedAlbumsUseCase(rep),
            InsertLovedAlbumUseCase(rep),
            DeleteLovedAlbumUseCase(rep),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        NetworkChecker(manager).performAction (
            ifConnected = {
                setupBottomNav()
                setupSongBottomSheet()

                KeyboardVisibilityEvent.setEventListener (this) { isOpen ->
                    if (isOpen) {
                        binding.bnMain.visibility = View.GONE
                    } else {
                        binding.bnMain.visibility = View.VISIBLE
                    }
                }
            },
            ifDisconnected = {
                showInternetError()
            }
        )
    }

    /*------------------------------------------
    |       Setting the navigation bottom      |
    ------------------------------------------*/

    private fun setupBottomNav() {

        binding.bnMain.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_home -> {
                    replaceStack(MainHomeFragment::class.java, null)
                    true
                }
                R.id.page_search -> {
                    replaceStack(MainSearchFragment::class.java,null)
                    true
                }
                R.id.page_library -> {
                    replaceStack(MainLibraryFragment::class.java,null)
                    true
                }
                else -> false
            }
        }

        binding.bnMain.selectedItemId = R.id.page_home
        binding.bnMain.bringToFront()
    }

    /*---------------------------------------
    |       Setting the SongBottomSheet     |
    ---------------------------------------*/

    private fun setupSongBottomSheet() {
        binding.songBottomSheet.bringToFront()
        setupSongListeners()
    }

    /*-------------------------------------
    |       SongBottomSheet Listeners     |
    -------------------------------------*/

    private fun setupSongListeners() {

        viewModel.resetPlayer()
        setupSongObservers()

        binding.btnSkipMusicOnPlaying.onClick = { if (viewModel.playerIsValid) { viewModel.skipMusic() } }

        binding.songBottomSheet.setOnClickListener {

            val (_, player) = viewModel.getPlayerInfo()
            val (track, positionOnTrack, _) = player

            val song = viewModel.studioData.value?.songs?.get(track[positionOnTrack])

            if (!viewModel.playerIsValid || track.isEmpty() || song == null) return@setOnClickListener

            val currentFragment = supportFragmentManager.findFragmentById(R.id.fcv_main)

            when(currentFragment) {
                is MainHomeFragment -> {
                    addToStack(SongFragment::class.java, bundleOf("SONG" to song))
                }
                is SongFragment -> {
                    val otherSongInfo = currentFragment.requireArguments().getSerializable("SONG") as SongUI
                    if (otherSongInfo.id != song.id) {
                        addToStack(SongFragment::class.java, bundleOf("SONG" to otherSongInfo))
                    }
                }
                else -> {
                    // binding.bnMain.selectedItemId = R.id.page_home
                    addToStack(SongFragment::class.java, bundleOf("SONG" to song))
                }
            }
        }
    }

    /*--------------------------------
    |       SongPlayer Observers     |
    --------------------------------*/

    private fun setupSongObservers() {
        viewModel.run {
            currentSecond.observe(this@MainActivity) { value -> updateProgressBar(value) }

            state.observe(this@MainActivity) { state ->
                when(state) {
                    MainViewModel.SongState.Playing -> handleStatePlaying()
                    MainViewModel.SongState.Paused -> handleStatePaused()
                    MainViewModel.SongState.Stopped -> handleStateAway()
                }
            }
        }
    }

    /*--------------------------------
    |       Handle States Player     |
    --------------------------------*/

    private fun handleStatePlaying() {
        binding.run {
            turnOnSongBottomSheet()
            btnPlayAndPauseOnPlaying.setIconFun(R.drawable.ic_pause)
            btnPlayAndPauseOnPlaying.onClick = { if (viewModel.playerIsValid) { viewModel.pauseMusic() } }
        }
    }

    private fun handleStatePaused() {
        binding.run {
            turnOnSongBottomSheet()
            btnPlayAndPauseOnPlaying.setIconFun(R.drawable.ic_play)
            btnPlayAndPauseOnPlaying.onClick = { if (viewModel.playerIsValid) { viewModel.playMusic() } }
        }
    }

    private fun handleStateAway() {
        binding.run {
            turnOffSongBottomSheet()
            btnPlayAndPauseOnPlaying.setIconFun(R.drawable.ic_play)
        }
    }

    /*----------------------------------------------
    |       TurnOff and TurnOn the bottomSheet     |
    ----------------------------------------------*/

    private fun turnOnSongBottomSheet() {
        binding.run {
            songBottomSheet.visibility = View.VISIBLE
            getMusicInfo()
        }
    }

    private fun turnOffSongBottomSheet() {
        binding.songBottomSheet.visibility = View.GONE
    }

    /*-------------------------------------------------
    |       Update the UI getting the currentSong     |
    -------------------------------------------------*/

    private fun getMusicInfo() {

        val (_, player) = viewModel.getPlayerInfo()
        val (track, positionOnTrack, _) = player
        val song = viewModel.studioData.value?.songs?.get(track[positionOnTrack])

        if (!viewModel.playerIsValid || track.isEmpty() || song == null) return

        binding.run {
            tvSongNameOnPlaying.text = song.title
            tvMusicArtistOnPlaying.text = song.artist.name
            ivSongIconOnPlaying.loadImage(song.image_url, false)
            lpiSongOnPlaying.max = song.duration_seconds
        }
    }

    private fun updateProgressBar(value: Int) {
        binding.lpiSongOnPlaying.progress = value
    }

    companion object {
        val PREF_PROFILE_NAME = stringPreferencesKey("user_name")
        val PREF_PROFILE_ID = stringPreferencesKey("user_id")
        val PREF_PROFILE_EMAIL = stringPreferencesKey("user_email")
    }

    private fun showInternetError() {
        replaceStack(InternetErrorFragment::class.java)
    }
}