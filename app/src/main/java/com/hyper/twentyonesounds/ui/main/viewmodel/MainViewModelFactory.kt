package com.hyper.twentyonesounds.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hyper.twentyonesounds.domain.*
import com.hyper.twentyonesounds.domain.lovedAlbumsUseCase.DeleteLovedAlbumUseCase
import com.hyper.twentyonesounds.domain.lovedAlbumsUseCase.GetAllLovedAlbumsUseCase
import com.hyper.twentyonesounds.domain.lovedAlbumsUseCase.InsertLovedAlbumUseCase
import com.hyper.twentyonesounds.domain.lovedSongsUseCase.DeleteLovedSongUseCase
import com.hyper.twentyonesounds.domain.lovedSongsUseCase.GetAllLovedSongsUseCase
import com.hyper.twentyonesounds.domain.lovedSongsUseCase.InsertLovedSongUseCase
import com.hyper.twentyonesounds.domain.studioUseCase.GetStudioUseCase


class MainViewModelFactory
(
    private val getStudio: GetStudioUseCase,

    private val getAllLovedSongs: GetAllLovedSongsUseCase,
    private val insertLovedSong: InsertLovedSongUseCase,
    private val deleteLovedSong: DeleteLovedSongUseCase,

    private val getAllLovedAlbums: GetAllLovedAlbumsUseCase,
    private val insertLovedAlbum: InsertLovedAlbumUseCase,
    private val deleteLovedAlbum: DeleteLovedAlbumUseCase,

    ) : ViewModelProvider.Factory
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass == MainViewModel::class.java) {
            return MainViewModel(getStudio,
                getAllLovedSongs, insertLovedSong, deleteLovedSong,
                getAllLovedAlbums, insertLovedAlbum, deleteLovedAlbum,
            ) as T
        }
        throw IllegalArgumentException("this class can't be instantiate")
    }
}