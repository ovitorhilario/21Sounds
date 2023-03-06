package com.hyper.twentyonesounds.domain.studioUseCase

import com.hyper.twentyonesounds.data.api.Album
import com.hyper.twentyonesounds.data.api.Artist
import com.hyper.twentyonesounds.data.api.Song
import com.hyper.twentyonesounds.data.api.Studio
import com.hyper.twentyonesounds.data.repository.StudioRepository
import com.hyper.twentyonesounds.domain.AlbumUI
import com.hyper.twentyonesounds.domain.ArtistUI
import com.hyper.twentyonesounds.domain.SongUI
import com.hyper.twentyonesounds.domain.StudioUI

class GetStudioUseCase
(
    private val repository: StudioRepository
)
{
    suspend operator fun invoke() : StudioUI = repository.getStudio().toUIModel()

    private fun Studio.toUIModel() : StudioUI {
        val artistsUI = mutableListOf<ArtistUI>()
        val songsUI = mutableListOf<SongUI>()
        val albumsUI = mutableListOf<AlbumUI>()

        artists.forEach { artist -> artistsUI.add(artist.toUI()) }
        albums.forEach { album -> albumsUI.add(album.toUI()) }
        songs.forEach { song -> artistsUI.firstOrNull { it.id == song.artist_id }?.let { songsUI.add(song.toUI(it)) } }

        return StudioUI(artistsUI, songsUI, albumsUI)
    }

    private fun Song.toUI(artistUI: ArtistUI) : SongUI {
        return SongUI (
            id = this.id,
            title = this.title,
            artist = artistUI,
            image_url = this.image_url,
            duration_seconds = this.duration_seconds
        )
    }

    private fun Artist.toUI() : ArtistUI {
        return ArtistUI (
            id = this.id,
            name = this.name,
            image_url = this.image_url,
            likes = this.likes
        )
    }

    private fun Album.toUI() : AlbumUI {
        return AlbumUI (
            id = this.id,
            title = this.title,
            likes = this.likes,
            image_url = this.image_url,
            songs = this.songs
        )
    }
}