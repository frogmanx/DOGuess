package com.adamford.eng.backend


import app.cash.turbine.test
import com.adamford.eng.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DogRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var dogService: DogService
    private lateinit var dogRepository: DogRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)

        dogService = mockk()
        dogRepository = DogRepository(dogService)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getRandomDogImage emits DogImage on success`() = runTest {
        val breed = "Poodle"
        val options = listOf("Poodle", "Bulldog", "Beagle")
        val randomDogResponse = RandomDog(
            message = "https://example.com/poodle.jpg",
            status = "success"
        )
        val expectedDogImage = DogImage(
            url = randomDogResponse.message,
            breed = breed,
            options = options
        )
        coEvery { dogService.getRandomDogImage(breed) } returns randomDogResponse
        val flow = dogRepository.getRandomDogImage(options, breed)

        flow.test {
            val emission = awaitItem()
            assertThat(emission).isEqualTo(expectedDogImage)
            awaitComplete()
        }
        coVerify(exactly = 1) { dogService.getRandomDogImage(breed) }
        confirmVerified(dogService)
    }

    @Test
    fun `getRandomDogImage propagates exception on failure`() = runTest {
        val breed = "Poodle"
        val options = listOf("Poodle", "Bulldog", "Beagle")
        val exception = Exception("Network Error")
        coEvery { dogService.getRandomDogImage(breed) } throws exception
        val flow = dogRepository.getRandomDogImage(options, breed)

        flow.test {
            val thrown = awaitError()
            assertThat(thrown).isInstanceOf(Exception::class.java)
            assertThat(thrown).hasMessageThat().isEqualTo("Network Error")
        }
        coVerify(exactly = 1) { dogService.getRandomDogImage(breed) }
    }

    @Test
    fun `getDogBreeds emits breeds on success`() = runTest {
        val dogBreedsResponse = DogBreeds(
            message = mapOf(
                "bulldog" to listOf("boston", "english", "french"),
                "poodle" to listOf("miniature", "standard", "toy"),
                "beagle" to emptyList(),
                "labrador" to emptyList()
            ),
            status = "success"
        )
        val expectedBreeds = listOf("bulldog", "poodle", "beagle", "labrador")

        coEvery { dogService.getDogBreeds() } returns dogBreedsResponse
        val flow = dogRepository.getDogBreeds()
        flow.test {
            val emission = awaitItem()
            assertThat(emission).isEqualTo(expectedBreeds)
            awaitComplete()
        }
        coVerify(exactly = 1) { dogService.getDogBreeds() }
        confirmVerified(dogService)
    }

    @Test
    fun `getDogBreeds emits cached breeds on subsequent calls`() = runTest {
        val dogBreedsResponse = DogBreeds(
            message = mapOf(
                "bulldog" to listOf("boston", "english", "french"),
                "poodle" to listOf("miniature", "standard", "toy"),
                "beagle" to emptyList(),
                "labrador" to emptyList()
            ),
            status = "success"
        )
        val expectedBreeds = listOf("bulldog", "poodle", "beagle", "labrador")
        coEvery { dogService.getDogBreeds() } returns dogBreedsResponse
        val firstFlow = dogRepository.getDogBreeds()

        firstFlow.test {
            val emission = awaitItem()
            assertThat(emission).isEqualTo(expectedBreeds)
            awaitComplete()
        }
        val secondFlow = dogRepository.getDogBreeds()

        secondFlow.test {
            val emission = awaitItem()
            assertThat(emission).isEqualTo(expectedBreeds)
            awaitComplete()
        }
        coVerify(exactly = 1) { dogService.getDogBreeds() }
    }

    @Test
    fun `getDogBreeds propagates exception on failure`() = runTest {
        val exception = Exception("Network Error")

        coEvery { dogService.getDogBreeds() } throws exception
        val flow = dogRepository.getDogBreeds()

        flow.test {
            val thrown = awaitError()
            assertThat(thrown).isInstanceOf(Exception::class.java)
            assertThat(thrown).hasMessageThat().isEqualTo("Network Error")
        }

        coVerify(exactly = 1) { dogService.getDogBreeds() }
        confirmVerified(dogService)
    }
}
