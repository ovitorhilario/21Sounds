package com.hyper.twentyonesounds.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LovedItemsDao {

    @Query("SELECT * FROM loved_songs")
    suspend fun getAllSongs() : List<LovedSongsEntity>

    @Insert
    suspend fun insertSong(favoriteSongId: LovedSongsEntity)

    @Delete
    suspend fun deleteSong(favoriteSongId: LovedSongsEntity)

    @Query("SELECT * FROM loved_albums")
    suspend fun getAllAlbums() : List<LovedAlbumsEntity>

    @Insert
    suspend fun insertAlbum(favoriteAlbumId: LovedAlbumsEntity)

    @Delete
    suspend fun deleteAlbum(favoriteAlbumId: LovedAlbumsEntity)
}