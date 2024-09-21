package com.adamford.eng.backend

import kotlinx.coroutines.flow.Flow

interface DogServiceHelper {
    fun getRandomDogImage(): Flow<String>

    fun getRandomDogImage(breed: String): Flow<String>

    fun getRandomDogBreed(): Flow<String?>
}