package mikufan.cx.conduit.frontend.ui.screen.main.me

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import mikufan.cx.conduit.frontend.logic.component.main.me.MeNavComponent
import mikufan.cx.conduit.frontend.logic.component.main.me.MeNavComponentChild


@Composable
fun MeNavPage(meNavComponent: MeNavComponent, modifier: Modifier = Modifier) {

  val childStack by meNavComponent.childStack.subscribeAsState()

  AnimatedContent(
    targetState = childStack.active.instance,
    modifier = modifier,
    content = { child ->
      when (child) {
        is MeNavComponentChild.RootMeComponent -> MePage()
      }
    }
  )

}