package mikufan.cx.conduit.frontend.ui.screen.main.me

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.ChildStack
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.stackAnimation
import mikufan.cx.conduit.frontend.logic.component.main.me.MeNavComponent
import mikufan.cx.conduit.frontend.logic.component.main.me.MeNavComponentChild


@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun MeNavPage(meNavComponent: MeNavComponent, modifier: Modifier = Modifier) {

  ChildStack(
    stack = meNavComponent.childStack,
    modifier = modifier,
    animation = stackAnimation(fade() + scale())
  ) {
    when (val child = it.instance) {
      is MeNavComponentChild.MePage -> MePage(child.mePageComponent)
      is MeNavComponentChild.EditProfile -> EditProfilePage(child.editProfileComponent)
      is MeNavComponentChild.AddArticle -> AddArticlePage(child.addArticleComponent)
    }
  }
}