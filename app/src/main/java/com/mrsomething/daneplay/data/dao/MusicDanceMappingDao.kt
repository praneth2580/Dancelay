package com.mrsomething.daneplay.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mrsomething.daneplay.data.entity.MusicDanceMapping
import com.mrsomething.daneplay.data.relations.MappingWithDance

@Dao
interface MusicDanceMappingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMapping(mapping: MusicDanceMapping)

    @Query("SELECT * FROM music_dance_mapping ORDER BY `order`")
    suspend fun getAllMappings(): List<MusicDanceMapping>

    @Transaction
    @Query("SELECT * FROM music_dance_mapping")
    suspend fun getMappingsWithDance(): List<MappingWithDance>
}