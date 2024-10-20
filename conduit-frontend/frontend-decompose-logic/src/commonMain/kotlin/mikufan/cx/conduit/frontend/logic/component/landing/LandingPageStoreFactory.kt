package mikufan.cx.conduit.frontend.logic.component.landing

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import mikufan.cx.conduit.frontend.logic.service.UserConfigService


sealed interface LandingPageIntent {
  data class TextChanged(val text: String) : LandingPageIntent
  data object ToNextPage : LandingPageIntent
}

data object LandingPageToNextPageLabel

@Serializable
data class LandingPageState(
  val url: String,
  val errorMsg: String,
)

class LandingPageStoreFactory(
  private val storeFactory: StoreFactory,
  private val userConfigService: UserConfigService,
  mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
  defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
  ) {

  internal sealed interface Msg {
    data class TextChanged(val text: String) : Msg
    data class ErrorMsgChanged(val errorMsg: String) : Msg
  }

  private val executor =
    coroutineExecutorFactory<LandingPageIntent, Nothing, LandingPageState, Msg, LandingPageToNextPageLabel>(mainDispatcher) {
      onIntent<LandingPageIntent.TextChanged> {
        dispatch(Msg.TextChanged(it.text))
        if (state().errorMsg.isNotBlank()) {
          dispatch(Msg.ErrorMsgChanged(""))
        }
      }
      onIntent<LandingPageIntent.ToNextPage> {
        launch {
          try {
            withContext(defaultDispatcher) {
              userConfigService.setUrl(state().url)
//              log.debug { "Set url = ${state().url}" }
            }
            // this label is in fact unused, because every other component is subscribing the userConfigService directly
            publish(LandingPageToNextPageLabel)
//            log.debug { "Pushed the label" }
          } catch (e: IllegalArgumentException) {
            dispatch(Msg.ErrorMsgChanged(e.message ?: "Unknown error"))
          }
        }
      }
    }

  private val reducer = Reducer<LandingPageState, Msg> { msg ->
    when (msg) {
      is Msg.TextChanged -> copy(url = msg.text)
      is Msg.ErrorMsgChanged -> copy(errorMsg = msg.errorMsg)
    }
  }

  fun createStore() = storeFactory.create(
      name = "LandingPageStore",
      initialState = LandingPageState("", ""),
      executorFactory = executor,
      reducer = reducer
    )
}

private val log = KotlinLogging.logger { }

