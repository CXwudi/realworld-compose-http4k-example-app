package mikufan.cx.conduit.frontend.ui.util

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.runtime.compositionLocalOf

val LocalWindowAdaptiveInfo = compositionLocalOf<WindowAdaptiveInfo> { error("No LocalWindowAdaptiveInfo provided") }