package dk.christensen.garbagefinalassignment.domain

import kotlinx.coroutines.flow.Flow

interface BinRepository {
    fun getBins(): Flow<List<Bin>>
    suspend fun insertBins()
    suspend fun updateBin(bin: Bin)
}