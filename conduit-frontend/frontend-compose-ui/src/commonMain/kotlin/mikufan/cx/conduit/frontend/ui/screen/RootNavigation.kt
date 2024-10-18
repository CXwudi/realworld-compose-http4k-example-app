package mikufan.cx.conduit.frontend.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import mikufan.cx.conduit.frontend.logic.component.DefaultRootNavComponent
import mikufan.cx.conduit.frontend.logic.component.RootComponentChild
import mikufan.cx.conduit.frontend.ui.screen.main.MainNavPage

@Composable
fun RootNavigation(rootComponent: DefaultRootNavComponent, modifier: Modifier = Modifier) {
  val childStack by rootComponent.childStack.subscribeAsState()

  Crossfade(childStack.active.instance) {
    when (it) {
      is RootComponentChild.Loading -> LoadingScreen()
      is RootComponentChild.LandingPage -> LandingPage(it.component)
      is RootComponentChild.MainPage -> MainNavPage(it.component)
    }
  }
}

//@Composable
//private fun <S> AnimatedContentTransition(
//  targetState: S,
//  modifier: Modifier = Modifier,
//  content: @Composable AnimatedContentScope.(targetState: S) -> Unit
//) {
//  AnimatedContent(
//    targetState = targetState,
//    modifier = modifier,
//    transitionSpec = {
//      slideIntoContainer(
//        towards = SlideDirection.Left,
//        animationSpec = tween(),
//      ).togetherWith(
//        slideOutOfContainer(
//          towards = SlideDirection.Left,
//          animationSpec = tween()
//        )
//      )
//    },
//    content = content
//  )
//}
