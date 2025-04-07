package com.mrsomething.daneplay.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "dance_def",
    indices = [Index(value = ["name"], unique = true)]
)
data class DanceDef(
    @PrimaryKey(autoGenerate = true) val dance_id: Int = 0,
    val name: String
)
