package com.adamford.eng.backend

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class DogRepository @Inject constructor(private val dogService: DogService) {

    private val dogBreedsMutex = Mutex()

    private var dogBreeds: List<String> = emptyList()

    fun getRandomDogImage(options: List<String>, breed: String): Flow<DogImage>  = flow {
        val response = dogService.getRandomDogImage(breed)
        emit(DogImage(breed = breed, options = options, url = response.message))
    }

    fun getDogBreeds(): Flow<List<String>>  = flow {
        if (dogBreeds.isEmpty()) {
            val response = dogService.getDogBreeds()
            dogBreedsMutex.withLock {
                dogBreeds = response.message.keys.toList()
            }
        }
        dogBreedsMutex.withLock {
            emit(dogBreeds)
        }
    }
}
