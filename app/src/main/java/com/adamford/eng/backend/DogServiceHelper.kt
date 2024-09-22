package com.adamford.eng.backend

import kotlinx.coroutines.flow.Flow

interface DogServiceHelper {
    fun getRandomDogImage(): Flow<String>

    fun getRandomDogImage(options: List<String>, breed: String): Flow<DogImage>

    fun getDogBreeds(): Flow<List<String>>
}