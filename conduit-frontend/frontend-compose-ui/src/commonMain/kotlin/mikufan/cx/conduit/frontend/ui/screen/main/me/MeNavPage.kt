package mikufan.cx.conduit.frontend.ui.screen.main.me

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import mikufan.cx.conduit.frontend.logic.component.main.me.MeNavComponent
import mikufan.cx.conduit.frontend.logic.component.main.me.MeNavComponentChild
import mikufan.cx.conduit.frontend.ui.util.fadeInAndOut


@Composable
fun MeNavPage(meNavComponent: MeNavComponent, modifier: Modifier = Modifier) {

  val childStack by meNavComponent.childStack.subscribeAsState()

  AnimatedContent(
    targetState = childStack.active.instance,
    modifier = modifier,
    transitionSpec = { fadeInAndOut() },
    content = { child ->
      when (child) {
        is MeNavComponentChild.MePage -> MePage(child.mePageComponent)
        is MeNavComponentChild.EditProfile -> EditProfilePage(child.editProfileComponent)
        is MeNavComponentChild.AddArticle -> AddArticlePage(child.addArticleComponent)
      }
    }
  )

}