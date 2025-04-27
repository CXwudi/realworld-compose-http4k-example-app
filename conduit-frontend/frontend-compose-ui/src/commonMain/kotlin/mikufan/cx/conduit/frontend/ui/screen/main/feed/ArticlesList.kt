package mikufan.cx.conduit.frontend.ui.screen.main.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListComponent
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListIntent
import mikufan.cx.conduit.frontend.logic.component.main.feed.LoadMoreState
import mikufan.cx.conduit.frontend.ui.common.BouncingDotsLoading
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace

@Composable
fun AnimatedVisibilityScope.ArticlesList(component: ArticlesListComponent) {
  val articlesListState = component.state.collectAsState()
  val collectedThumbInfos by remember { derivedStateOf { articlesListState.value.collectedThumbInfos } }
  val loadState by remember { derivedStateOf { articlesListState.value.loadMoreState } }
  val size by remember { derivedStateOf { collectedThumbInfos.size } }

  val gridState = rememberLazyGridState()

  val reachLoadMoreThreshold by remember {
    derivedStateOf {
      val visibleItemsInfo = gridState.layoutInfo.visibleItemsInfo
      visibleItemsInfo.isEmpty() || visibleItemsInfo.last().index > collectedThumbInfos.lastIndex - 5
    }
  }

  // start loading upon the first visit
  LaunchedEffect(Unit) {
    component.send(ArticlesListIntent.LoadMore)
  }

  // launch effect for loading more
  // use reachLoadMoreThreshold and if statement with reachLoadMoreThreshold
  // to check if the user is about to reach the end of the list, so that we can start loading more.
  // use size to check if loading once is not enough, and we still reachLoadMoreThreshold,
  // so that we can start loading more.
  LaunchedEffect(reachLoadMoreThreshold, size) {
    // guarding with gridState.layoutInfo.visibleItemsInfo.isNotEmpty()
    // so that the first launch effect will do the initial load.
    // although we could merge two launch effects by simply removing this guard.
    if (loadState == LoadMoreState.Loaded && reachLoadMoreThreshold && gridState.layoutInfo.visibleItemsInfo.isNotEmpty()) {
      // must make sure that when this function finished,
      // the state is already not ArticlesListIntent.Loaded
      component.send(ArticlesListIntent.LoadMore)
    }
  }


  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(LocalSpace.current.vertical.spacing),
    modifier = Modifier.padding(vertical = LocalSpace.current.vertical.padding)
  ) {
    LazyVerticalGrid(
      state = gridState,
      columns = GridCells.Adaptive(minSize = LocalSpace.current.horizontal.maxContentSpace / 3),
      horizontalArrangement = Arrangement.spacedBy(LocalSpace.current.horizontal.spacing),
      verticalArrangement = Arrangement.spacedBy(LocalSpace.current.vertical.spacing),
      modifier = Modifier.padding(horizontal = LocalSpace.current.horizontal.padding)
    ) {
      items(
        items = collectedThumbInfos,
        key = { "${it.slug}${it.createdAt}" }
      ) { item ->
        Column {
          Text("slug: ${item.slug}")
          Text("author: ${item.authorUsername}")
          Text("title: ${item.title}")
          Text("description: ${item.description}")
          Text("createdAt: ${item.createdAt}")
        }
      }
    }
    AnimatedVisibility(loadState == LoadMoreState.Loading) {
      BouncingDotsLoading()
    }
  }
}

@Composable
private fun EmptyScreen() {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(LocalSpace.current.vertical.spacing * 2)
    ) {
      Text(
        text = ">_<",
        style = MaterialTheme.typography.headlineLarge
      )
      Text(
        text = "No articles yet",
        style = MaterialTheme.typography.bodyLarge
      )
    }
  }
}
