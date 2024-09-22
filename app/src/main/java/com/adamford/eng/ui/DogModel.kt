package com.adamford.eng.ui

data class DogModel(
    val loading: Boolean = false,
    val randomDogImageUrl: String? = null,
    val breed: String? = null,
    val options: List<String> = listOf(),
    val errorMessage: String? = null,
)
