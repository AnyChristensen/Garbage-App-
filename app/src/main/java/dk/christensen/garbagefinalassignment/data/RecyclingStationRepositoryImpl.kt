package dk.christensen.garbagefinalassignment.data

import android.util.Log
import dk.christensen.garbagefinalassignment.data.remote.RecyclingStationApiService
import dk.christensen.garbagefinalassignment.domain.RecyclingStation
import dk.christensen.garbagefinalassignment.domain.RecyclingStationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okio.IOException
import retrofit2.http.HTTP
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.map
import kotlin.random.Random


@Singleton
    class RecyclingStationRepositoryImpl @Inject constructor(
        private val recyclingStationApiService: RecyclingStationApiService
    ) : RecyclingStationRepository {
    override fun getRecyclingStations(): Flow<List<RecyclingStation>> = flow {
        emit(fetchRecyclingStation())
    }


    private suspend fun fetchRecyclingStation(): List<RecyclingStation> {
        var attempts = 0
        val maxAttempts = 3
        var delayMillis = 1000L

        while (true) {
            try {
                return recyclingStationApiService.getRecyclingStation()
                    .features
                    .map { it.toDomain() }
            } catch (e: Exception) {
                // Handle 404 separately: don't retry, the recycling station does not exist.
                if (e is HttpException && e.code() == 404) {
                    Log.w("RecyclingStationRepositoryImpl", "Recycling Station not found (404)")
                    return emptyList()
                }
                // For other errors (including network errors), log and prepare to retry.
                val errorType =
                    if (e is IOException) "Network error (likely offline)" else "API error"
                Log.w(
                    "RecyclingStationRepositoryImpl",
                    "$errorType fetching recycling station. Attempt ${attempts + 1} of $maxAttempts."
                )
                attempts++
                if (attempts >= maxAttempts) {
                    return emptyList()
                }

                kotlinx.coroutines.delay(delayMillis)

                delayMillis *= 2 // Double the delay for the next attempt
                delayMillis += Random.nextLong(0, 1000) // Add jitter

                /*  if (attempts < maxAttempts) {
                 Log.d(
                     "RecyclingStationRepositoryImpl",
                     "Retrying in ${delayMillis / 1000} seconds..."
                 )
                 delayMillis *= 2 // Double the delay for the next attempt
                 delayMillis += Random.nextLong(from = 0, until = 1000) // Add jitter
             }

            */
            }
        }
    }
}


