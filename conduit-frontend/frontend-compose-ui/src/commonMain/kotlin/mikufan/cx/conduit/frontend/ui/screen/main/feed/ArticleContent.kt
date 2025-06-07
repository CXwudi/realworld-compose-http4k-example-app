package mikufan.cx.conduit.frontend.ui.screen.main.feed

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticleDetailComponent
import mikufan.cx.conduit.frontend.logic.component.main.feed.FullArticleInfo
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace

/**
 * Display the article detail screen.
 * 
 * TODO: Implement the full article detail display with loading the article content.
 */
@Composable
fun AnimatedVisibilityScope.ArticleContent(component: ArticleDetailComponent, modifier: Modifier = Modifier) {
  val state by component.state.collectAsState()
  Box( // outer box to make sure the column is in the center
    modifier = modifier
      .fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(LocalSpace.current.vertical.spacing),
      modifier = modifier
        .widthIn(max = LocalSpace.current.horizontal.maxContentSpace)
        .fillMaxHeight()
        .verticalScroll(rememberScrollState())
        .padding(vertical = LocalSpace.current.vertical.padding)
        .imePadding()
        .safeDrawingPadding(),
    ) {
      Text(
        text = "Viewing article: ${state.info.title}",
        style = MaterialTheme.typography.headlineSmall
      )

      val body = remember {
        derivedStateOf {
          (state.info as? FullArticleInfo)?.bodyMarkdown ?: "No content"
        }
      }

      Text(
        text = body.value,
        style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}
