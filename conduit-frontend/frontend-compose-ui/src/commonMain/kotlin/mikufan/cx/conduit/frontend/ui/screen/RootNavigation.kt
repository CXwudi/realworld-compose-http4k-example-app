package mikufan.cx.conduit.frontend.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.ChildStack
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.stackAnimation
import mikufan.cx.conduit.frontend.logic.component.DefaultRootNavComponent
import mikufan.cx.conduit.frontend.logic.component.RootComponentChild
import mikufan.cx.conduit.frontend.ui.screen.main.MainNavPage

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun RootNavigation(rootComponent: DefaultRootNavComponent, modifier: Modifier = Modifier) {

  ChildStack(
    stack = rootComponent.childStack,
    modifier = modifier,
    animation = stackAnimation(fade() + scale())
  ) { child: Child.Created<Any, RootComponentChild> ->
    when (val instance = child.instance) {
      is RootComponentChild.Loading -> LoadingScreen()
      is RootComponentChild.LandingPage -> LandingPage(instance.component)
      is RootComponentChild.MainPage -> MainNavPage(instance.component)
    }
  }
}
