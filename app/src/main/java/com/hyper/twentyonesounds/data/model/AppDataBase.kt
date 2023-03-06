package com.hyper.twentyonesounds.data.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [LovedSongsEntity::class, LovedAlbumsEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lovedItemsDao(): LovedItemsDao

    companion object
    {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase
        {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "loved_items_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}