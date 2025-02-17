package mikufan.cx.conduit.frontend.ui.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.panels.ChildPanelsLayout
import com.arkivanov.decompose.router.panels.ChildPanelsMode

/**
 * An implementation of [ChildPanelsLayout] for laying out panels with fixed and adaptive widths.
 *
 * - If the `mode` is `SINGLE`, all panels are displayed in a stack. The Main panel, then
 * the Details panel on top (if any), and finally the Extra panel (if any).
 * - If the `mode` is `DUAL`, the Main panel has a fixed width, and the Details and Extra panels
 * share the remaining space in a stack on the right side.
 * - If the `mode` is `TRIPLE`, the Main and Extra panels have fixed widths, while the Details panel
 * takes up the remaining space between them.
 *
 * ```
 * SINGLE mode
 * +-----------------------------+
 * |            Main             |
 * |           Details           |
 * |            Extra            |
 * +-----------------------------+
 *
 * DUAL mode
 * +-----------------------------+
 * |  Fixed   |     Adaptive    |
 * |   Main   |     Details     |
 * |          |      Extra      |
 * +-----------------------------+
 *
 * TRIPLE mode
 * +-----------------------------+
 * | Fixed  |  Adaptive  |Fixed |
 * | Main   |  Details   |Extra |
 * |        |            |      |
 * +-----------------------------+
 * ```
 *
 * @param dualMainWidth Fixed width for the Main panel in DUAL mode
 * @param tripleWidths Pair of fixed widths for (Main panel, Extra panel) in TRIPLE mode
 */
@ExperimentalDecomposeApi
class CustomHorizontalChildPanelsLayout(
  private val dualMainWidth: Dp,
  private val tripleWidths: Pair<Dp, Dp>,
) : ChildPanelsLayout {

  private val singleMeasurePolicy = SingleMeasurePolicy()

  @Composable
  override fun Layout(
    mode: ChildPanelsMode,
    main: @Composable () -> Unit,
    details: @Composable () -> Unit,
    extra: @Composable () -> Unit,
  ) {
    val density = LocalDensity.current
    val dualMainPx = remember(dualMainWidth, density) {
      with(density) { dualMainWidth.roundToPx() }
    }
    val (tripleMainPx, tripleExtraPx) = remember(tripleWidths, density) {
      with(density) {
        tripleWidths.first.roundToPx() to tripleWidths.second.roundToPx()
      }
    }

    val measurePolicy = remember(mode, dualMainPx, tripleMainPx, tripleExtraPx) {
      when (mode) {
        ChildPanelsMode.SINGLE -> singleMeasurePolicy
        ChildPanelsMode.DUAL -> DualMeasurePolicy(mainWidthPx = dualMainPx)
        ChildPanelsMode.TRIPLE -> TripleMeasurePolicy(
          mainWidthPx = tripleMainPx,
          extraWidthPx = tripleExtraPx
        )
      }
    }

    Layout(
      content = {
        main()
        details()
        extra()
      },
      modifier = Modifier.fillMaxSize(),
      measurePolicy = measurePolicy,
    )
  }
}

private class SingleMeasurePolicy : MeasurePolicy {
  override fun MeasureScope.measure(
    measurables: List<Measurable>,
    constraints: Constraints
  ): MeasureResult {
    val placeables = measurables.map { it.measure(constraints) }

    return layout(constraints.maxWidth, constraints.maxHeight) {
      placeables.forEach {
        it.placeRelative(x = 0, y = 0)
      }
    }
  }
}

private class DualMeasurePolicy(private val mainWidthPx: Int) : MeasurePolicy {
  override fun MeasureScope.measure(
    measurables: List<Measurable>,
    constraints: Constraints
  ): MeasureResult {
    val clampedMain = mainWidthPx.coerceAtMost(constraints.maxWidth)
    val remaining = (constraints.maxWidth - clampedMain).coerceAtLeast(0)

    val mainPlaceable =
      measurables[0].measure(constraints.copy(minWidth = clampedMain, maxWidth = clampedMain))
    val detailsPlaceable =
      measurables[1].measure(constraints.copy(minWidth = remaining, maxWidth = remaining))
    val extraPlaceable =
      measurables[2].measure(constraints.copy(minWidth = remaining, maxWidth = remaining))

    return layout(constraints.maxWidth, constraints.maxHeight) {
      mainPlaceable.placeRelative(0, 0)
      detailsPlaceable.placeRelative(clampedMain, 0)
      extraPlaceable.placeRelative(clampedMain, 0)
    }
  }
}

private class TripleMeasurePolicy(
  private val mainWidthPx: Int,
  private val extraWidthPx: Int
) : MeasurePolicy {
  override fun MeasureScope.measure(
    measurables: List<Measurable>,
    constraints: Constraints
  ): MeasureResult {
    val clampedMain = mainWidthPx.coerceAtMost(constraints.maxWidth)
    val remainingAfterMain = (constraints.maxWidth - clampedMain).coerceAtLeast(0)
    val clampedExtra = extraWidthPx.coerceAtMost(remainingAfterMain)
    val detailsWidth = (remainingAfterMain - clampedExtra).coerceAtLeast(0)

    val mainPlaceable =
      measurables[0].measure(constraints.copy(minWidth = clampedMain, maxWidth = clampedMain))
    val detailsPlaceable =
      measurables[1].measure(constraints.copy(minWidth = detailsWidth, maxWidth = detailsWidth))
    val extraPlaceable =
      measurables[2].measure(constraints.copy(minWidth = clampedExtra, maxWidth = clampedExtra))

    return layout(constraints.maxWidth, constraints.maxHeight) {
      mainPlaceable.placeRelative(0, 0)
      detailsPlaceable.placeRelative(clampedMain, 0)
      extraPlaceable.placeRelative(clampedMain + detailsWidth, 0)
    }
  }
}
