package dk.christensen.garbagefinalassignment.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import dk.christensen.garbagefinalassignment.domain.BinRepository
import dk.christensen.garbagefinalassignment.domain.Bin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await


@Singleton
class BinRepositoryImpl @Inject constructor(private val  db: FirebaseFirestore) : BinRepository {
        private val binsCollection = db.collection(/* collectionPath = */ "bins" )

    // Retrieving the bins as a flow list of Bin objects
    override fun getBins(): Flow<List<Bin>> {
        return binsCollection.snapshots().map { snapshot ->
            snapshot.toObjects(/* clazz = */ BinDto::class.java)
                .map { it.toBin() }
                // Sorting the bin by name so the bins does not change order when lastPickupTime is changed
                .sortedBy {it.name }

        }
    }

    override suspend fun insertBins() {
            val bins = listOf(
                BinDto(
                    name = "Food Waste",
                    nameDanish = "Madaffald",
                    imageUrl = "https://affald.kk.dk/sites/default/files/styles/large/public/2024-04/Netv%C3%A6rk_madaffald_Web_500px6.jpg?itok=JtVQ1ZIW",
                    brandColor = "#00a04b",
                    lastPickupTime = null
                ),
                BinDto(
                    name = "Plastics",
                    nameDanish = "Plast og Mad- og drikkekartoner",
                    imageUrl = "https://cdn.prod.website-files.com/633fd4b071f8f56baf6d88c9/633fd4b071f8f5d2a86d8a04_PLAST_rgb_72dpi.jpg",
                    brandColor = "#961e82",
                    lastPickupTime = null
                ),
                BinDto(
                    name = "Glass",
                    nameDanish = "Glas",
                    imageUrl = "https://cdn.prod.website-files.com/633fd4b071f8f56baf6d88c9/633fda2d8e67ca67900e1a09_GLAS_rgb_72dpi.jpg",
                    brandColor = "#21b685",
                    lastPickupTime = null
                ),
                BinDto(
                    name = "Paper",
                    nameDanish = "Papir",
                    imageUrl = "https://cdn.prod.website-files.com/633fd4b071f8f56baf6d88c9/633fdb43d294ae564485d5a0_PAPIR_rgb_72dpi.jpg",
                    brandColor = "#0082be",
                    lastPickupTime = null
                ),
                BinDto(
                    name = "Cardboard",
                    nameDanish = "Pap",
                    imageUrl = "https://cdn.prod.website-files.com/633fd4b071f8f56baf6d88c9/633fdd1d337f1620e240ff30_PAP_rgb_72dpi.jpg",
                    brandColor = "#bea064",
                    lastPickupTime = null
                ),
                BinDto(
                    name = "Metal",
                    nameDanish = "Metal",
                    imageUrl = "https://cdn.prod.website-files.com/633fd4b071f8f56baf6d88c9/633fdd52d278a18c574354e1_METAL_rgb_72dpi.jpg",
                    brandColor = "#5a6e78",
                    lastPickupTime = null
                ),
                BinDto(
                    name = "Textile Waste",
                    nameDanish = "Tekstilaffald",
                    imageUrl = "https://cdn.prod.website-files.com/633fd4b071f8f56baf6d88c9/633fddfef9c6379888d26315_TEKSTILAFFALD%20rgb%2072dpi.jpg",
                    brandColor = "#a01e41",
                    lastPickupTime = null
                ),
                BinDto(
                    name = "Hazardous Waste",
                    nameDanish = "Farligt affald",
                    imageUrl = "https://cdn.prod.website-files.com/633fd4b071f8f56baf6d88c9/633fdf3d530bde43a3dc25ba_FARLIGT_AFFALD_rgb_72dpi.jpg",
                    brandColor = "#e10f1e",
                    lastPickupTime = null
                ),
                BinDto(
                    name = "Wood",
                    nameDanish = "Indendørs træ",
                    imageUrl = "https://cdn.prod.website-files.com/633fd4b071f8f56baf6d88c9/65aeb6bb7397ecb9ee1afff6_TRAE_rgb_72dpi.jpg",
                    brandColor = "#826428",
                    lastPickupTime = null
                ),

                BinDto(
                    name = "Residual Waste",
                    nameDanish = "Restaffald",
                    imageUrl = "https://cdn.prod.website-files.com/633fd4b071f8f56baf6d88c9/633fe03386c06e05046d0208_RESTAFFALD_rgb_72dpi.jpg",
                    brandColor = "#141414",
                    lastPickupTime = null
                ),

                BinDto(
                    name = "Batteries",
                    nameDanish = "Batterier",
                    imageUrl = "https://cdn.prod.website-files.com/633fd4b071f8f56baf6d88c9/6343b68a4d9033df76a2bf9f_BATTERIER_rgb_72dpi.jpg",
                    brandColor = "#e10f1e",
                    lastPickupTime = null
                ),
                BinDto(
                    name = "Electronics",
                    nameDanish = "Elektronik",
                    imageUrl = "https://cdn.prod.website-files.com/633fd4b071f8f56baf6d88c9/6343b1f1025b9f2da35f381f_ELEKTRONIK_rgb_72dpi.jpg",
                    brandColor = "#f0910a",
                    lastPickupTime = null
                ),
                BinDto(
                    name = "Bulky Waste",
                    nameDanish = "Stort indbo",
                    imageUrl = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fcms.vestfor.dk%2Fmedia%2Fwhngkhxr%2Fstorskrald.webp&f=1&nofb=1&ipt=2d97c8fddb44d4e7a601f1413053a4ef32489aec638901c9fc14c215255ccc99",
                    brandColor = "#141414",
                    lastPickupTime = null
                ),
                BinDto(
                    name = "Garden Waste",
                    nameDanish = "Haveaffald",
                    imageUrl = "https://affald.kk.dk/sites/default/files/2024-04/Haveaffald%20piktogram%20uden%20tekst%20600px.png",
                    brandColor = "#4CAF50",
                    lastPickupTime = null
                ),

                BinDto(
                    name = "Large Electronics",
                    nameDanish = "Stort elektronik",
                    imageUrl = "https://affald.kk.dk/sites/default/files/2024-04/Elektronik%20piktogram%20uden%20tekst%20600px.png",
                    brandColor = "#F57C00",
                    lastPickupTime = null
                )
            )

            // Adding all the bins to the database
            val batch = db.batch()
            bins.forEach { binDto ->
                val docRef =
                    binsCollection.document(/* documentPath = */ binDto.name) // Using name as ID
                batch.set(/* documentRef = */ docRef, /* data = */ binDto)
            }
            batch.commit().await()
        }


    override suspend fun updateBin(bin: Bin){
        binsCollection
            .document(bin.name)
            .update("lastPickupTime", bin.lastPickupTime)
            .await()
    }


}





