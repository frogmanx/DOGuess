package com.adamford.eng.ui

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamford.eng.backend.DogImage
import com.adamford.eng.backend.DogRepository
import com.adamford.eng.backend.IDogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class DogViewModel @Inject constructor(private val dogRepository: IDogRepository) : ViewModel() {

    @VisibleForTesting val _uiState = MutableStateFlow(DogModel())
    val uiState: StateFlow<DogModel> = _uiState.asStateFlow()

    init {
        fetchRaceSummaries()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchRaceSummaries() {
        viewModelScope.launch {
            dogRepository.getDogBreeds()
                .flowOn(Dispatchers.IO)
                .filter { it.isNotEmpty() }
                .flatMapLatest { fetchRandomImageAndGenerateOptions(breeds = it) }
                .onStart {
                    _uiState.update { it.copy(loading = true, errorMessage = null) }
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            errorMessage = e.localizedMessage ?: "An unexpected error occurred"
                        )
                    }
                }
                .collect { dogImage ->
                    _uiState.update { dogModel ->
                        dogModel.copy(
                            loading = false,
                            randomDogImageUrl = dogImage.url,
                            breed = dogImage.breed,
                            options = dogImage.options,
                            errorMessage = null,
                        )
                    }
                }
        }
    }

    private fun fetchRandomImageAndGenerateOptions(breeds: List<String>): Flow<DogImage> {
        val correctBreed = breeds.random()
        val breedsRemaining = breeds.minus(correctBreed).toMutableList()
        val options: MutableList<String> = mutableListOf()
        options.add(correctBreed)
        for (i in 0..1) {
            breedsRemaining.randomOrNull()?.let {
                options.add(it)
                breedsRemaining.remove(it)
            }
        }
        return dogRepository.getRandomDogImage(
            options = options.toList().shuffled(),
            breed = correctBreed,
        )
    }

    fun nextRound() {
        _uiState.update { dogModel ->
            dogModel.copy(
                errorMessage = null,
                options = listOf(),
                randomDogImageUrl = null,
                breed = null
            )
        }
        fetchRaceSummaries()
    }

    fun checkGuess(guess: String): Boolean {
        return guess.equals(_uiState.value.breed, ignoreCase = true)
    }
}