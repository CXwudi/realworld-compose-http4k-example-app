package mikufan.cx.conduit.frontend.logic.component.util

import com.arkivanov.decompose.Cancellation
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


fun <T : Any> StateFlow<T>.asValue(scope: CoroutineScope): Value<T> =
  if (this is ValueStateFlow) {
    this.source
  } else {
    StateFlowValue(this, scope)
  }

private class StateFlowValue<out T : Any>(
  val source: StateFlow<T>,
  private val scope: CoroutineScope
) : Value<T>() {
  override val value: T
    get() = source.value

  override fun subscribe(observer: (T) -> Unit): Cancellation {
    val job = source
      .onEach { observer(it) }
      .launchIn(scope)
    return Cancellation {
      job.cancel()
    }
  }
}

fun <T : Any> Value<T>.asStateFlow(): StateFlow<T> =
  if (this is StateFlowValue) {
    this.source
  } else {
    ValueStateFlow(this)
  }

private class ValueStateFlow<out T : Any>(val source: Value<T>) : StateFlow<T> {

  override val value: T
    get() = source.value

  override val replayCache: List<T>
    get() = listOf(source.value)

  override suspend fun collect(collector: FlowCollector<T>): Nothing {
    val flow = MutableStateFlow(source.value)
    val disposable = source.subscribe { flow.value = it }

    try {
      flow.collect(collector)
    } finally {
      disposable.cancel()
    }
  }
}