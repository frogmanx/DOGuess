package com.adamford.eng.backend

import retrofit2.http.GET
import retrofit2.http.Path

interface DogService {

    @GET("/api/breed/{breed}/images/random")
    suspend fun getRandomDogImage(@Path("breed") breed: String): RandomDog

    @GET("/api/breeds/list/all")
    suspend fun getDogBreeds(): DogBreeds

}
