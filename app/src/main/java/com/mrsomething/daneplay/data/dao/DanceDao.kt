package com.mrsomething.daneplay.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mrsomething.daneplay.data.entity.DanceDef

@Dao
interface DanceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDance(dance: DanceDef)

    @Query("SELECT * FROM dance_def ORDER BY name")
    suspend fun getAllDances(): List<DanceDef>
}