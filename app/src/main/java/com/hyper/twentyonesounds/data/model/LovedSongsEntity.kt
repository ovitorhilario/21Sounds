package com.hyper.twentyonesounds.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loved_songs")
data class LovedSongsEntity (
    @PrimaryKey @ColumnInfo("id") val id : Int,
)