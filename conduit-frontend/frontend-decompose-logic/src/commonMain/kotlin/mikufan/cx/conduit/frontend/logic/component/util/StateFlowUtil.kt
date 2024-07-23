package mikufan.cx.conduit.frontend.logic.component.util

import com.arkivanov.decompose.Cancellation
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


fun <T : Any> StateFlow<T>.asValue(scope: CoroutineScope = CoroutineScope(Dispatchers.Default)): Value<T> =
  object : Value<T>() {
    override val value: T
      get() = this@asValue.value

    override fun subscribe(observer: (T) -> Unit): Cancellation {

      val job = this@asValue
        .onEach { observer(it) }
        .launchIn(scope)
      return Cancellation {
        job.cancel()
      }
    }

  }