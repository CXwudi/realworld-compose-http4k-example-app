package mikufan.cx.conduit.frontend.ui.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.panels.ChildPanels
import com.arkivanov.decompose.router.panels.ChildPanelsMode
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mikufan.cx.conduit.frontend.logic.component.main.MainNavComponent
import mikufan.cx.conduit.frontend.logic.component.main.MainNavComponentChild
import mikufan.cx.conduit.frontend.logic.component.main.MainNavIntent
import mikufan.cx.conduit.frontend.logic.component.main.MainNavMode
import mikufan.cx.conduit.frontend.logic.component.main.MainNavState
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListDetailNavComponent
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListDetailNavComponentChild
import mikufan.cx.conduit.frontend.ui.util.SetupPreviewUI


@ExperimentalDecomposeApi
val fakeArticlesListDetailComponent = object : ArticlesListDetailNavComponent {
  override val panels: Value<ChildPanels<*, ArticlesListDetailNavComponentChild.ArticlesList, *, ArticlesListDetailNavComponentChild.ArticleDetail, Unit, Unit>> =
    MutableValue(
      ChildPanels<Unit, ArticlesListDetailNavComponentChild.ArticlesList, Unit, ArticlesListDetailNavComponentChild.ArticleDetail, Unit, Unit>(
        Child.Created(
          configuration = Unit,
          ArticlesListDetailNavComponentChild.ArticlesList
        ),
        null,
        null,
        ChildPanelsMode.SINGLE
      )
    )

  override fun setWidestAllowedMode(mode: ChildPanelsMode) {}
}


@OptIn(ExperimentalDecomposeApi::class)
@Composable
@Preview
fun MainPagePreview() {
  SetupPreviewUI {
    val fakeComponent = object : MainNavComponent {
      override val childStack: Value<ChildStack<*, MainNavComponentChild>> = MutableValue(
        ChildStack(
          Child.Created(
            Unit, MainNavComponentChild.MainFeed(
              fakeArticlesListDetailComponent
            )
          )
        )
      )
      override val state: StateFlow<MainNavState> =
        MutableStateFlow(MainNavState(MainNavMode.NOT_LOGGED_IN, 0))

      override fun send(intent: MainNavIntent) = Unit
    }
    MainNavPage(component = fakeComponent)
  }
}

@OptIn(ExperimentalDecomposeApi::class)
@Composable
@Preview
fun MainPagePreviewForLoginUser() {
  SetupPreviewUI {
    val fakeComponent = object : MainNavComponent {
      override val childStack: Value<ChildStack<*, MainNavComponentChild>> = MutableValue(
        ChildStack(
          Child.Created(
            Unit, MainNavComponentChild.MainFeed(
              fakeArticlesListDetailComponent
            )
          )
        )
      )
      override val state: StateFlow<MainNavState> =
        MutableStateFlow(MainNavState(MainNavMode.LOGGED_IN, 0))

      override fun send(intent: MainNavIntent) = Unit
    }
    MainNavPage(component = fakeComponent)
  }
}