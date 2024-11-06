package mikufan.cx.conduit.frontend.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass

data class Space(
  val horizontal: HorizontalSpace,
  val vertical: VerticalSpace,
)

data class HorizontalSpace(
  val spacing: Dp,
  val spacingSmall: Dp,
  val spacingLarge: Dp,
  val padding: Dp,
  val paddingSmall: Dp,
  val paddingLarge: Dp,
)

data class VerticalSpace(
  val spacing: Dp,
  val spacingSmall: Dp,
  val spacingLarge: Dp,
  val padding: Dp,
  val paddingSmall: Dp,
  val paddingLarge: Dp,
)

val LocalSpace = compositionLocalOf<Space> { error("No LocalSpace provided") }

val compactHorizontalSpace = HorizontalSpace(
  spacing = 3.dp,
  spacingSmall = 2.dp,
  spacingLarge = 4.dp,
  padding = 3.dp,
  paddingSmall = 2.dp,
  paddingLarge = 4.dp
)

val mediumHorizontalSpace = HorizontalSpace(
  spacing = 4.dp,
  spacingSmall = 3.dp,
  spacingLarge = 6.dp,
  padding = 4.dp,
  paddingSmall = 3.dp,
  paddingLarge = 6.dp
)

val expandedHorizontalSpace = HorizontalSpace(
  spacing = 6.dp,
  spacingSmall = 4.dp,
  spacingLarge = 8.dp,
  padding = 6.dp,
  paddingSmall = 4.dp,
  paddingLarge = 8.dp
)

val compactVerticalSpace = VerticalSpace(
  spacing = 3.dp,
  spacingSmall = 2.dp,
  spacingLarge = 4.dp,
  padding = 3.dp,
  paddingSmall = 2.dp,
  paddingLarge = 4.dp
)

val medianVerticalSpace = VerticalSpace(
  spacing = 4.dp,
  spacingSmall = 3.dp,
  spacingLarge = 6.dp,
  padding = 4.dp,
  paddingSmall = 3.dp,
  paddingLarge = 6.dp
)

val expandedVerticalSpace = VerticalSpace(
  spacing = 6.dp,
  spacingSmall = 4.dp,
  spacingLarge = 8.dp,
  padding = 6.dp,
  paddingSmall = 4.dp,
  paddingLarge = 8.dp
)

object SpacingDefaults {
  fun calculateSpacing(windowSizeClass: WindowSizeClass): Space {
    val windowWidthSizeClass = windowSizeClass.windowWidthSizeClass
    val windowHeightSizeClass = windowSizeClass.windowHeightSizeClass
    val horizontalSpace = when (windowWidthSizeClass) {
      WindowWidthSizeClass.COMPACT -> compactHorizontalSpace
      WindowWidthSizeClass.MEDIUM -> mediumHorizontalSpace
      WindowWidthSizeClass.EXPANDED -> expandedHorizontalSpace
      else -> error("What is this window width size class: $windowWidthSizeClass")
    }

    // so far we just let the vertical space be the same with the horizontal space
    val verticalSpace = when (windowHeightSizeClass) {
      WindowHeightSizeClass.COMPACT -> compactVerticalSpace
      WindowHeightSizeClass.MEDIUM -> medianVerticalSpace
      WindowHeightSizeClass.EXPANDED -> expandedVerticalSpace
      else -> error("What is this window height size class: $windowHeightSizeClass")
    }

    return Space(horizontal = horizontalSpace, vertical = verticalSpace)
  }
}