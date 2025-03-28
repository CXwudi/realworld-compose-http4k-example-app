package mikufan.cx.conduit.frontend.logic.component.util

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * A single MVI component that accept an [Intent] and update the exposed single [State] value.
 *
 * Suitable for [ComponentContext] that is used for a single UI.
 *
 * Probably not suitable for [ComponentContext] that is used for navigation.
 * Although you likely can do it for child slot navigation,
 * it will break the MVI pattern as you will have another exposed value for navigation.
 *
 * @param Intent
 * @param State
 */
interface MviComponent<in Intent : Any, out State : Any> {
  /**
   * Note: originally we chose to use [com.arkivanov.decompose.value.Value] class instead of [StateFlow],
   * because we want to let the kotlin/js side happy. But now looks like [StateFlow] has more benefits
   * (like `distinctUntilChanged` and `caching`) that really out-perform [com.arkivanov.decompose.value.Value].
   *
   * Example of React Kotlin app collecting StateFlow: https://youtrack.jetbrains.com/issue/KT-42129/KJS-collecting-StateFlow-in-a-Kotlin-React-app
   */
  val state: StateFlow<State>
  fun send(intent: Intent)
}

interface LabelEmitter<out Label : Any>  {
  val labels: Flow<Label>
}