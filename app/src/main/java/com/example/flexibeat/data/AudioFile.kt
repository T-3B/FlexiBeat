package com.example.flexibeat.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import java.io.File

private const val AUDIOFILE_DBKEY = "audio_file"

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
abstract class AudioDatabase : RoomDatabase() {

    abstract fun audioFileDao(): AudioFileDao

    companion object {
        @Volatile
        private var INSTANCE: AudioDatabase? = null

        fun getDatabase(context: Context): AudioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AudioDatabase::class.java,
                    AudioFile::class.simpleName
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Entity(tableName = AUDIOFILE_DBKEY)
data class AudioFile(
    @PrimaryKey val id: Long,
    val title: String?,
    val album: String?,
    val artist: String?,
    val coverPath: String?,
    val duration: Long?
)

data class FileExplorerItems(
    val folders: List<File>,
    val files: List<AudioFile>
)