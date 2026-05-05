package dk.christensen.garbagefinalassignment.domain



// Cares about the business logic


// Copied from Shopping App
// Data class that creates the Item object
// Data class that creates a Bin object
// Data class that creates a RecyclingStation object


import android.content.Context
import androidx.compose.ui.graphics.Color
import dk.christensen.garbagefinalassignment.R
import dk.christensen.garbagefinalassignment.data.ItemDto
import dk.christensen.garbagefinalassignment.data.BinDto
import kotlinx.coroutines.flow.first
import java.util.UUID




// Item object
data class Item(
    val id: String = UUID.randomUUID().toString(),
    val what: String,
    val where: String,
    val description: String = "",
    val deadline: String? = null,
    val imageUrl: String? = null
)


// A Bin object
data class Bin(
    val name: String,
    val nameDanish: String,
    val imageUrl: String,
    val brandColor: Color,
    val lastPickupTime: Long? // timestamp (System.currentTimeMillis())
)


// For translating the API response into a RecyclingStation object
data class RecyclingStation(
    val id: String,
    val name: String,
    val category: String,
    val address: String,
    val status: String,
    val bins: List<String>,
    val latitude: Double,
    val longitude: Double,
){

    /*fun isCurrentlyOpen(): Boolean {
    val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return currentHour in openingHour until closingHour
}*/
}


// Themes objects for the user settings
enum class Theme {
    SYSTEM,
    LIGHT,
    DARK
}


fun Item.toDto(): ItemDto = ItemDto(id = this.id, what = this.what, where = this.where, description = this.description, deadline = this.deadline, imageUrl = this.imageUrl)

fun Bin.toDto(): BinDto = BinDto(name = this.name, imageUrl = this.imageUrl, brandColor = this.brandColor.value.toString())

// Does GarbageStation and Bins need to be one object?
// latitude = this.latitude, longitude = this.longitude, openingHour = this.openingHour, closingHour = this.closingHour)

// Function that finds all the bins that needs to be emptied
fun getOverdueBins (bins: List<Bin>):List<Bin>{

    // The current number of milliseconds since jan. 1970
    val currentTime = System.currentTimeMillis()

    // Calculating when there has been more than a week (7 days)
    val sevenDaysInMilliSec = 7 * 24 * 60 * 60 * 1000L

    return bins.filter { bin ->
        // previously emptied bin AND not emptied over 7 days
        bin.lastPickupTime != null && (currentTime-bin.lastPickupTime) > sevenDaysInMilliSec
    }
}


fun Item.fullDescription(context: Context): String =
    context.getString(R.string.list_item_label, this.what.lowercase(), this.where)

// Without using context
// fun Item.fullDescription(): String = "${this.what} should be placed in: ${this.where}"

