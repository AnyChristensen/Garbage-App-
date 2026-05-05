package dk.christensen.garbagefinalassignment.data.injection

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dk.christensen.garbagefinalassignment.data.remote.RecyclingStationApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton


// Setting up Retrofit (the HTTP client)

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(json: Json): Retrofit {
        return Retrofit.Builder()
            // The base URL for API-calls
            .baseUrl(/* baseUrl = */ "https://wfs-kbhkort.kk.dk/")
            .addConverterFactory(/* factory = */ json.asConverterFactory(contentType = "application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideRecyclingStationApiService(retrofit: Retrofit): RecyclingStationApiService {
        // Binds the interface (API endpoints) to Retrofit
        return retrofit.create(RecyclingStationApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideJson(): Json {
        // ignoreUnKnownKeys to ignore elements in the Json, that is not needed in our data class
        return Json { ignoreUnknownKeys = true }
    }
}


