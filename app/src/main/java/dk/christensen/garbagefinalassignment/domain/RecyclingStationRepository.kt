package dk.christensen.garbagefinalassignment.domain

import kotlinx.coroutines.flow.Flow


interface RecyclingStationRepository {
    fun getRecyclingStations(): Flow<List<RecyclingStation>>
}

