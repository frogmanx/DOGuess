package com.adamford.eng.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.adamford.eng.MainDispatcherRule
import com.adamford.eng.backend.DogImage
import com.adamford.eng.backend.IDogRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DogViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dogRepository: IDogRepository
    private lateinit var dogViewModel: DogViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)

        dogRepository = mockk()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `initial state is correct`() = runTest {
        every { dogRepository.getDogBreeds() } returns flowOf(listOf())
        dogViewModel = DogViewModel(dogRepository)
        dogViewModel.uiState.test {
            val initialState = awaitItem()
            assertThat(initialState.loading).isFalse()
            assertThat(initialState.randomDogImageUrl).isNull()
            assertThat(initialState.breed).isNull()
            assertThat(initialState.options).isEmpty()
            assertThat(initialState.errorMessage).isNull()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetchRaceSummaries emits loading true then data`() = runTest {
        val breeds = listOf("Bulldog", "Poodle", "Beagle")
        val dogImage = DogImage(
            breed = "Poodle",
            options = listOf("Poodle", "Bulldog", "Beagle"),
            url = "https://example.com/poodle.jpg"
        )

        every { dogRepository.getDogBreeds() } returns flowOf(breeds)
        every { dogRepository.getRandomDogImage(any(), any()) } returns flowOf(dogImage)

        dogViewModel = DogViewModel(dogRepository)
        dogViewModel.uiState.test {
            val initialState = awaitItem()
            assertThat(initialState.loading).isFalse()

            val loadingState = awaitItem()
            assertThat(loadingState.loading).isTrue()
            assertThat(loadingState.errorMessage).isNull()

            val dataState = awaitItem()
            assertThat(dataState.loading).isFalse()
            assertThat(dataState.randomDogImageUrl).isEqualTo(dogImage.url)
            assertThat(dataState.breed).isEqualTo(dogImage.breed)
            assertThat(dataState.options).isEqualTo(dogImage.options)
            assertThat(dataState.errorMessage).isNull()

            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 1) { dogRepository.getDogBreeds() }
        verify(exactly = 1) { dogRepository.getRandomDogImage(any(), any()) }
        confirmVerified(dogRepository)
    }

    @Test
    fun `fetchRaceSummaries emits loading true then error`() = runTest {
        val breeds = listOf("Bulldog", "Poodle", "Beagle")
        val exception = Exception("Network Error")

        every { dogRepository.getDogBreeds() } returns flowOf(breeds)
        every { dogRepository.getRandomDogImage(any(), any()) } returns flow {
            throw exception
        }

        dogViewModel = DogViewModel(dogRepository)

        dogViewModel.uiState.test {
            val initialState = awaitItem()
            assertThat(initialState.loading).isFalse()

            val loadingState = awaitItem()
            assertThat(loadingState.loading).isTrue()
            assertThat(loadingState.errorMessage).isNull()

            val errorState = awaitItem()
            assertThat(errorState.loading).isFalse()
            assertThat(errorState.errorMessage).isEqualTo("Network Error")

            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 1) { dogRepository.getDogBreeds() }
        verify(exactly = 1) { dogRepository.getRandomDogImage(any(), any()) }
        confirmVerified(dogRepository)
    }
}
