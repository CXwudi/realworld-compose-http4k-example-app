@file:OptIn(ExperimentalDecomposeApi::class)

package mikufan.cx.conduit.frontend.logic.component.custom

import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.panels.ChildPanelsMode
import com.arkivanov.decompose.router.panels.Panels

/**
 * A functional interface for handling back button presses in child panels.
 * The implementation should determine what action to take when back button is pressed
 * based on the current state of the panels.
 *
 * @param MC Main component configuration type
 * @param DC Details component configuration type
 * @param EC Extra component configuration type
 */
@OptIn(ExperimentalDecomposeApi::class)
fun interface ChildPanelsBackHandler<MC : Any, DC : Any, EC : Any> {

  /**
   * Handle back button press for the given panels state.
   *
   * @param panels Current panels state
   * @return The modified panels state after handling the back button press, or null if
   * the back button press should be ignored or handled by parent components.
   */
  fun handle(panels: Panels<MC, DC, EC>): Panels<MC, DC, EC>?
}

/**
 * Creates a back handler that only handles back navigation in SINGLE mode.
 * This is the default handler in standard Decompose.
 * 
 * The handler closes details panel if open, or extra panel if open.
 * Does nothing in DUAL or TRIPLE modes.
 *
 * @return A [ChildPanelsBackHandler] function object
 */
@OptIn(ExperimentalDecomposeApi::class)
class SingleModeChildPanelsBackHandler<MC : Any, DC : Any, EC : Any> : ChildPanelsBackHandler<MC, DC, EC> {
  override fun handle(panels: Panels<MC, DC, EC>): Panels<MC, DC, EC>? = when {
    (panels.mode == ChildPanelsMode.SINGLE) && (panels.extra != null) -> {
      panels.copy(extra = null)
    }

    (panels.mode == ChildPanelsMode.SINGLE) && (panels.details != null) -> {
      panels.copy(details = null)
    }

    else -> null
  }
}

/**
 * Creates a back handler that handles back navigation in all panel modes.
 * 
 * In SINGLE mode, it behaves like [SingleModeChildPanelsBackHandler].
 * In TRIPLE mode, pressing back will close the extra panel and switch to DUAL mode.
 * In DUAL mode, pressing back will close the details panel and switch to SINGLE mode.
 *
 * @return A [ChildPanelsBackHandler] function object
 */
@OptIn(ExperimentalDecomposeApi::class)
class MultiModeChildPanelsBackHandler<MC : Any, DC : Any, EC : Any> : ChildPanelsBackHandler<MC, DC, EC> {
  override fun handle(panels: Panels<MC, DC, EC>): Panels<MC, DC, EC>? =
    when {
      (panels.mode == ChildPanelsMode.SINGLE) && (panels.extra != null) -> {
        panels.copy(extra = null)
      }

      (panels.mode == ChildPanelsMode.SINGLE) && (panels.details != null) -> {
        panels.copy(details = null)
      }

      // Added two more customizations for back button handling, where in the wide screen, going back will close the right-most panel
      (panels.mode == ChildPanelsMode.TRIPLE) && (panels.extra != null) -> {
        panels.copy(extra = null, mode = ChildPanelsMode.DUAL)
      }

      (panels.mode == ChildPanelsMode.DUAL) && (panels.details != null) -> {
        panels.copy(details = null, mode = ChildPanelsMode.SINGLE)
      }

      // no handling, let the other back handler do its job
      else -> null
    }
}
