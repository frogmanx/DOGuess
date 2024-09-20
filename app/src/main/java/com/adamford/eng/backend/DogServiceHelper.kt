package com.adamford.eng.backend

import kotlinx.coroutines.flow.Flow

interface DogServiceHelper {
    fun getRandomDogImage(): Flow<String>
}