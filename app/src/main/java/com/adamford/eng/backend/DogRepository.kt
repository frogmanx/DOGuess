package com.adamford.eng.backend

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DogRepository @Inject constructor(private val dogService: DogService) :
    DogServiceHelper {

    override fun getRandomDogImage(): Flow<String>  = flow {
        val response = dogService.getRandomDogImage()
        emit(response.message)
    }
}
