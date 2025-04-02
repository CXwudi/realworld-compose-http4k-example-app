package mikufan.cx.conduit.frontend.ui.screen.main.feed

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace

@Composable
fun ArticlesListLoading() {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(LocalSpace.current.vertical.spacingLarge * 2, Alignment.CenterVertically)
  ) {
    BouncingDotsLoadingAnimation()
    Text(
      text = "Loading articles...",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.alpha(0.8f)
    )
  }
}

@Composable
private fun BouncingDotsLoadingAnimation() {
  val dotCount = 3
  val infiniteTransition = rememberInfiniteTransition()
  val delays = listOf(0, 200, 400)

  // Create animated offsets with different delay for each dot
  val offsets = delays.map { delay ->
    infiniteTransition.animateFloat(
      initialValue = 0f,
      targetValue = 15f,
      animationSpec = infiniteRepeatable(
        animation = keyframes {
          durationMillis = 1200
          0f at 0 using LinearEasing
          15f at 300 using FastOutSlowInEasing
          0f at 600 using LinearEasing
          0f at 1200
        },
        initialStartOffset = StartOffset(delay)
      )
    )
  }

  Row(
    Modifier
      .wrapContentSize(Alignment.Center)
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    offsets.forEachIndexed { index, offset ->
      Box(
        Modifier
          .size(10.dp)
          .offset(y = (-offset.value).dp)
          .background(
            color = MaterialTheme.colorScheme.primary,
            shape = CircleShape
          )
      )
      if (index < dotCount - 1) {
        Spacer(Modifier.width(8.dp))
      }
    }
  }
}
