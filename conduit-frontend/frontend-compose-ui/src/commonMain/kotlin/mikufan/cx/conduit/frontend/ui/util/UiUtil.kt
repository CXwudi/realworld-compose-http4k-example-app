package mikufan.cx.conduit.frontend.ui.util

import androidx.compose.runtime.Composable
import mikufan.cx.conduit.frontend.ui.theme.AppTheme
import mikufan.cx.conduit.frontend.ui.theme.WithProperSize

/**
 * Setup the base UI theme and sizing
 */
@Composable
fun SetupUI(
  content: @Composable () -> Unit
) {
  WithProperSize {
    AppTheme(content = content)
  }
}

/**
 * Right now it is just a wrapper of [SetupUI],
 *
 * but in the future, we may have special setup for just preview
 */
@Composable
fun SetupPreviewUI(
  content: @Composable () -> Unit
) = SetupUI(content = content)