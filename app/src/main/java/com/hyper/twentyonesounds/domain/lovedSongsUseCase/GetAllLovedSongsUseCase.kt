package com.hyper.twentyonesounds.domain.lovedSongsUseCase

import com.hyper.twentyonesounds.data.repository.StudioRepository

class GetAllLovedSongsUseCase
(
    private val repository: StudioRepository
)
{
    suspend operator fun invoke() : List<Int> = repository.getAllLovedSongs().map { it.id }
}