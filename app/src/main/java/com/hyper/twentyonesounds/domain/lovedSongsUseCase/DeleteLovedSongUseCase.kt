package com.hyper.twentyonesounds.domain.lovedSongsUseCase

import com.hyper.twentyonesounds.data.model.LovedSongsEntity
import com.hyper.twentyonesounds.data.repository.StudioRepository

class DeleteLovedSongUseCase
(
    private val repository: StudioRepository
)
{
    suspend operator fun invoke(id: Int) {
        repository.deleteLovedSong(LovedSongsEntity(id))
    }
}