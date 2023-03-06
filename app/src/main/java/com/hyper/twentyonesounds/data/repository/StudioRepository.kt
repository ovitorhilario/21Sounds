package com.hyper.twentyonesounds.data.repository

import androidx.annotation.WorkerThread
import com.hyper.twentyonesounds.data.api.Studio
import com.hyper.twentyonesounds.data.api.StudioService
import com.hyper.twentyonesounds.data.model.LovedAlbumsEntity
import com.hyper.twentyonesounds.data.model.LovedItemsDao
import com.hyper.twentyonesounds.data.model.LovedSongsEntity

class StudioRepository
(
    private val studioServiceInstance: StudioService,
    private val dataBaseInstance: LovedItemsDao
)
{
    suspend fun getStudio(): Studio = studioServiceInstance.getStudio()

    // SONG

    @WorkerThread
    suspend fun getAllLovedSongs() : List<LovedSongsEntity> = dataBaseInstance.getAllSongs()

    @WorkerThread
    suspend fun insertLovedSong(song: LovedSongsEntity) = dataBaseInstance.insertSong(song)

    @WorkerThread
    suspend fun deleteLovedSong(song: LovedSongsEntity) = dataBaseInstance.deleteSong(song)


    // ALBUM

    @WorkerThread
    suspend fun getAllLovedAlbums() : List<LovedAlbumsEntity> = dataBaseInstance.getAllAlbums()

    @WorkerThread
    suspend fun insertLovedAlbum(album: LovedAlbumsEntity) = dataBaseInstance.insertAlbum(album)

    @WorkerThread
    suspend fun deleteLovedAlbum(album: LovedAlbumsEntity) = dataBaseInstance.deleteAlbum(album)

}