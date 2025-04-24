package mikufan.cx.conduit.frontend.ui.screen.main.feed

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.panels.ChildPanels
import com.arkivanov.decompose.router.panels.ChildPanelsMode
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListComponent
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListDetailNavComponent
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListDetailNavComponentChild
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListIntent
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListLabel
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListState
import mikufan.cx.conduit.frontend.ui.util.SetupPreviewUI

val fakeLoadingArticleListComponent = object : ArticlesListComponent {
  override val state: StateFlow<ArticlesListState> = MutableStateFlow(ArticlesListState())

  override fun send(intent: ArticlesListIntent) {}

  override val labels: Flow<ArticlesListLabel> =
    MutableStateFlow(ArticlesListLabel.Failure(null, "some error"))

}

@OptIn(ExperimentalDecomposeApi::class)
private fun createFakeArticlesListDetailComponent(
  childPanels: ChildPanels<Unit, ArticlesListDetailNavComponentChild.ArticlesList, Unit, ArticlesListDetailNavComponentChild.ArticleDetail, Nothing, Nothing>
) = object : ArticlesListDetailNavComponent {
  override val panels: Value<ChildPanels<Unit, ArticlesListDetailNavComponentChild.ArticlesList, Unit, ArticlesListDetailNavComponentChild.ArticleDetail, Nothing, Nothing>> =
    MutableValue(childPanels)
  override fun setWidestAllowedMode(mode: ChildPanelsMode) {}
}


@OptIn(ExperimentalDecomposeApi::class)
@Composable
@Preview
fun ArticlesListDetailPanelPreviewSingleLoading() {
  val fakeComponent = createFakeArticlesListDetailComponent(
    ChildPanels(
      main = Child.Created(
        Unit,
        ArticlesListDetailNavComponentChild.ArticlesList(fakeLoadingArticleListComponent)
      ),
      details = null,
      extra = null,
      mode = ChildPanelsMode.SINGLE
    )
  )
  SetupPreviewUI {
    ArticlesListDetailPanel(fakeComponent)
  }
}

@OptIn(ExperimentalDecomposeApi::class)
@Composable
@Preview
fun ArticlesListDetailPanelPreviewSomeContent() {
  val fakeComponent = createFakeArticlesListDetailComponent(
    ChildPanels(
      main = Child.Created(
        Unit,
        ArticlesListDetailNavComponentChild.ArticlesList(fakeLoadingArticleListComponent)
      ),
      details = Child.Created(Unit, ArticlesListDetailNavComponentChild.ArticleDetail),
      extra = null,
      mode = ChildPanelsMode.SINGLE
    )
  )
  SetupPreviewUI {
    ArticlesListDetailPanel(fakeComponent)
  }
}