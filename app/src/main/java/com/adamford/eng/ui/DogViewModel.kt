package com.adamford.eng.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamford.eng.backend.DogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogViewModel @Inject constructor(private val dogRepository: DogRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DogModel())
    val uiState: StateFlow<DogModel> = _uiState.asStateFlow()

    init {
        fetchRaceSummaries()
    }

    private fun fetchRaceSummaries() {
        viewModelScope.launch {
            _uiState.update { dogModel ->
                dogModel.copy(loading = true)
            }
            dogRepository.getRandomDogBreed()
                .flowOn(Dispatchers.IO)
                .catch { e ->
                    //Todo: Add error state
                }
                .collect { breed ->
                    breed?.let {
                        dogRepository.getRandomDogImage(breed)
                            .flowOn(Dispatchers.IO)
                            .catch { e ->
                                //Todo: Add error state
                            }
                            .collect {
                                _uiState.update { dogModel ->
                                    dogModel.copy(
                                        loading = false,
                                        randomDogImageUrl = it,
                                        breed = breed
                                    )
                                }
                            }
                    }
                }
        }
    }
}