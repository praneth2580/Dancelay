package com.mrsomething.daneplay.data.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mrsomething.daneplay.data.AppDatabase
import com.mrsomething.daneplay.data.entity.DanceDef
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DanceViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).danceDao()

    // Expose list of dances as StateFlow
    val allDances: StateFlow<List<DanceDef>> = dao.getAllDances()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    suspend fun getDance(dance_id: Int): DanceDef {
        return dao.getDance(dance_id)
    }

    fun addDance(name: String) {
        viewModelScope.launch {
            dao.insertDance(DanceDef(name = name))
        }
    }
}
