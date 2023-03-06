package com.hyper.twentyonesounds.ui.main.viewmodel

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyper.twentyonesounds.domain.*
import com.hyper.twentyonesounds.domain.lovedAlbumsUseCase.DeleteLovedAlbumUseCase
import com.hyper.twentyonesounds.domain.lovedAlbumsUseCase.GetAllLovedAlbumsUseCase
import com.hyper.twentyonesounds.domain.lovedAlbumsUseCase.InsertLovedAlbumUseCase
import com.hyper.twentyonesounds.domain.lovedSongsUseCase.DeleteLovedSongUseCase
import com.hyper.twentyonesounds.domain.lovedSongsUseCase.GetAllLovedSongsUseCase
import com.hyper.twentyonesounds.domain.lovedSongsUseCase.InsertLovedSongUseCase
import com.hyper.twentyonesounds.domain.studioUseCase.GetStudioUseCase
import kotlinx.coroutines.*
import java.time.LocalTime

class MainViewModel
(
    private val getStudio: GetStudioUseCase,

    // Songs
    private val getAllLovedSongs: GetAllLovedSongsUseCase,
    private val insertLovedSong: InsertLovedSongUseCase,
    private val deleteLovedSong: DeleteLovedSongUseCase,

    // Albums
    private val getAllLovedAlbums: GetAllLovedAlbumsUseCase,
    private val insertLovedAlbum: InsertLovedAlbumUseCase,
    private val deleteLovedAlbum: DeleteLovedAlbumUseCase,

    ) : ViewModel()
{
    private val _studioData = MutableLiveData<StudioUI>()
    val studioData = _studioData as LiveData<StudioUI>

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            val prevStudio = getStudio().updateStudio()

            withContext(Dispatchers.Main) {
                _studioData.value = prevStudio
            }
        }
    }

    private fun StudioUI.updateStudio() : StudioUI {
        val studio = this
        viewModelScope.launch {
            studio.songs.forEach { it.loved = false }
            studio.albums.forEach { it.loved = false }

            getAllLovedSongs().forEach { id ->
                studio.songs.firstOrNull { song -> song.id == id }?.let { it.loved = true }
            }

            getAllLovedAlbums().forEach { id ->
                studio.albums.firstOrNull { album -> album.id == id }?.let { it.loved = true }
            }
        }
        return studio
    }

    suspend fun insertSong(id: Int) : Boolean {

        var success: Boolean = false

        val insertJob = viewModelScope.launch {
            try {
                insertLovedSong(id)
                _studioData.value = _studioData.value?.updateStudio()
                success = true
            } catch(e: Exception) {
                success = false
            }
        }

        insertJob.join()

        return success
    }

    suspend fun deleteSong(id: Int) : Boolean {

        var success: Boolean = false

        val deleteJob = viewModelScope.launch {
            try {
                deleteLovedSong(id)
                _studioData.value = _studioData.value?.updateStudio()
                success = true
            } catch(e: Exception) {
                success = false
            }
        }

        deleteJob.join()

        return success
    }

    suspend fun insertAlbum(id: String) : Boolean {

        var success: Boolean = false

        val insertJob = viewModelScope.launch {
            try {
                insertLovedAlbum(id)
                _studioData.value = _studioData.value?.updateStudio()
                success = true
            } catch(e: Exception) {
                success = false
            }
        }

        insertJob.join()

        return success
    }

    suspend fun deleteAlbum(id: String) : Boolean {

        var success: Boolean = false

        val deleteJob = viewModelScope.launch {
            try {
                deleteLovedAlbum(id)
                _studioData.value = _studioData.value?.updateStudio()
                success = true
            } catch(e: Exception) {
                success = false
            }
        }

        deleteJob.join()

        return success
    }

    // Private variables
    private val _state = MutableLiveData<SongState>()
    private val _track = MutableLiveData<List<Int>>()
    private val _positionOnTrack = MutableLiveData<Int>()
    private val _currentSecond = MutableLiveData<Int>()
    private var _soundThread: Job? = null

    // Observable variables from UI
    val state = _state as LiveData<SongState>
    val currentSecond = _currentSecond as LiveData<Int>
    val soundAlert = MutableLiveData<Action>(Action.AWAY)

    /* ----------------------
    |   Player Actions      |
    ---------------------- */

    private fun cancelSoundThread() = _soundThread?.cancel()

    fun pauseMusic() {

        if (!playerIsValid) return

        cancelSoundThread()
        _state.value = SongState.Paused
    }

    fun playMusic() {

        if (!playerIsValid) return

        _state.value = SongState.Playing
        soundAlert.value = Action.AWAY

        _soundThread = viewModelScope.launch {

            val track = _track.value!!
            val position = _positionOnTrack.value!!

            val song = _studioData.value?.songs?.get(track[position])!!
            val totalTime = song.duration_seconds

            while ((_currentSecond.value ?: 0) <= totalTime) {
                delay(1000)
                _currentSecond.value = _currentSecond.value?.plus(1) ?: 0
            }

            if (track.indexOf(track[position]) == track.lastIndex) {
                resetPlayer()
            } else {
                skipMusic()
            }
        }
    }

    fun skipMusic() {

        if (!playerIsValid) return

        val trackSize = _track.value?.size ?: 0
        val positionOnTrack = _positionOnTrack.value ?: 0
        val newPositionOnTrack = positionOnTrack + 1

        if (newPositionOnTrack in 0 until trackSize) {
            cancelSoundThread()
            _positionOnTrack.value = _positionOnTrack.value?.plus(1)
            _currentSecond.value = 0
            soundAlert.value = Action.SKIPPING
            playMusic()
        }
    }

    fun playPrevious() {

        if (!playerIsValid) return

        val trackSize = _track.value?.size ?: 0
        val positionOnTrack = _positionOnTrack.value ?: 0
        val newPositionOnTrack = positionOnTrack - 1

        if (newPositionOnTrack in 0 until trackSize) {
            cancelSoundThread()
            _positionOnTrack.value = _positionOnTrack.value?.minus(1)
            _currentSecond.value = 0
            playMusic()
        }
    }

    fun resetPlayer() {
        if (playerIsValid) cancelSoundThread()
        _track.value = emptyList()
        _currentSecond.value = 0
        _positionOnTrack.value = 0
        _state.value = SongState.Stopped
        soundAlert.value = Action.AWAY
    }

    /* ----------------------
    |   Auxiliary Functions |
    ---------------------- */

     fun getPlayerInfo() : Pair<SongState, Triple<List<Int>, Int, Int>> =
        Pair((_state.value ?: SongState.Stopped), Triple((_track.value ?: emptyList()),
            (_positionOnTrack.value ?: -1), (_currentSecond.value ?: -1)))

    val playerIsValid get () =
        (!_track.value.isNullOrEmpty() && _positionOnTrack.value != null && _currentSecond.value != null)

    val studioIsValid get () = run {
        (_studioData.value?.albums?.isNotEmpty() ?: false) && (_studioData.value?.songs?.isNotEmpty() ?: false) &&
        (_studioData.value?.artists?.isNotEmpty() ?: false)
    }

    fun setupPlayerFromUI(newTrack: List<Int>) {
        if (newTrack.isEmpty()) return
        resetPlayer()
        _track.value = newTrack
        _state.value = SongState.Paused
    }

    sealed class SongState {
        object Playing : SongState()
        object Paused : SongState()
        object Stopped : SongState()
    }

    sealed class Action {
        object SKIPPING : Action()
        object AWAY : Action()
    }
}
