package com.adamford.eng.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.adamford.eng.ui.theme.DOGuessTheme


@Preview(showBackground = true, name = "DogGuess - Loading")
@Composable
fun DogGuessLoadingPreview() {
    // Initialize DogModel with loading state
    val loadingModel = DogModel(
        loading = true,
        randomDogImageUrl = null,
        breed = null,
        options = emptyList(),
        errorMessage = null
    )

    DOGuessTheme {
        DogGuessView(dogModel = loadingModel)
    }
}

@Preview(showBackground = true, name = "DogGuess - Error")
@Composable
fun DogGuessErrorPreview() {
    val errorModel = DogModel(
        loading = false,
        randomDogImageUrl = null,
        breed = null,
        options = emptyList(),
        errorMessage = "Failed to load data. Please try again."
    )

    DOGuessTheme {
        DogGuessView(dogModel = errorModel)
    }
}

@Preview(showBackground = true, name = "DogGuess - Success")
@Composable
fun DogGuessSuccessPreview() {
    // Initialize DogModel with success state
    val successModel = DogModel(
        loading = false,
        randomDogImageUrl = "https://images.dog.ceo/breeds/hound-afghan/n02088094_1003.jpg",
        breed = "Afghan Hound",
        options = listOf("Bulldog", "Afghan Hound", "Poodle", "Beagle"),
        errorMessage = null
    )

    DOGuessTheme {
        DogGuessView(dogModel = successModel)
    }
}

@Preview(showBackground = true, name = "DogGuess - Success with Correct Guess")
@Composable
fun DogGuessSuccessCorrectGuessPreview() {
    // Initialize DogModel with success state
    val successModel = DogModel(
        loading = false,
        randomDogImageUrl = "https://images.dog.ceo/breeds/hound-afghan/n02088094_1003.jpg",
        breed = "Afghan Hound",
        options = listOf("Bulldog", "Afghan Hound", "Poodle", "Beagle"),
        errorMessage = null
    )

    DOGuessTheme {
        DogGuessView(
            dogModel = successModel,
            isGuessCorrect = true,
            selectedOption = "Afghan Hound"
        )
    }
}
