package com.adamford.eng.backend

import retrofit2.http.GET

interface DogService {

    @GET("/api/breeds/image/random")
    suspend fun getRandomDogImage(): RandomDog
}
