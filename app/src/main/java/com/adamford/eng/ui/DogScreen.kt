package com.adamford.eng.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.adamford.eng.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DogGuess(viewModel: DogViewModel) {
    val dogModel by viewModel.uiState.collectAsState()

    // State for showing feedback
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isGuessCorrect by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dog Breed Guessing Game") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            val errorMessage = dogModel.errorMessage
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(dimensionResource(id = R.dimen.spacing_2)),
                    textAlign = TextAlign.Center
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(id = R.dimen.spacing_2)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DogImage(
                        imageUrl = dogModel.randomDogImageUrl,
                        contentDescription = "Random Dog Image",
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_3)))

                    // Multiple-Choice Options
                    if (dogModel.options.isNotEmpty()) {
                        OptionsList(
                            options = dogModel.options,
                            selectedOption = selectedOption,
                            isGuessCorrect = isGuessCorrect,
                            onOptionSelected = { option ->
                                if (selectedOption == null) { // Prevent multiple selections
                                    selectedOption = option
                                    isGuessCorrect = viewModel.checkGuess(option)
                                }
                            }
                        )
                    }

                    // Handle delay and next round
                    LaunchedEffect(selectedOption) {
                        selectedOption?.let {
                            delay(2000)
                            viewModel.nextRound()
                            selectedOption = null
                            isGuessCorrect = false
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
    selectedOption: String?,
    isGuessCorrect: Boolean,
    onOptionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        options.forEach { option ->
            val isSelected = selectedOption == option
            val isCorrect = if (isSelected) isGuessCorrect else false

            OptionItem(
                optionText = option,
                isSelected = isSelected,
                isCorrect = isCorrect,
                onOptionSelected = onOptionSelected
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_1)))
        }
    }
}

@Composable
fun OptionItem(
    optionText: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    onOptionSelected: (String) -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected && isCorrect -> colorResource(id = R.color.correct_guess)
            isSelected && !isCorrect -> colorResource(id = R.color.incorrect_guess)
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(durationMillis = 300)
    )
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isSelected) { onOptionSelected(optionText) },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(id = R.dimen.elevation)
        ),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.spacing_1))
    ) {
        Text(
            text = optionText,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.spacing_2))
                .fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun DogImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = dimensionResource(id = R.dimen.spacing_2),
    backgroundColor: Color = MaterialTheme.colorScheme.background,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Box(
        modifier = modifier
            .size(screenWidth, screenWidth)
            .background(color = backgroundColor, shape = RoundedCornerShape(cornerRadius))
            .clip(RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.Center,
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
        } else {
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_2)))
            CircularProgressIndicator()
        }
    }
}