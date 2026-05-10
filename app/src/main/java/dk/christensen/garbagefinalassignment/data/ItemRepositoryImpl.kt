package dk.christensen.garbagefinalassignment.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import dk.christensen.garbagefinalassignment.domain.Item
import dk.christensen.garbagefinalassignment.domain.ItemRepository
import dk.christensen.garbagefinalassignment.domain.toDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


// Creating an ItemRepository class that implements an interface (ItemRepository)
// Making it Singleton, such that only one instances is created
// Using inject (constructor) to let Hilt handle the dependencies
@Singleton
class ItemRepositoryImpl @Inject constructor(
    db: FirebaseFirestore) : ItemRepository {

    // Retrieving/establishing connection to the "items" Collection in firestone
    private val itemsCollection = db.collection("items")

    // Implementing the function getGarbageList() declared in the interface (ItemRepository)
    // Returning a sorted "Flow" list of Items (in to form used in the interface)
    override fun getGarbageList(): Flow<List<Item>> {
        return itemsCollection
            //.orderBy("where")
            //.orderBy("what")
            .snapshots()
            .map { snapshot ->
                snapshot
                    .toObjects(ItemDto::class.java)
                    .map { it.toItem() }
                    .sortedWith(compareBy({ it.where }, { it.what }))
            }
    }

    // Function to retrieve an item from the database on the id
    // (snapshot toObject -> ItemDto -> Item)
    override fun getItem(id: String): Flow<Item?> {
        return itemsCollection.document(id)
            .snapshots()
            .map { snapshot ->
                snapshot.toObject(ItemDto::class.java)?.toItem()
            }
    }

    // Function for adding an item to the database
    // Formates the item using the helper function toTitleCase()
    override suspend fun addItem(item: Item) {
        val formattedItem =
            item.copy(
                what = item.what.toTitleCase(),
                where = item.where.toTitleCase(),
            )
        val itemDto = formattedItem.toDto()
        itemsCollection.document(itemDto.id).set(itemDto).await()
    }


    // Function for removing an item from the database
    override suspend fun removeItem(item: Item) {
        itemsCollection.document(item.id).delete().await()
    }


    // Function for updating an item in the database
    // Formats the item using the helper function toTitleCase
    override suspend fun updateItem(item: Item) {
        val formattedItem =
            item.copy(what = item.what.toTitleCase(), where = item.where.toTitleCase())
        val itemDto = formattedItem.toDto()
        itemsCollection.document(itemDto.id).set(itemDto, SetOptions.merge()).await()
    }

    // Function to search in Firestore for an item by its "what" field.
    // The function returns the first matching item or null if no match is found (therefore : Item? (to allow null value))
    // The function also converts the itemDto into an Item object
    override suspend fun searchItem(userInput: String): Item? {
        val snapshot = itemsCollection
            .whereEqualTo("what", userInput.toTitleCase())
            .get()
            .await()

        return snapshot.documents
            .firstOrNull()
            ?.toObject(ItemDto::class.java)
            ?.toItem()

    }

    // To insert the already existing garbage items from the list of garbage Strings
    override suspend fun insertExistingItems() {
        val garbageStrings = listOf(
            "newspaper, paper",
            "magazine, paper" ,
            "milk carton, plastics",
            "shoe box, cardboard",
            "can, metal",
            "book, paper",
            "aluminium foil (clean), metal",
            "teddy bears, residual waste",
            "flower pot (plastic), residual waste",
            "cables, metal",
            "envelopes, paper",
            "detergents, plastics",
            "musical instrument, wood",
            "cookware, metal",
            "hammer, metal",
            "curtain clips, metal",
            "jars, glass",
            "carpets, bulky waste",
            "postcards, cardboard",
            "chips bag, residual waste",
            "tooth brush, plastics",
            "shampoo bottle, plastics",
            "capsule, metal",
            "needle, metal",
            "letter, paper",
            "plastic bottle, plastics",
            "meat, food waste",
            "clothes, textile waste",
            "cutlery, metal",
            "paint, paints",
            "chlorine, hazardous waste",
            "computer, electronics",
            "battery, batteries",
            "printer, electronics",
            "potato, food waste",
            "cabbage, food waste",
            "kale, food waste",
            "cauliflower, food waste",
            "onion, food waste",
            "beetroot, food waste",
            "celeriac, food waste",
            "cellery, food waste",
            "flour, food waste",
            "sugar, food waste",
            "rice, food waste"
        )

        // Using map function to convert each garbage string to a garbage item
        val items = garbageStrings.map {
            it.toGarbageItemDto()
        }

        Log.d("TEST", "Inserting items...")

        // Creating a batch (change/update) in firestone
        val batch = itemsCollection.firestore.batch()

        // Adding each item to firestore
        items.forEach { itemDto ->
            val docRef = itemsCollection.document(itemDto.id) // using the assigned id
            batch.set(docRef,itemDto)
        }

        batch.commit().await()
    }


}

