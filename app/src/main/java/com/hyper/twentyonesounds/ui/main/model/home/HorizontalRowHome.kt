package com.hyper.twentyonesounds.ui.main.model.home

import com.hyper.twentyonesounds.domain.AlbumUI
import java.io.Serializable

data class HorizontalRowHome (
    val title: String,
    val albums: List<AlbumUI>
) : Serializable

