package dk.christensen.garbagefinalassignment.data.injection

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dk.christensen.garbagefinalassignment.data.ItemRepositoryImpl
import dk.christensen.garbagefinalassignment.data.BinRepositoryImpl
import dk.christensen.garbagefinalassignment.data.UserPreferencesRepositoryImpl
import dk.christensen.garbagefinalassignment.domain.ItemRepository
import dk.christensen.garbagefinalassignment.domain.BinRepository
import dk.christensen.garbagefinalassignment.domain.UserPreferencesRepository
import dk.christensen.garbagefinalassignment.data.RecyclingStationRepositoryImpl
import dk.christensen.garbagefinalassignment.domain.RecyclingStationRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindItemRepository(impl: ItemRepositoryImpl): ItemRepository

    @Singleton
    @Binds
    abstract fun bindBinRepository(impl: BinRepositoryImpl): BinRepository

    @Singleton
    @Binds
    abstract fun bindRecyclingStationRepository(impl: RecyclingStationRepositoryImpl): RecyclingStationRepository

    @Singleton
    @Binds
    abstract fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository
}


