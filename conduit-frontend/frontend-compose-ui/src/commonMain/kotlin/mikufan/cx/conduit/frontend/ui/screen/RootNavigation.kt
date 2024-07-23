package mikufan.cx.conduit.frontend.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import mikufan.cx.conduit.frontend.logic.component.DefaultRootNavComponent
import mikufan.cx.conduit.frontend.logic.component.RootComponentChild
import mikufan.cx.conduit.frontend.ui.screen.main.MainPage

@Composable
fun RootNavigation(rootComponent: DefaultRootNavComponent, modifier: Modifier = Modifier) {
  val childSlot by rootComponent.childSlot.subscribeAsState()

  AnimatedContentTransition(
    targetState = childSlot.child?.instance,
    modifier = modifier
  ) {
    when (it) {
      is RootComponentChild.Loading -> LoadingScreen()
      is RootComponentChild.LandingPage -> LandingPage(it.component)
      is RootComponentChild.MainPage -> MainPage(it.component)
      null -> error("Unexpected null child in childSlot")
    }
  }
}

@Composable
private fun <S> AnimatedContentTransition(
  targetState: S,
  modifier: Modifier = Modifier,
  content: @Composable AnimatedContentScope.(targetState: S) -> Unit
) {
  AnimatedContent(
    targetState = targetState,
    modifier = modifier,
    transitionSpec = {
      slideIntoContainer(
        towards = SlideDirection.Left,
        animationSpec = tween(),
      ).togetherWith(
        slideOutOfContainer(
          towards = SlideDirection.Left,
          animationSpec = tween()
        )
      )
    },
    content = content
  )
}
