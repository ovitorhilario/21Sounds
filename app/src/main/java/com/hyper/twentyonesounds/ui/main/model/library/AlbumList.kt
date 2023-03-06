package com.hyper.twentyonesounds.ui.main.model.library

import com.hyper.twentyonesounds.domain.AlbumUI
import java.io.Serializable

data class AlbumList (
    val id: String,
    val title: String,
    val description: String,
    val image_url: String,
    val albums: List<AlbumUI>
) : Serializable