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

    @Query("SELECT * FROM music_dance_mapping where dance_id = :dance_id ORDER BY `order`")
    suspend fun getMusicByDanceId(dance_id: Int): List<MusicDanceMapping>

    @Query("SELECT count(music_dance_mapping_id) FROM music_dance_mapping where dance_id = :dance_id ORDER BY `order`")
    suspend fun getMusicNextOrder(dance_id: Int): Int

    @Transaction
    @Query("SELECT * FROM music_dance_mapping")
    suspend fun getMappingsWithDance(): List<MappingWithDance>

    @Query("DELETE FROM music_dance_mapping WHERE dance_id = :danceId")
    suspend fun deleteByDanceId(danceId: Int)
}