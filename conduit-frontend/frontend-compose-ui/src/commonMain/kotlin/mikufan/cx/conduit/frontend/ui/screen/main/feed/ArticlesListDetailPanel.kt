package mikufan.cx.conduit.frontend.ui.screen.main.feed

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.panels.ChildPanels
import com.arkivanov.decompose.extensions.compose.experimental.panels.ChildPanelsAnimators
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.slide
import com.arkivanov.decompose.router.panels.ChildPanelsMode
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListDetailNavComponent
import mikufan.cx.conduit.frontend.ui.common.CustomHorizontalChildPanelsLayout


@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun ArticlesListDetailPanel(component: ArticlesListDetailNavComponent) {
  val mode = rememberPanelMode()
  LaunchedEffect(mode) {
    component.setWidestAllowedMode(mode)
  }

  ChildPanels(
    panels = component.panels,
    mainChild = { ArticlesList(it.instance.component) },
    detailsChild = { detail -> ArticleContent(detail.instance.component) },
    layout = remember { CustomHorizontalChildPanelsLayout(250.dp, 250.dp to 250.dp) },
    // unfortunately, it would be really hard to do animation for mode switching.
    // as it requires custom measuring the dp value changing in the ChildPanelsLayout
    animators = ChildPanelsAnimators(single = fade() + slide(), dual = (fade() + scale()) to (fade() + slide())),
  )
}

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun rememberPanelMode(): ChildPanelsMode {
  val windowAdaptiveInfo = currentWindowAdaptiveInfo()
  val windowWidthSizeClass by remember(windowAdaptiveInfo) { derivedStateOf { windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass } }
  val mode by remember(windowWidthSizeClass) {
    derivedStateOf {
      when (windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> ChildPanelsMode.SINGLE
        else -> ChildPanelsMode.DUAL
      }
    }
  }
  return mode
}
