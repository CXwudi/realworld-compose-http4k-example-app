package mikufan.cx.conduit.frontend.ui.theme

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import mikufan.cx.conduit.frontend.ui.util.LocalWindowSize

@Composable
fun WithProperSize(
  windowSizeClass : WindowSizeClass = calculateWindowSizeClass(),
  content: @Composable () -> Unit,
) {
  val spacing = windowSizeClass.calculateSpace()
  CompositionLocalProvider(
    LocalWindowSize provides windowSizeClass,
    LocalSpace provides spacing,
    content = content
  )
}

@Composable
expect fun calculateWindowSizeClass(): WindowSizeClass