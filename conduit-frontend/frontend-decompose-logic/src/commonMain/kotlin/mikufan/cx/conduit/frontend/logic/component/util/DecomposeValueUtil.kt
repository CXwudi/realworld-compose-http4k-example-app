package mikufan.cx.conduit.frontend.logic.component.util

import com.arkivanov.decompose.Cancellation
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.rx.observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn


fun <T : Any> StateFlow<T>.toValue(scope: CoroutineScope): Value<T> =
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

@ExperimentalCoroutinesApi
fun <T : Any> Value<T>.toStateFlow(): StateFlow<T> =
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

fun <T : Any> Value<T>.toFlow() = callbackFlow {
  val disposable = this@toFlow.subscribe {
    observer<T>(
      onComplete = { channel.close() },
      onNext = { channel.trySend(it) }
    )
  }
  awaitClose { disposable.cancel() }
}

fun <T : Any> Value<T>.toStateFlow(
  scope: CoroutineScope,
  started: SharingStarted = SharingStarted.Eagerly,
): StateFlow<T> = toFlow().stateIn(scope, started, this.value)
