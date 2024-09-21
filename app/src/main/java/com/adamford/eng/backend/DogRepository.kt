package com.adamford.eng.backend

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class DogRepository @Inject constructor(private val dogService: DogService) :
    DogServiceHelper {

    private val dogBreedsMutex = Mutex()

    private var dogBreeds: List<String> = emptyList()

    override fun getRandomDogImage(): Flow<String>  = flow {
        val response = dogService.getRandomDogImage()
        emit(response.message)
    }

    override fun getRandomDogImage(breed: String): Flow<String>  = flow {
        val response = dogService.getRandomDogImage(breed)
        emit(response.message)
    }

    override fun getRandomDogBreed(): Flow<String?>  = flow {
        if (dogBreeds.isEmpty()) {
            val response = dogService.getDogBreeds()
            dogBreedsMutex.withLock {
                dogBreeds = response.message.keys.toList().minus(Companion.MIX_KEY)
            }
        }
        dogBreedsMutex.withLock {
            emit(dogBreeds.randomOrNull())
        }
    }

    companion object {
        private const val MIX_KEY = "mix"
    }
}
