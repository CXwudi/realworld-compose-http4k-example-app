package mikufan.cx.conduit.frontend.ui.util

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import mikufan.cx.conduit.frontend.ui.theme.AppTheme
import mikufan.cx.conduit.frontend.ui.theme.WithProperSize


@Composable
fun SetupUI(
  content: @Composable () -> Unit
) {
  WithProperSize {
    AppTheme(content = content)
  }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun SetupPreviewUI(
  content: @Composable () -> Unit
) {
  WithProperSize(windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(600.dp, 900.dp))) {
    AppTheme(content = content)
  }
}