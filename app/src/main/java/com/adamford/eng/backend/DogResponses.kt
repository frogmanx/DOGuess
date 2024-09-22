package com.adamford.eng.backend

data class RandomDog(
    val message: String,
    val status: String,
)

data class DogBreeds(
    val message: Map<String, List<String>>,
    val status: String,
)

data class DogImage(
    val breed: String,
    val options: List<String>,
    val url: String,
)
