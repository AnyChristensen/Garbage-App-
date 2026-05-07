package dk.christensen.garbagefinalassignment.ui.Features.garbage

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import dk.christensen.garbagefinalassignment.R
import dk.christensen.garbagefinalassignment.ui.components.GarbageTopAppBar
import dk.christensen.garbagefinalassignment.ui.components.NavigationType
import dk.christensen.garbagefinalassignment.ui.navigation.AppRoute
import kotlinx.serialization.Serializable


@Serializable
object AffaldKbh : AppRoute

@Composable
fun AffaldKbhScreen(
    onUpClick: () -> Unit
) {
        Scaffold(
            topBar = {
                GarbageTopAppBar(
                    titleRes = R.string.web_view,
                    navigationType = NavigationType.BACK,
                    onUpClick = onUpClick
                )
            }
        ) { innerPadding ->
            AndroidView(
                modifier = Modifier.padding(innerPadding),
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        loadUrl("https://affald.kk.dk/")
                    }
                }
            )
        }
}
