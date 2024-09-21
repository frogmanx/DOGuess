package com.adamford.eng

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.adamford.eng.ui.DogViewModel
import com.adamford.eng.ui.theme.DOGuessTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: DogViewModel by viewModels()
        setContent {
            DOGuessTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(viewModel: DogViewModel, modifier: Modifier = Modifier) {
    val dogModel by viewModel.uiState.collectAsState()
    var text by remember { mutableStateOf("") }
    Column {
        Text(
            text = "Hello ${dogModel.randomDogImageUrl}",
            modifier = modifier
        )
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(dogModel.randomDogImageUrl)
                .build(),
            contentDescription = "complex image",
            contentScale = ContentScale.Crop,
        )
        TextField(
            value = text,
            onValueChange = { text = it },
            isError = text.toLowerCase(Locale.current) != dogModel.breed,
            label = { Text("Guess") },
        )
    }
}
