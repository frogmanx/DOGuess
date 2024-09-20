package com.adamford.eng.di

import com.adamford.eng.backend.DogRepository
import com.adamford.eng.backend.DogService
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@InstallIn(ViewModelComponent::class)
@Module
object DogModule {
    private const val BASE_URL = "https://dog.ceo"

    @Provides
    fun provideDogService(
    ): DogService {
        val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(DogService::class.java)
    }

    @Provides
    fun provideDogRepository(
        dogService: DogService
    ): DogRepository {
        return DogRepository(dogService)
    }
}
