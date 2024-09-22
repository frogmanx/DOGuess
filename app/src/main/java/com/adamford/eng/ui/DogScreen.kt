package com.adamford.eng.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DogGuess(viewModel: DogViewModel) {
    val dogModel by viewModel.uiState.collectAsState()
    var guess by remember { mutableStateOf("") }

    // State for showing feedback
    var showFeedback by remember { mutableStateOf(false) }
    var isGuessCorrect by remember { mutableStateOf(false) }

    // State to prevent multiple selections
    var isOptionSelected by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dog Breed Guessing Game") }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            val errorMessage = dogModel.errorMessage
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DogImage(
                        imageUrl = dogModel.randomDogImageUrl,
                        contentDescription = "Random Dog Image",
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedVisibility(visible = showFeedback) {
                        Text(
                            text = if (isGuessCorrect) "Correct!" else "Wrong!",
                            color = if (isGuessCorrect) Color(0xFF4CAF50) else Color(0xFFF44336),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    if (dogModel.options.isNotEmpty() && !isOptionSelected) {
                        OptionsList(
                            options = dogModel.options,
                            onOptionSelected = { selectedOption ->
                                isGuessCorrect = viewModel.checkGuess(selectedOption)
                                showFeedback = true
                                isOptionSelected = true
                            }
                        )
                    }

                    // Handle feedback delay and next round
                    LaunchedEffect(showFeedback) {
                        if (showFeedback) {
                            delay(2000)
                            showFeedback = false
                            if (isGuessCorrect) {
                                viewModel.nextRound()
                                isOptionSelected = false
                            } else {
                                // For simplicity, we'll proceed to the next round
                                viewModel.nextRound()
                                isOptionSelected = false
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OptionsList(
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        options.forEach { option ->
            OptionItem(
                optionText = option,
                onOptionSelected = onOptionSelected
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun OptionItem(
    optionText: String,
    onOptionSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOptionSelected(optionText) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Text(
            text = optionText,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun DogImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    cornerRadius: Int = 16,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Box(
        modifier = modifier
            .size(screenWidth, screenWidth)
            .background(color = backgroundColor, shape = RoundedCornerShape(cornerRadius.dp))
            .clip(RoundedCornerShape(cornerRadius.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop, // Crop to fill the square
                modifier = Modifier
                    .fillMaxSize()
            )
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }

}