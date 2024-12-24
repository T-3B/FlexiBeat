package com.example.flexibeat.data.datasave

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import com.example.flexibeat.data.AUDIOFILE_DBKEY
import com.example.flexibeat.data.AudioFile

@Dao
interface AudioFileDao {
    @Insert
    suspend fun insertAllAudioFiles(audioFiles: List<AudioFile>)

    @Query("SELECT * FROM $AUDIOFILE_DBKEY ORDER BY title")
    suspend fun getAllAudioFiles(): List<AudioFile>

    @Query("DELETE FROM $AUDIOFILE_DBKEY")
    suspend fun deleteAllAudioFiles()

    @Transaction
    suspend fun deleteAndInsertAllAudioFiles(audioFiles: List<AudioFile>) {
        deleteAllAudioFiles()
        insertAllAudioFiles(audioFiles)
    }
}

@Database(entities = [AudioFile::class], version = 1, exportSchema = false)
abstract class QueueDatabase : RoomDatabase() {

    abstract fun audioFileDao(): AudioFileDao

    companion object {
        @Volatile
        private var INSTANCE: QueueDatabase? = null

        fun getDatabase(context: Context): QueueDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QueueDatabase::class.java,
                    AudioFile::class.simpleName
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}