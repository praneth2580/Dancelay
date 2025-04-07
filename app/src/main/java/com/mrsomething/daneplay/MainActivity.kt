package com.mrsomething.daneplay

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
// Coil Image Loading
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mrsomething.daneplay.data.entity.DanceDef
import com.mrsomething.daneplay.data.entity.MusicDanceMapping
import com.mrsomething.daneplay.data.model.DanceViewModel
import com.mrsomething.daneplay.data.model.MusicDanceMappingViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DancelayTheme {

                var current_page by remember { mutableStateOf("show-dance") }
                var current_dance: DanceDef? by remember { mutableStateOf(null) }
                val danceViewModel: DanceViewModel = viewModel()
                val musicDanceViewModel: MusicDanceMappingViewModel = viewModel()
                val dances by danceViewModel.allDances.collectAsState()
                val danceList: List<DanceDef> = dances

                Scaffold(
                    topBar = { CenterAlignedTopAppBar(
                        title = { Text(text = "Dance") },
                        navigationIcon = {
                            if (current_page != "show-dance") {
                                IconButton(onClick = { current_page = "show-dance" }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
                                        contentDescription = "Go Back"
                                    )
                                }
                            }
                        },
                        actions = {
                            if (current_page == "show-dance") {
                                IconButton(onClick = { current_page = "add-dance" }) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Add Dance"
                                    )
                                }
                            }
                        }
                    )},
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    if (current_page == "add-dance") {
                        DanceForm(modifier = Modifier.padding(innerPadding))
                    } else if (current_page == "music-dance") {
                        AddMusicDanceMappingForm(
                            viewModel = musicDanceViewModel,
                            danceList = danceList,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }  else {
                            DanceView(
                                openDanceMapping = { dance ->
                                    current_page = "music-dance"
                                    current_dance = dance
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
    //                        MusicGridScreen(this, modifier = Modifier.padding(innerPadding))
                        }
                    }
            }
        }
    }
}

@Composable
fun DanceView(openDanceMapping: (dance: DanceDef) -> Unit, modifier: Modifier = Modifier) {
    val viewModel: DanceViewModel = viewModel()
    val dances by viewModel.allDances.collectAsState()

    LazyColumn (
        modifier = modifier
    ) {
        items(dances) { dance ->
            Card (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp, vertical = 10.dp)
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = dance.name,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                    IconButton(onClick = { openDanceMapping(dance) }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit Dances"
                        )
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

@Composable
fun AddMusicDanceMappingForm(
    viewModel: MusicDanceMappingViewModel,
    danceList: List<DanceDef>,
    modifier: Modifier = Modifier
) {
    var selectedDance by remember { mutableStateOf<DanceDef?>(null) }
    var name by remember { mutableStateOf("") }
    var filePath by remember { mutableStateOf("") }
    var order by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var startTransitionType by remember { mutableStateOf("") }
    var startTransitionDuration by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var endTransitionType by remember { mutableStateOf("") }
    var endTransitionDuration by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Map Dance to Music", style = MaterialTheme.typography.titleLarge)

        // Dance dropdown
        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedTextField(
                value = selectedDance?.name ?: "",
                onValueChange = {},
                label = { Text("Select Dance") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            )
            DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
                danceList.forEach { dance ->
                    DropdownMenuItem(
                        text = { Text(dance.name) },
                        onClick = {
                            selectedDance = dance
                            expanded = false
                        }
                    )
                }
            }
            Spacer(
                Modifier
                    .matchParentSize()
                    .clickable { expanded = true }
            )
        }

        // Fields
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        OutlinedTextField(value = filePath, onValueChange = { filePath = it }, label = { Text("File Path") })
        OutlinedTextField(
            value = order,
            onValueChange = { order = it },
            label = { Text("Order") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(value = startTime, onValueChange = { startTime = it }, label = { Text("Start Time (ms)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        OutlinedTextField(value = startTransitionType, onValueChange = { startTransitionType = it }, label = { Text("Start Transition Type") })
        OutlinedTextField(value = startTransitionDuration, onValueChange = { startTransitionDuration = it }, label = { Text("Start Transition Duration (ms)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        OutlinedTextField(value = endTime, onValueChange = { endTime = it }, label = { Text("End Time (ms)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        OutlinedTextField(value = endTransitionType, onValueChange = { endTransitionType = it }, label = { Text("End Transition Type") })
        OutlinedTextField(value = endTransitionDuration, onValueChange = { endTransitionDuration = it }, label = { Text("End Transition Duration (ms)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        Button(
            onClick = {
                selectedDance?.let {
                    val mapping = MusicDanceMapping(
                        name = name,
                        file_path = filePath,
                        order = order.toIntOrNull() ?: 0,
                        start_time = startTime.toLongOrNull() ?: 0L,
                        start_transition_type = startTransitionType,
                        start_transition_duration = startTransitionDuration.toLongOrNull() ?: 0L,
                        end_time = endTime.toLongOrNull() ?: 0L,
                        end_transition_type = endTransitionType,
                        end_transition_duration = endTransitionDuration.toLongOrNull() ?: 0L,
                        dance_id = it.dance_id
                    )
                    viewModel.addMapping(mapping)

                    // Optional: Clear fields after submission
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Map")
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
        columns = GridCells.Fixed(3),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 5.dp, vertical = 0.dp),
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
//            .aspectRatio(1f)
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
            val isPreview = LocalInspectionMode.current
            val imageModifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(CircleShape);
            if (isPreview) {
                Image(
                    painter = painterResource(R.drawable.ic_music_placeholder),
                    contentDescription = file.name,
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop
                )
            } else {
                AsyncImage(
                    model = file.albumArt,
                    contentDescription = file.name,
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop,
                    fallback = painterResource(R.drawable.ic_music_placeholder),
                    error = painterResource(R.drawable.ic_music_placeholder)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = file.name,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                maxLines = 2,
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