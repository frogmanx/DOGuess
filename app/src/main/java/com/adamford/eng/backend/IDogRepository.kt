package com.adamford.eng.backend

import kotlinx.coroutines.flow.Flow

interface IDogRepository {
    fun getRandomDogImage(options: List<String>, breed: String): Flow<DogImage>
    fun getDogBreeds(): Flow<List<String>>
}