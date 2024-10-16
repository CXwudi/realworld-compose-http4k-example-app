package mikufan.cx.conduit.frontend.ui.theme

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable

@Composable
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
actual fun calculateWindowSizeClass(): WindowSizeClass = androidx.compose.material3.windowsizeclass.calculateWindowSizeClass()