package dk.christensen.garbagefinalassignment.data

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import dk.christensen.garbagefinalassignment.domain.Item
import dk.christensen.garbagefinalassignment.domain.Bin
import dk.christensen.garbagefinalassignment.domain.RecyclingStation
import kotlinx.serialization.Serializable
import java.util.UUID
import com.google.firebase.firestore.DocumentId


data class ItemDto(
    // When reading from firestone, the id is set automatically
    @DocumentId
    val id: String = "", // UUID.randomUUID().toString(),
    val what: String = "",
    val where: String = "",
    val description: String = "",
    val deadline: String? = null,
    val imageUrl: String? = null
)

data class BinDto(
    val name: String = "",
    val nameDanish: String = "",
    val imageUrl: String = "",
    val brandColor: String = "",
    val lastPickupTime: Long? = null // String?
)


// Used AI to convert the GeoJSON into data classes

// The full response containing a list of feature objects
@Serializable
data class RecyclingStationResponse(
    val features: List<Feature>
)


// A Feature Object containing both a properties object and a geometry object of the Recycling Station
@Serializable
data class Feature(
    val type: String,
    val properties: Properties,
        val geometry: Geometry
)


// Object containing the properties of the Recycling Station
@Serializable
data class Properties(
    val id: Int,
    val navn: String,
    val kategori: String,
    val adresse: String,
    val status: String,
    val fraktioner: String? = null
)


// Object containing the coordinates of the Recycling Station
// Coordinates contains a list of list of double because:
// "coordinates": [ [12.58, 55.70] ] (in JSON)
@Serializable
data class Geometry(
    val type: String,
    val coordinates: List<List<Double>>
)

// Part of the JSON object from Kbh kort
// An object with a list of features objects which again contains a geometry object and a properties object
/* "type": "FeatureCollection",
"features": [
{
    "type": "Feature",
    "id": "genbrugsstation.1",
    "geometry": {
    "type": "MultiPoint",
    "coordinates": [
    [12.58807223, 55.70736942]
    ]
},
    "geometry_name": "wkb_geometry",
    "properties": {
    "id": 46,
    "navn": "Østerbrohuset Nærgenbrugsstation",
    "kategori": "Nærgenbrugsstation",
    "adresse": "Århusgade 103, 2100 København Ø",
    "status": "Eksisterende",
    "anvendelse": null,
    "bytte_dele_zone": null,
    "lukket": "Lukket onsdag, øvrige helligdage samt d. 24., 25., 26., 31. december og 1. januar",
    "aabningstider_1": "Mandag og tirsdag kl. 12.00–18.00\nTorsdag og fredag kl. 12.00–18.00",
    "aabningstider_2": "Lørdag og søndag kl. 10.00–16.00\n1. maj og 5. juni samt helligdage, der falder på en hverdag kl. 10.00–16.00",
    "aabningstider_3": null,
    "fraktioner": "Batterier, Elektronik, Farligt affald, Tekstilaffald, Metal, Pap, Papir, Plast og Mad- og drikkekartoner, Indendørs træ, Stort Indbo, Genbrug",
    "link": null,
    "reg_dato": "2023-09-03T22:00:00Z",
    "rettet_dato": "2026-01-27T09:07:40.764Z"
}
}, ......
*/


// FUNCTIONS TO CONVERT THE OBJECTS BETWEEN THE DIFFERENT LAYERS


fun ItemDto.toItem(): Item = Item(
    id = this.id,
    what = this.what,
    where = this.where,
    description = this.description,
    // deadline = this.deadline,
    // imageUrl = this.imageUrl
)

fun BinDto.toBin(): Bin = Bin(
    name = this.name,
    nameDanish = this.nameDanish,
    imageUrl = this.imageUrl,
    brandColor = Color(color = this.brandColor.toColorInt()),
    lastPickupTime = this.lastPickupTime
)




// Used AI to create this convert function
fun Feature.toDomain(): RecyclingStation {
    val coords = geometry.coordinates.firstOrNull()

    return RecyclingStation(
        id = properties.id.toString(),
        name = properties.navn,
        category = properties.kategori,
        address = properties.adresse,
        status = properties.status,
        bins = properties.fraktioner
            ?.split(",")
            ?.map { it.trim() }
            ?: emptyList(),
        latitude = coords?.getOrNull(1) ?: 0.0,
        longitude = coords?.getOrNull(0) ?: 0.0
    )

}


//-----------------------Helper functions---------------------------------------------
// Moved to here to make them accessible to the whole data layer

// Helper function to convert garbage strings into garbage ItemDto (objects for the data layer)
// Extension function (on String)
fun String.toGarbageItemDto(): ItemDto {
    val splitString = split(",")

    return ItemDto(
        id = UUID.randomUUID().toString(),
        what=splitString[0].toTitleCase(),
        where= splitString[1].toTitleCase())
}

// Helper function to trim and ignore different cases (e.g for user-typed garbage items)
fun String.toTitleCase(): String {
    return this.trim().split(" ").joinToString(separator = " ") { word ->
        word.lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

