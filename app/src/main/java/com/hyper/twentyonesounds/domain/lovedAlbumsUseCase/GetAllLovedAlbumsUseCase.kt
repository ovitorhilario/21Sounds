package com.hyper.twentyonesounds.domain.lovedAlbumsUseCase

import com.hyper.twentyonesounds.data.repository.StudioRepository

class GetAllLovedAlbumsUseCase
(
    private val repository: StudioRepository
)
{
    suspend operator fun invoke() : List<String> = repository.getAllLovedAlbums().map { it.id }
}