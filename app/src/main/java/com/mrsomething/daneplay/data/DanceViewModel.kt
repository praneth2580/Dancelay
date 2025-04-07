package com.mrsomething.daneplay.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mrsomething.daneplay.data.entity.DanceDef
import kotlinx.coroutines.launch

class DanceViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).danceDao()

    fun addDance(name: String) {
        viewModelScope.launch {
            dao.insertDance(DanceDef(name = name))
        }
    }
}
