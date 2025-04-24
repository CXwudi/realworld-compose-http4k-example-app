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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import mikufan.cx.conduit.frontend.logic.component.main.MainNavComponent
import mikufan.cx.conduit.frontend.logic.component.main.MainNavComponentChild
import mikufan.cx.conduit.frontend.logic.component.main.MainNavIntent
import mikufan.cx.conduit.frontend.logic.component.main.MainNavMode
import mikufan.cx.conduit.frontend.logic.component.main.MainNavState
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListComponent
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListDetailNavComponent
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListDetailNavComponentChild
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListIntent
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListLabel
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListState
import mikufan.cx.conduit.frontend.ui.util.SetupPreviewUI

@ExperimentalDecomposeApi
val fakeArticlesListComponent = object : ArticlesListComponent {
  override val state: StateFlow<ArticlesListState> = MutableStateFlow(ArticlesListState())
  override val labels: Flow<ArticlesListLabel> = flow {}

  override fun send(intent: ArticlesListIntent) {}

}

@ExperimentalDecomposeApi
val fakeArticlesListDetailComponent = object : ArticlesListDetailNavComponent {
  override val panels: Value<ChildPanels<*, ArticlesListDetailNavComponentChild.ArticlesList, *, ArticlesListDetailNavComponentChild.ArticleDetail, Nothing, Nothing>> =
    MutableValue(
      ChildPanels<Unit, ArticlesListDetailNavComponentChild.ArticlesList, Unit, ArticlesListDetailNavComponentChild.ArticleDetail, Nothing , Nothing>(
        Child.Created(
          configuration = Unit,
          ArticlesListDetailNavComponentChild.ArticlesList(fakeArticlesListComponent)
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