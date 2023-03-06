package com.hyper.twentyonesounds.domain.lovedSongsUseCase

import com.hyper.twentyonesounds.data.model.LovedSongsEntity
import com.hyper.twentyonesounds.data.repository.StudioRepository

class InsertLovedSongUseCase
(
    private val repository: StudioRepository
)
{
    suspend operator fun invoke(id: Int) {
        repository.insertLovedSong(LovedSongsEntity(id))
    }
}