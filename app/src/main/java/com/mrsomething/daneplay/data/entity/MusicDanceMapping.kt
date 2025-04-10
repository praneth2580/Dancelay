package com.mrsomething.daneplay.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "music_dance_mapping",
    foreignKeys = [
        ForeignKey(
            entity = DanceDef::class,
            parentColumns = ["dance_id"],
            childColumns = ["dance_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["dance_id"])]
)
data class MusicDanceMapping(
    @PrimaryKey(autoGenerate = true) val music_dance_mapping_id: Int = 0,
    val name: String,
    val file_path: String,
    val order: Int,
    val start_time: Long? = null,
    val start_transition_type: String? = null,
    val start_transition_duration: Long? = null,
    val end_time: Long? = null,
    val end_transition_type: String? = null,
    val end_transition_duration: Long? = null,
    val dance_id: Int // 🔗 Foreign key to dance_def
)

