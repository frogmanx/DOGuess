package com.adamford.eng

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.adamford.eng.ui.DogGuess
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
                DogGuess(viewModel = viewModel)
            }
        }
    }
}
