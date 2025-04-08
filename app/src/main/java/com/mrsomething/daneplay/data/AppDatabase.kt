package com.mrsomething.daneplay.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mrsomething.daneplay.data.dao.DanceDao
import com.mrsomething.daneplay.data.dao.MusicDanceMappingDao
import com.mrsomething.daneplay.data.entity.DanceDef
import com.mrsomething.daneplay.data.entity.MusicDanceMapping

@Database(
    entities = [DanceDef::class, MusicDanceMapping::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun danceDao(): DanceDao
    abstract fun musicDanceMappingDao(): MusicDanceMappingDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build().also {
                    INSTANCE = it
                }
            }
        }
    }
}
