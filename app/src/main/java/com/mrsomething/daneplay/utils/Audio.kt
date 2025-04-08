package com.mrsomething.daneplay.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.mrsomething.daneplay.data.entity.DanceDef
import com.mrsomething.daneplay.data.entity.MusicDanceMapping
import com.mrsomething.daneplay.data.model.MusicDanceMappingViewModel

data class AudioFile(
    val uri: Uri,
    val name: String,
    var selected: Boolean = false,
    val albumArt: Uri? = null
)

suspend fun getAudioFiles(context: Context, dance: DanceDef? = null, viewModel: MusicDanceMappingViewModel? = null): List<AudioFile> {
    val audioList = mutableListOf<AudioFile>()
    val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val mappedMusic = dance?.dance_id?.let { viewModel?.getMusicByDanceId(it) }

    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.ALBUM_ID
    )

    val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
    val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

    context.contentResolver.query(
        collection,
        projection,
        selection,
        null,
        sortOrder
    )?.use { cursor ->
        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
        val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idCol)
            val name = cursor.getString(nameCol)
            val albumId = cursor.getLong(albumIdCol)

            val songUri = ContentUris.withAppendedId(collection, id)
            val albumArtUri = ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/albumart"),
                albumId
            )

            audioList.add(AudioFile(songUri, name, mappedMusic?.any { it.file_path == songUri.toString() } ?: false, albumArtUri))
        }
    }

    return audioList
}

fun getAudioFilesFromUris(context: Context, uriStrings: List<MusicDanceMapping>): List<AudioFile> {
    val resolver = context.contentResolver
    val audioFiles = mutableListOf<AudioFile>()

    uriStrings.forEach { music_dance_mapping ->
        val uri = Uri.parse(music_dance_mapping.file_path)

        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM_ID
        )

        resolver.query(uri, projection, null, null, null)?.use { cursor ->
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            if (cursor.moveToFirst()) {
                val title = cursor.getString(titleCol)
                val albumId = cursor.getLong(albumIdCol)

                val albumArtUri = Uri.parse("content://media/external/audio/albumart/$albumId")

                audioFiles.add(
                    AudioFile(
                        name = title,
                        uri = uri,
                        albumArt = albumArtUri
                    )
                )
            }
        }
    }

    return audioFiles
}
