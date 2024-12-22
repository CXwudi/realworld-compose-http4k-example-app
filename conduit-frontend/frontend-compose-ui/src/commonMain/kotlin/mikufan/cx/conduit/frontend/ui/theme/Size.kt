package mikufan.cx.conduit.frontend.ui.theme

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import mikufan.cx.conduit.frontend.ui.util.LocalWindowAdaptiveInfo

@Composable
fun WithProperSize(
  windowAdaptiveInfo : WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
  content: @Composable () -> Unit,
) {
  val spacing = SpacingDefaults.calculateSpacing(windowAdaptiveInfo.windowSizeClass)
  CompositionLocalProvider(
    LocalWindowAdaptiveInfo provides windowAdaptiveInfo,
    LocalSpace provides spacing,
    content = content
  )
}

@Composable
expect fun calculateWindowSizeClass(): WindowSizeClass