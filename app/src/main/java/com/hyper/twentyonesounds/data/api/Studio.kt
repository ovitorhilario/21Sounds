package com.hyper.twentyonesounds.data.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Studio (
    @SerializedName("artists")
    val artists: List<Artist>,
    @SerializedName("songs")
    val songs: List<Song>,
    @SerializedName("albums")
    val albums: List<Album>,
) : Serializable

data class Artist (
    val id: Int,
    val name: String,
    val image_url: String,
    val likes: Int
) : Serializable

data class Album (
    val id: String,
    val title: String,
    val likes: Int,
    val image_url: String,
    val songs: List<Int>
) : Serializable

data class Song (
    val id: Int,
    val title: String,
    val image_url: String,
    val duration_seconds: Int,
    val artist_id: Int
) : Serializable