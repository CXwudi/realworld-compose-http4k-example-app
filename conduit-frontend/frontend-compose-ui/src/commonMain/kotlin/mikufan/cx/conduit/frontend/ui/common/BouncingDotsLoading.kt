package mikufan.cx.conduit.frontend.ui.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace

/**
 * A bouncing dots loading animation component that can be used in any loading state.
 * Features dots that bounce up and down with a slight delay between each.
 *
 * @param modifier The modifier to be applied to the layout
 * @param dotCount Number of dots to display
 * @param dotSize Size of each dot
 * @param dotColor Color of the dots
 * @param dotShape Shape of the dots
 * @param dotSpacing Spacing between dots
 * @param bounceHeight Height of the bounce animation
 * @param animationDuration Duration of the animation in milliseconds
 * @param delayBetweenDots Delay between each dot's animation start in milliseconds
 * @param paddingAroundDots Padding around the dots row
 */
@Composable
fun BouncingDotsLoading(
  modifier: Modifier = Modifier,
  dotCount: Int = 3,
  dotSize: Dp = LocalSpace.current.horizontal.spacing * 2,
  dotColor: Color = MaterialTheme.colorScheme.primary,
  dotShape: Shape = CircleShape,
  dotSpacing: Dp = LocalSpace.current.horizontal.spacingSmall * 3,
  bounceHeight: Float = 15f,
  animationDuration: Int = 1200,
  delayBetweenDots: Int = 200,
  paddingAroundDots: Dp = LocalSpace.current.horizontal.padding * 2
) {
  val infiniteTransition = rememberInfiniteTransition()
  
  // Generate delays based on dot count and delayBetweenDots
  val delays = List(dotCount) { it * delayBetweenDots }

  // Create animated offsets with different delay for each dot
  val offsets = delays.map { delay ->
    infiniteTransition.animateFloat(
      initialValue = 0f,
      targetValue = bounceHeight,
      animationSpec = infiniteRepeatable(
        animation = keyframes {
          durationMillis = animationDuration
          0f at 0 using LinearEasing
          bounceHeight at (animationDuration / 4) using FastOutSlowInEasing
          0f at (animationDuration / 2) using LinearEasing
          0f at animationDuration
        },
        initialStartOffset = StartOffset(delay)
      )
    )
  }

  Row(
    modifier
      .wrapContentSize(Alignment.Center)
      .padding(paddingAroundDots),
    verticalAlignment = Alignment.CenterVertically
  ) {
    offsets.forEachIndexed { index, offset ->
      Box(
        Modifier
          .size(dotSize)
          .offset(y = (-offset.value).dp)
          .background(
            color = dotColor,
            shape = dotShape
          )
      )
      if (index < dotCount - 1) {
        Spacer(Modifier.width(dotSpacing))
      }
    }
  }
}
