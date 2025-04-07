package com.mrsomething.daneplay.data.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mrsomething.daneplay.data.AppDatabase
import com.mrsomething.daneplay.data.entity.MusicDanceMapping
import kotlinx.coroutines.launch

class MusicDanceMappingViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).musicDanceMappingDao()

    fun addMapping(mapping: MusicDanceMapping) {
        viewModelScope.launch {
            dao.insertMapping(mapping)
        }
    }
}
