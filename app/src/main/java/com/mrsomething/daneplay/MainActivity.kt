package com.mrsomething.daneplay

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mrsomething.daneplay.ui.theme.DancelayTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mrsomething.daneplay.utils.AudioFile
import com.mrsomething.daneplay.utils.getAudioFiles
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
// Coil Image Loading
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mrsomething.daneplay.data.DanceViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DancelayTheme {

                var showForm by remember { mutableStateOf(false) }

                Scaffold(
                    topBar = { CenterAlignedTopAppBar(
                        title = { Text(text = "Dance") },
                        actions = {
                            IconButton(onClick = { showForm = true }) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Add Dance"
                                )
                            }
                        }
                    )},
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    if (showForm) {
                        DanceForm(modifier = Modifier.padding(innerPadding))
                    } else {
                        MusicGridScreen(this, modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun DanceForm(modifier: Modifier = Modifier) {
    val viewModel: DanceViewModel = viewModel()
    AddDanceForm(viewModel, modifier = modifier)
}

@Composable
fun AddDanceForm(viewModel: DanceViewModel, modifier: Modifier = Modifier) {
    var danceName by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Add New Dance", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = danceName,
            onValueChange = { danceName = it },
            label = { Text("Dance Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                if (danceName.isNotBlank()) {
                    viewModel.addDance(danceName)
                    danceName = ""
                    showSuccess = true
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add")
        }

        if (showSuccess) {
            Text(
                text = "✅ Dance added successfully!",
                color = Color.Green,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicGridScreen(context: Context, modifier: Modifier) {
    var audioFiles by remember { mutableStateOf<List<AudioFile>>(emptyList()) }

    LaunchedEffect(Unit) {
        audioFiles = getAudioFiles(context)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(audioFiles) { file ->
            MusicFileCard(
                context = context,
                file = file,
                modifier = Modifier
            )
        }
    }
}

@Composable
fun MusicFileCard(context: Context, file: AudioFile, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable {
                val intent = Intent(context, MusicPlayerActivity::class.java).apply {
                    putExtra("audio_uri", file.uri.toString())
                    putExtra("song_name", file.name)
                }
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = file.albumArt,
                contentDescription = file.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                fallback = painterResource(R.drawable.ic_music_placeholder),
                error = painterResource(R.drawable.ic_music_placeholder)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = file.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MusicFileCardPreview() {
    val dummyFile = AudioFile(
        uri = Uri.parse("content://media/external/audio/media/123"),
        name = "Florence and the Machine - How Big, How Blue, How Beautiful"
    )

    // Use LocalContext.current to simulate context
    val context = LocalContext.current

    MusicFileCard(context = context, file = dummyFile, modifier = Modifier.padding(16.dp))
}