package com.mrsomething.daneplay.data.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mrsomething.daneplay.data.AppDatabase
import com.mrsomething.daneplay.data.entity.DanceDef
import com.mrsomething.daneplay.data.entity.MusicDanceMapping
import kotlinx.coroutines.launch

class MusicDanceMappingViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).musicDanceMappingDao()

    suspend fun getMusicByDanceId(dance_id: Int): List<MusicDanceMapping> {
        return dao.getMusicByDanceId(dance_id)
    }

    suspend fun getNextOrder(dance_id: Int): Int {
        return dao.getMusicNextOrder(dance_id)
    }

    fun deleteMappingByDanceId(dance_id: Int) {
        viewModelScope.launch {
            dao.deleteByDanceId(dance_id)
        }
    }

    fun addMappingSingle(mapping: MusicDanceMapping) {
        viewModelScope.launch {
            dao.insertMapping(mapping)
        }
    }

    fun addMapping(dance: DanceDef, mappings: List<MusicDanceMapping>) {
        viewModelScope.launch {

            dao.deleteByDanceId(dance.dance_id)

            mappings.forEach { music_dance ->
                dao.insertMapping(music_dance)
            }

        }
    }
}
