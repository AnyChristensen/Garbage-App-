package dk.christensen.garbagefinalassignment.domain

// Copied from Shopping App (added function)
// Interface for the Item Repository
// (modeling the data from the data layer to make it presentable in the UI layer)

import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getGarbageList(): Flow<List<Item>>

    //added from ShoppingV4
    fun getItem(id: String): Flow<Item?>

    suspend fun addItem(item: Item) // changed to suspend because of async call to database (different thread)
    suspend fun removeItem(item: Item) // changed to suspend because of async call to database (different thread)

    suspend fun updateItem(item:Item) // changed to suspend because of async call to database (different thread)

    suspend fun searchItem(userInput: String): Item? // returning an item (previous it was a flow list of items...)

    suspend fun insertExistingItems()

}
