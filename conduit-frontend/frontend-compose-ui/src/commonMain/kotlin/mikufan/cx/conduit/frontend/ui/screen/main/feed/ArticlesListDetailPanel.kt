package mikufan.cx.conduit.frontend.ui.screen.main.feed

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.panels.ChildPanels
import com.arkivanov.decompose.extensions.compose.experimental.panels.ChildPanelsAnimators
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.scale
import com.arkivanov.decompose.router.panels.ChildPanelsMode
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListDetailNavComponent
import mikufan.cx.conduit.frontend.ui.common.CustomHorizontalChildPanelsLayout


@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun ArticlesListDetailPanel(component: ArticlesListDetailNavComponent) {
  val mode = getPanelMode()
  LaunchedEffect(mode) {

    component.setWidestAllowedMode(mode.value)
  }
  ChildPanels(
    component.panels,
    mainChild = { ArticlesList() },
    detailsChild = { ArticleContent() },
    layout = remember { CustomHorizontalChildPanelsLayout(250.dp, 250.dp to 250.dp) },
    animators = ChildPanelsAnimators(single = fade() + scale(), dual = fade() to fade()),
  )
}

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun getPanelMode(): State<ChildPanelsMode> {
  val windowWidthSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
  val mode = remember(windowWidthSizeClass) {
    derivedStateOf {
      when (windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> ChildPanelsMode.SINGLE
        else -> ChildPanelsMode.DUAL
      }
    }
  }
  return mode
}
