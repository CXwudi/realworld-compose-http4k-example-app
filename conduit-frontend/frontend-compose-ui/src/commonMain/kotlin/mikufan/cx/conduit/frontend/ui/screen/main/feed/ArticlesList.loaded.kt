package mikufan.cx.conduit.frontend.ui.screen.main.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticleInfo
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListIntent
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace

@Composable
fun ArticlesListLoaded(
  collectedThumbInfosState: State<List<ArticleInfo>>,
  isLoadingMoreState: State<Boolean>,
  onIntent: (ArticlesListIntent) -> Unit
) {
  val isEmpty by remember { derivedStateOf { collectedThumbInfosState.value.isEmpty() } }
  if (isEmpty) {
    EmptyScreen()
    return
  }
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(LocalSpace.current.vertical.spacing)
  ) {
    items(
      items = collectedThumbInfosState.value,
      key = { it.slug }
      ) {
      Text("slug: ${it.slug}")
      Text("author: ${it.authorUsername}")
      Text("title: ${it.title}")
      Text("description: ${it.description}")
      Text("createdAt: ${it.createdAt}")
    }
  }
}

@Composable
private fun EmptyScreen() {
  Text("EmptyScreen")
}