package dk.christensen.garbagefinalassignment.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dk.christensen.garbagefinalassignment.domain.Item
import dk.christensen.garbagefinalassignment.domain.Bin
import java.util.UUID

class BinProvider : PreviewParameterProvider<Bin> {
    override val values = sequenceOf(
        Bin(
            name = "Netto",
            nameDanish = "Netto",
            imageUrl = "https://tse2.mm.bing.net/th/id/OIP.zLTC9xtHFbG3qlBlpreHLgHaHa",
            brandColor = Color(color = 0xFFfbdc12),
            lastPickupTime = System.currentTimeMillis()
        ),
        Bin(
            name = "Rema1000",
            nameDanish = "Rema1000",
            imageUrl = "https://logowik.com/content/uploads/images/rema-10007971.logowik.com.webp",
            brandColor = Color(color = 0xFF023ea5),
            lastPickupTime = System.currentTimeMillis()
        ),
        Bin(
            name = "Kvickly",
            nameDanish = "Kvickly",
            imageUrl = "https://via.ritzau.dk/data/images/00354/3718f3a8-2a42-4efd-b039-fde93c537ba7.png",
            brandColor = Color(color = 0xFFc50e20),
            lastPickupTime = System.currentTimeMillis()
        ),
        Bin(
            name = "SuperBrugsen",
            nameDanish = "SuperBrugsen",
            imageUrl = "https://banner2.cleanpng.com/20181112/gog/kisspng-superbrugsen-esbjerg-leader-esbjerg-storcenter-dag-5bea05f7ef56e6.6004540715420636079803.jpg",
            brandColor = Color(color = 0xFFc31315),
            lastPickupTime = System.currentTimeMillis()
        ),
        Bin(
            name = "365Discount",
            nameDanish = "",
            imageUrl = "https://tse1.mm.bing.net/th/id/OIP.HW3Q43OHW-EHjvYIi8XNnwHaHa",
            brandColor = Color(color = 0xFF086036),
            lastPickupTime = System.currentTimeMillis()
        ),
        Bin(
            name = "Føtex",
            nameDanish = "",
            imageUrl = "https://www.legout.dk/wp-content/uploads/2024/02/275850_Ftex.png",
            brandColor = Color(color = 0xFF0e223b),
            lastPickupTime = System.currentTimeMillis()
        ),
        Bin(
            name = "Bilka",
            nameDanish = "",
            imageUrl = "https://foodpartners.dk/wp-content/uploads/2021/10/bilka_logo.png",
            brandColor = Color(color = 0xFF009fe3),
            lastPickupTime = System.currentTimeMillis()
        ),
        Bin(
            name = "Hart Bakery",
            nameDanish = "",
            imageUrl = "https://images.squarespace-cdn.com/content/v1/53a0099de4b03f568ef67cea/1596556236454-82QYXKM4AXJ2L1DPTAOF/Hart-logo.png",
            brandColor = Color(color = 0xFF000000),
            lastPickupTime = System.currentTimeMillis()
        )
    )
}

class BinOrNullProvider : PreviewParameterProvider<Bin?> {
    override val values = sequenceOf(
        Bin(
            name = "Netto",
            nameDanish = "",
            imageUrl = "https://tse2.mm.bing.net/th/id/OIP.zLTC9xtHFbG3qlBlpreHLgHaHa",
            brandColor = Color(color = 0xFFfbdc12),
            lastPickupTime = System.currentTimeMillis()
        ),
        null
    )
}

class ItemOrNullProvider : PreviewParameterProvider<Item?> {
    override val values = sequenceOf(
        Item(
            id = UUID.randomUUID().toString(),
            what = "Coffee",
            where = "Føtex",
            description = "The one Bob showed us - with the blue label."
        ),
        null
    )
}

class BooleanProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(
        true,
        false
    )
}

fun previewBins() = listOf(
    Bin(
        name = "Netto",
        nameDanish = "",
        imageUrl = "https://tse2.mm.bing.net/th/id/OIP.zLTC9xtHFbG3qlBlpreHLgHaHa",
        brandColor = Color(color = 0xFFfbdc12),
        lastPickupTime = System.currentTimeMillis()
    ),
    Bin(
        name = "Rema1000",
        nameDanish = "",
        imageUrl = "https://logowik.com/content/uploads/images/rema-10007971.logowik.com.webp",
        brandColor = Color(color = 0xFF023ea5),
        lastPickupTime = System.currentTimeMillis()
    ),
    Bin(
        name = "Kvickly",
        nameDanish = "",
        imageUrl = "https://via.ritzau.dk/data/images/00354/3718f3a8-2a42-4efd-b039-fde93c537ba7.png",
        brandColor = Color(color = 0xFFc50e20),
        lastPickupTime = System.currentTimeMillis()
    ),
    Bin(
        name = "SuperBrugsen",
        nameDanish = "",
        imageUrl = "https://banner2.cleanpng.com/20181112/gog/kisspng-superbrugsen-esbjerg-leader-esbjerg-storcenter-dag-5bea05f7ef56e6.6004540715420636079803.jpg",
        brandColor = Color(color = 0xFFc31315),
        lastPickupTime = System.currentTimeMillis()
    ),
    Bin(
        name = "365Discount",
        nameDanish = "",
        imageUrl = "https://tse1.mm.bing.net/th/id/OIP.HW3Q43OHW-EHjvYIi8XNnwHaHa",
        brandColor = Color(color = 0xFF086036),
        lastPickupTime = System.currentTimeMillis()
    ),
    Bin(
        name = "Føtex",
        nameDanish = "",
        imageUrl = "https://www.legout.dk/wp-content/uploads/2024/02/275850_Ftex.png",
        brandColor = Color(color = 0xFF0e223b),
        lastPickupTime = System.currentTimeMillis()
    ),
    Bin(
        name = "Bilka",
        nameDanish = "",
        imageUrl = "https://foodpartners.dk/wp-content/uploads/2021/10/bilka_logo.png",
        brandColor = Color(color = 0xFF009fe3),
        lastPickupTime = System.currentTimeMillis()
    ),
    Bin(
        name = "Hart Bakery",
        nameDanish = "",
        imageUrl = "https://images.squarespace-cdn.com/content/v1/53a0099de4b03f568ef67cea/1596556236454-82QYXKM4AXJ2L1DPTAOF/Hart-logo.png",
        brandColor = Color(color = 0xFF000000),
        lastPickupTime = System.currentTimeMillis()
    )
)

fun previewGarbageList() = listOf(
    Item(
        id = UUID.randomUUID().toString(),
        what = "Coffee",
        where = "Føtex",
        description = "The one Bob showed us - with the blue label."
    ),
    Item(
        id = UUID.randomUUID().toString(),
        what = "Carrots",
        where = "Netto",
        description = ""
    ),
    Item(
        id = UUID.randomUUID().toString(),
        what = "Milk",
        where = "SuperBrugsen",
        description = "Both sødmælk and lactose-free minimælk"
    ),
    Item(
        id = "deep-link-item",
        what = "Bread",
        where = "Hart Bakery",
        description = "Half-a-loaf and a chocolate bun (please!)"
    ),
    Item(
        id = UUID.randomUUID().toString(),
        what = "Butter",
        where = "Føtex",
        description = ""
    )
)

@Preview(name = "Light Mode", uiMode = UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "Dark Mode", uiMode = UI_MODE_NIGHT_YES, showBackground = true)
annotation class ThemedPreviews

