package mikufan.cx.conduit.frontend.ui.screen.main.feed

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListComponent
import mikufan.cx.conduit.frontend.logic.component.main.feed.LoadMoreState
import mikufan.cx.conduit.frontend.ui.common.BouncingDotsLoading
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace

@Composable
fun ArticlesList(component: ArticlesListComponent) {
  val articlesListState = component.state.collectAsState()
  val collectedThumbInfos = articlesListState.value.collectedThumbInfos
  val isLoadingMore by remember { derivedStateOf { articlesListState.value.loadMoreState == LoadMoreState.Loading } }
  val size by remember { derivedStateOf { collectedThumbInfos.size } }
  val isEmpty by remember { derivedStateOf { size == 0 } }
  
  if (isEmpty) {
    EmptyScreen()
    return
  }

  val gridState = rememberLazyGridState()
  // need completely rework
  // val reachLoadMoreThreshold by remember {
  //   derivedStateOf {
  //     gridState.layoutInfo.visibleItemsInfo.isNotEmpty()
  //     && gridState.layoutInfo.visibleItemsInfo.last().index > collectedThumbInfos.lastIndex - 5
  //   }
  // }

  // LaunchedEffect(reachLoadMoreThreshold, isLoadingMore) {
  //   if (reachLoadMoreThreshold && !isLoadingMore) {
  //     component.send(ArticlesListIntent.LoadMore)
  //   }
  // }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(LocalSpace.current.vertical.spacing),
    modifier = Modifier.padding(vertical = LocalSpace.current.vertical.padding)
  ) {
    LazyVerticalGrid(
      state = gridState,
      columns = GridCells.Adaptive(minSize = 200.dp),
      horizontalArrangement = Arrangement.spacedBy(LocalSpace.current.horizontal.spacing),
      verticalArrangement = Arrangement.spacedBy(LocalSpace.current.vertical.spacing),
      modifier = Modifier.padding(horizontal = LocalSpace.current.horizontal.padding)
    ) {
      items(
        items = collectedThumbInfos,
        key = { it.slug }
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
    AnimatedVisibility(isLoadingMore) {
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
