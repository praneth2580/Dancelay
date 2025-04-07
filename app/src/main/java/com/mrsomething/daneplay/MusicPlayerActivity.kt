package com.mrsomething.daneplay

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrsomething.daneplay.ui.theme.DancelayTheme

class MusicPlayerActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val audioUri = intent.getStringExtra("audio_uri")?.let { Uri.parse(it) }
        val songName = intent.getStringExtra("song_name") ?: "Unknown"

        setContent {
            MaterialTheme {
                MusicPlayerScreen(songName, audioUri,
                    onPlay = {
                        if (audioUri != null) {
                            mediaPlayer?.release()
                            mediaPlayer = MediaPlayer.create(this, audioUri)
                            mediaPlayer?.start()
                        }
                    },
                    onPause = { mediaPlayer?.pause() },
                    onStop = {
                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        mediaPlayer = null
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}

@Composable
fun MusicPlayerScreen(
    songName: String,
    uri: Uri?,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Now Playing:", style = MaterialTheme.typography.titleMedium)
        Text(text = songName, style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = onPlay) { Text("▶ Play") }
            Button(onClick = onPause) { Text("⏸ Pause") }
            Button(onClick = onStop) { Text("⏹ Stop") }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DancelayTheme {
        Greeting("Android")
    }
}