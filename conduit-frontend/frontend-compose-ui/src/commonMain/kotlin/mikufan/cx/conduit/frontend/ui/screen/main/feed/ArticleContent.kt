package mikufan.cx.conduit.frontend.ui.screen.main.feed

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticleDetailComponent
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticleDetailState
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace

/**
 * Display the article detail screen.
 * 
 * TODO: Implement the full article detail display with loading the article content.
 */
@Composable
fun AnimatedVisibilityScope.ArticleContent(component: ArticleDetailComponent) {
  val state by component.state.collectAsState()
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(
        horizontal = LocalSpace.current.horizontal.padding,
        vertical = LocalSpace.current.vertical.padding
      ),
    contentAlignment = Alignment.Center
  ) {
    if (state is ArticleDetailState.Preloaded) {
      Text(
        text = "Viewing article: ${(state as ArticleDetailState.Preloaded).info.title}",
        style = MaterialTheme.typography.headlineSmall
      )
    }

  }
}
