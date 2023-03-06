package com.hyper.twentyonesounds.application

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.hyper.twentyonesounds.data.api.StudioService
import com.hyper.twentyonesounds.data.repository.StudioRepository
import com.hyper.twentyonesounds.data.model.AppDatabase

class MainApplication : Application()
{
    private val appDataBase by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { StudioRepository(StudioService(), appDataBase.lovedItemsDao()) }
}