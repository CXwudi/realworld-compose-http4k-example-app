package mikufan.cx.conduit.frontend.ui.screen.main.feed

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticleDetailComponent
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace

/**
 * Display the article detail screen.
 * 
 * TODO: Implement the full article detail display with loading the article content.
 */
@Composable
fun AnimatedVisibilityScope.ArticleContent(component: ArticleDetailComponent) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(
        horizontal = LocalSpace.current.horizontal.padding,
        vertical = LocalSpace.current.vertical.padding
      ),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = "Viewing article: ${component.slug}",
      style = MaterialTheme.typography.headlineSmall
    )
  }
}
