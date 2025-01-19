package mikufan.cx.conduit.frontend.ui.theme

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun WithProperSize(
  windowAdaptiveInfo : WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
  content: @Composable () -> Unit,
) {
  val spacing = SpacingDefaults.calculateSpacing(windowAdaptiveInfo.windowSizeClass)
  CompositionLocalProvider(
    LocalSpace provides spacing,
    content = content
  )
}