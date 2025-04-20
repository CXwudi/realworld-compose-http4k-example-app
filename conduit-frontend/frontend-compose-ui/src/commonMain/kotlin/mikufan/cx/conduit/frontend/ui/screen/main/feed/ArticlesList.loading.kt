package mikufan.cx.conduit.frontend.ui.screen.main.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import mikufan.cx.conduit.frontend.ui.common.BouncingDotsLoading
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace

@Composable
fun ArticlesListLoading(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(LocalSpace.current.vertical.spacingLarge * 2, Alignment.CenterVertically)
  ) {
    BouncingDotsLoading()
    Text(
      text = "Loading articles...",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.alpha(0.8f)
    )
  }
}
