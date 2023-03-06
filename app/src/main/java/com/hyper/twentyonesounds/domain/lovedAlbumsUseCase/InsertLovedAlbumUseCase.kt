package com.hyper.twentyonesounds.domain.lovedAlbumsUseCase

import com.hyper.twentyonesounds.data.model.LovedAlbumsEntity
import com.hyper.twentyonesounds.data.repository.StudioRepository

class InsertLovedAlbumUseCase
(
    private val repository: StudioRepository
)
{
    suspend operator fun invoke(id: String) {
        repository.insertLovedAlbum(LovedAlbumsEntity(id))
    }
}