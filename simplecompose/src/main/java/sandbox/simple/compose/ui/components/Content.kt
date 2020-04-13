package sandbox.simple.compose.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import sandbox.simple.compose.ui.SandboxTheme
import java.util.*

@ExperimentalFoundationApi
@Composable
fun Content() {
    SandboxTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            Calendar(Locale.US)
        }
    }
}

@ExperimentalFoundationApi
@Preview(showBackground = true, name = "Content", showSystemUi = true)
@Composable
fun DefaultPreview() {
    Content()
}