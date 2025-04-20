package mikufan.cx.conduit.frontend.ui.screen.main.feed

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListComponent
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListState


@Composable
fun ArticlesList(component: ArticlesListComponent) {

  val listState = component.state.collectAsState()

  Crossfade(targetState = listState.value) {
    when (it) {
      is ArticlesListState.Loading -> ArticlesListLoading(Modifier.fillMaxSize())
      is ArticlesListState.Loaded -> {
        val collectedThumbInfosState = remember { derivedStateOf { it.collectedThumbInfos } }
        val isLoadingMoreState = remember { derivedStateOf { it.isLoadingMore } }
        ArticlesListLoaded(collectedThumbInfosState, isLoadingMoreState, component::send)
      }
    }
  }
}



