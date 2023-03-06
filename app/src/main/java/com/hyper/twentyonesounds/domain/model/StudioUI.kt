package com.hyper.twentyonesounds.domain

import java.io.Serializable

data class StudioUI (
    val artists: List<ArtistUI>,
    val songs: List<SongUI>,
    val albums: List<AlbumUI>
) : Serializable

data class AlbumUI (
    val id: String,
    val title: String,
    val likes: Int,
    val image_url: String,
    val songs: List<Int>,
    var loved: Boolean = false,
    var itsLovely: Boolean = true,
) : Serializable

data class SongUI (
    val id: Int,
    val title: String,
    val artist: ArtistUI,
    val image_url: String,
    val duration_seconds: Int,
    var loved: Boolean = false,
) : Serializable

data class ArtistUI (
    val id: Int,
    val name: String,
    val image_url: String,
    val likes: Int
) : Serializable