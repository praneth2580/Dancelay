package com.mrsomething.daneplay

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrsomething.daneplay.data.entity.DanceDef
import com.mrsomething.daneplay.data.entity.MusicDanceMapping
import com.mrsomething.daneplay.data.model.DanceViewModel
import com.mrsomething.daneplay.data.model.MusicDanceMappingViewModel
import com.mrsomething.daneplay.ui.theme.DancelayTheme
import com.mrsomething.daneplay.utils.AudioFile
import com.mrsomething.daneplay.utils.MusicQueuePlayer
import com.mrsomething.daneplay.utils.getAudioFilesFromUris
import kotlin.math.abs

class MusicPlayerActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dance_id = intent.getIntExtra("dance_id", -1)

        setContent {
            MaterialTheme {

                val danceViewModel: DanceViewModel = viewModel()
                val musicDanceViewModel: MusicDanceMappingViewModel = viewModel()

                val context = LocalContext.current

                var musics by remember { mutableStateOf<List<MusicDanceMapping>>(emptyList()) }
                var dance by remember { mutableStateOf<DanceDef?>(null) }
                var audios by remember { mutableStateOf<List<AudioFile>>(emptyList()) }

                LaunchedEffect(Unit) {
                    musics = musicDanceViewModel.getMusicByDanceId(dance_id)
                    dance = danceViewModel.getDance(dance_id)
                    audios = getAudioFilesFromUris( context, musics)
                }

                val musicPlayer = MusicQueuePlayer(context, audios)

                MusicControlGrid(
                    onNext = { musicPlayer.nextSong() },
                    onPrev = { musicPlayer.prevSong() },
                    onPlay = { if(musicPlayer.isPlaying) musicPlayer.pause() else musicPlayer.play()},
                    onValueCalc = { sec ->
                        if (sec > 0) {
                            musicPlayer.skipForward(sec)
                        } else {
                            musicPlayer.skipBackward(abs(sec))
                        }
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
fun MusicControlGrid(
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onPlay: () -> Unit,
    onValueCalc: (amount: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Column (
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("AAAAAAAAAAAAAAAAAAAAAAAAAa")
            Text("AAAAAAAAAAAAAAAAAAAAAAAAAa")
            Text("AAAAAAAAAAAAAAAAAAAAAAAAAa")
            Text("AAAAAAAAAAAAAAAAAAAAAAAAAa")
        }

        // Next Song
        FullWidthButton(text = "NEXT SONG", onClick = onNext)

        // +10 Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SquareButton(text = "+ 10", onClick = { onValueCalc(10) }, modifier = Modifier.weight(1f))
            SquareButton(text = "+ 20", onClick = { onValueCalc(20) }, modifier = Modifier.weight(1f))
            SquareButton(text = "+ 40", onClick = { onValueCalc(40) }, modifier = Modifier.weight(1f))
        }

        // Play Button
        FullWidthButton(text = "PLAY", onClick = onPlay)

        // Another +10 Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SquareButton(text = "- 10", onClick = { onValueCalc(-10) }, modifier = Modifier.weight(1f))
            SquareButton(text = "- 20", onClick = { onValueCalc(-20) }, modifier = Modifier.weight(1f))
            SquareButton(text = "- 40", onClick = { onValueCalc(-40) }, modifier = Modifier.weight(1f))
        }

        // Previous Song
        FullWidthButton(text = "PREV SONG", onClick = onPrev)
    }
}

@Composable
fun FullWidthButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB6B6)),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Text(text = text, color = Color.Black)
    }
}

@Composable
fun SquareButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB6B6)),
        modifier = modifier
            .aspectRatio(1f)
    ) {
        Text(text = text, color = Color.Black)
    }
}

@Composable
fun MusicTitle(text: String, isPlaying: Boolean) {

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DancelayTheme {
        MusicControlGrid(
            onNext = { /* Handle next */ },
            onPrev = { /* Handle prev */ },
            onPlay = { /* Handle play */ },
            onValueCalc = { /* Handle +10 */ }
        )
    }
}