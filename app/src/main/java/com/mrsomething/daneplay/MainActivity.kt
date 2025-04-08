package com.mrsomething.daneplay

// Coil Image Loading
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mrsomething.daneplay.data.entity.DanceDef
import com.mrsomething.daneplay.data.entity.MusicDanceMapping
import com.mrsomething.daneplay.data.model.DanceViewModel
import com.mrsomething.daneplay.data.model.MusicDanceMappingViewModel
import com.mrsomething.daneplay.ui.theme.DancelayTheme
import com.mrsomething.daneplay.utils.AudioFile
import com.mrsomething.daneplay.utils.getAudioFiles

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DancelayTheme {

                var current_page by remember { mutableStateOf("show-dance") }
                var current_dance: DanceDef? by remember { mutableStateOf(null) }
                val musicDanceViewModel: MusicDanceMappingViewModel = viewModel()

                BackHandler (
                    enabled = (current_page != "show-dance")
                ) {
                    current_page = "show-dance"
                }

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
                        if (current_dance == null) {
                            current_page = "show-dance"
                        } else {
                            AddMusicDanceMappingForm(
                                viewModel = musicDanceViewModel,
                                dance = current_dance!!,
                                context = this,
                                goBack = {
                                    current_page = "show-dance"
                                    current_dance = null
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }  else {
                            DanceView(
                                openDanceMapping = { dance ->
                                    current_page = "music-dance"
                                    current_dance = dance
                                },
                                openDancePlay = { dance ->
                                    val intent = Intent(this, MusicPlayerActivity::class.java).apply {
                                        putExtra("dance_id", dance.dance_id)
                                    }
                                    startActivity(intent)
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
fun DanceView(openDanceMapping: (dance: DanceDef) -> Unit, openDancePlay: (dance: DanceDef) -> Unit, modifier: Modifier = Modifier) {
    val viewModel: DanceViewModel = viewModel()
    val dances by viewModel.allDances.collectAsState()

    LazyColumn (
        modifier = modifier
            .padding(horizontal = 5.dp, vertical = 10.dp)
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
                    Row {

                        IconButton(onClick = { openDanceMapping(dance) }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Dances"
                            )
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        IconButton(onClick = { openDancePlay(dance) }) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = "Play Dances"
                            )
                        }
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
    dance: DanceDef,
    goBack: () -> Unit,
    context: Context,
    modifier: Modifier = Modifier
) {
    var audioFiles by remember { mutableStateOf<List<AudioFile>>(emptyList()) }
    var anySelected by remember { mutableStateOf(false) }
    var nextOrder by remember { mutableStateOf(0) }

    LaunchedEffect(dance.dance_id) {
        nextOrder = viewModel.getNextOrder(dance.dance_id)
    }

    LaunchedEffect(Unit) {
        audioFiles = getAudioFiles(context, dance, viewModel)
        anySelected = audioFiles.any { it.selected }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Map Music to ${dance.name}", style = MaterialTheme.typography.titleLarge)
        }

        item {
            Spacer(Modifier.height(8.dp))
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp), // ⬅️ important to constrain height
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(audioFiles) { file ->
                    MusicFileCardSelector(
                        file = file,
                        onClick = {
                            file.selected = !file.selected
                            anySelected = audioFiles.any { it.selected }
                        },
                        modifier = Modifier
                    )
                }
            }
        }

        // You can add the button below as another item
        item {
            if (anySelected) {
                Button(
                    onClick = {
                        audioFiles.forEach { file ->
                            var mappings = mutableListOf<MusicDanceMapping>()
                            if (file.selected) {
                                val mapping = MusicDanceMapping(
                                    name = file.name,
                                    file_path = file.uri.toString(),
                                    order = nextOrder,
                                    dance_id = dance.dance_id
                                )
                                mappings.add(mapping)
//                                viewModel.addMapping(mapping)
                                nextOrder++
                            }
                            viewModel.addMapping(dance, mappings)
                            goBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("Map")
                }
            }
        }
    }
}


@Composable
fun MusicFileCardSelector(file: AudioFile, onClick: () -> Unit, modifier: Modifier = Modifier) {

    var isSelected by remember { mutableStateOf(file.selected) }

    val defaultCardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    )
    val selectedCardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )


    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                isSelected = !isSelected
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = if (isSelected) selectedCardColors else defaultCardColors,
        elevation = CardDefaults.cardElevation(if (file.selected) 8.dp else 2.dp),
        border = if (file.selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
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