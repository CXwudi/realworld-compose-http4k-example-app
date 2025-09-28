package mikufan.cx.conduit.frontend.ui.common.layout

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace

/**
 * Combined scope that provides access to both BoxScope and ColumnScope members.
 * This allows content to use both Column layout (vertical arrangement) and Box alignment capabilities.
 */
interface PageColumnScope : BoxScope, ColumnScope

/**
 * Private implementation of PageColumnScope that delegates to the actual Box and Column scopes.
 */
private class PageColumnScopeImpl(
  private val boxScope: BoxScope,
  private val columnScope: ColumnScope
) : PageColumnScope, BoxScope by boxScope, ColumnScope by columnScope

/**
 * A reusable centered page container that encapsulates the common Box + Column pattern.
 * - Outer Box centers content with configurable alignment
 * - Inner Column supports scrolling, width constraints, spacing, and vertical padding
 * - Exposes combined PageColumnScope allowing both Column and Box alignment capabilities
 *
 * @param modifier Applied to the outer Box
 * @param contentModifier Applied to the inner Column
 * @param contentAlignment Alignment of the Column within the Box (default: Alignment.Center)
 * @param minWidth Minimum width constraint for the Column (default: no constraint)
 * @param maxWidth Maximum width constraint for the Column (default: LocalSpace.horizontal.maxContentSpace)
 * @param verticalSpacing Spacing between Column children (default: LocalSpace.vertical.spacing)
 * @param verticalPadding Top/bottom padding for the Column (default: LocalSpace.vertical.padding)
 * @param horizontalAlignment Horizontal alignment of Column children (default: CenterHorizontally)
 * @param verticalArrangementAlignment Vertical alignment within the Column's arrangement (default: none)
 * @param scrollState Custom scroll state (default: rememberScrollState())
 * @param scrollable Whether the Column should be scrollable (default: true)
 * @param fillHeight Whether the Column should fill max height (default: true)
 * @param includeImePadding Whether to include IME padding (default: true)
 * @param includeSafeDrawingPadding Whether to include safe drawing padding (default: true)
 * @param content Main content using combined PageColumnScope
 */
@Composable
fun PageColumn(
  modifier: Modifier = Modifier,
  contentModifier: Modifier = Modifier,
  contentAlignment: Alignment = Alignment.Center,
  minWidth: Dp? = null,
  maxWidth: Dp? = null,
  verticalSpacing: Dp? = null,
  verticalPadding: Dp? = null,
  horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
  verticalArrangementAlignment: Alignment.Vertical? = null,
  scrollState: ScrollState? = null,
  scrollable: Boolean = true,
  fillHeight: Boolean = true,
  includeImePadding: Boolean = true,
  includeSafeDrawingPadding: Boolean = true,
  content: @Composable PageColumnScope.() -> Unit,
) {
  val space = LocalSpace.current
  val resolvedMaxWidth = maxWidth ?: space.horizontal.maxContentSpace
  val resolvedSpacing = verticalSpacing ?: space.vertical.spacing
  val resolvedVerticalPadding = verticalPadding ?: space.vertical.padding
  val resolvedScrollState = scrollState ?: rememberScrollState()

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = contentAlignment
  ) { // this: BoxScope
    var colMod = contentModifier
      .widthIn(min = minWidth ?: Dp.Unspecified, max = resolvedMaxWidth)
      .padding(vertical = resolvedVerticalPadding)

    if (fillHeight) {
      colMod = colMod.fillMaxHeight()
    }
    if (scrollable) {
      colMod = colMod.verticalScroll(resolvedScrollState)
    }
    if (includeImePadding) {
      colMod = colMod.imePadding()
    }
    if (includeSafeDrawingPadding) {
      colMod = colMod.safeDrawingPadding()
    }

    val arrangement = if (verticalArrangementAlignment != null) {
      Arrangement.spacedBy(resolvedSpacing, verticalArrangementAlignment)
    } else {
      Arrangement.spacedBy(resolvedSpacing)
    }

    Column(
      horizontalAlignment = horizontalAlignment,
      verticalArrangement = arrangement,
      modifier = colMod
    ) { // this: ColumnScope
      // Create combined scope and invoke content with it
      val scope = PageColumnScopeImpl(boxScope = this@Box, columnScope = this@Column)
      scope.content()
    }
  }
}
