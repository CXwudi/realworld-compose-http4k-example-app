package mikufan.cx.conduit.frontend.logic.component.landing

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mikufan.cx.conduit.frontend.logic.component.util.rethrowIfShouldNotBeHandled
import mikufan.cx.conduit.frontend.logic.service.landing.LandingService


class LandingPageStoreFactory(
  private val storeFactory: StoreFactory,
  private val landingService: LandingService,
  mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
  defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

  internal sealed interface Msg {
    data class TextChanged(val text: String) : Msg
    data class ErrorMsgChanged(val errorMsg: String) : Msg
  }

  private val executor =
    coroutineExecutorFactory<LandingPageIntent, Nothing, LandingPageState, Msg, LandingPageToNextPageLabel>(
      mainDispatcher
    ) {
      onIntent<LandingPageIntent.TextChanged> {
        dispatch(Msg.TextChanged(it.text))
        if (state().errorMsg.isNotBlank()) {
          dispatch(Msg.ErrorMsgChanged(""))
        }
      }
      onIntent<LandingPageIntent.CheckAndMoveToMainPage> {

        launch {
          try {
            val url = state().url
            log.info { "Checking accessibility for $url" }
            withContext(defaultDispatcher) {
              landingService.checkAccessibilityAndSetUrl(url)
            }
            log.debug { "Set url = $url" }
            publish(LandingPageToNextPageLabel)
          } catch (e: Throwable) {
            rethrowIfShouldNotBeHandled(e) {
              log.debug { "Failed with exception $e" }
              dispatch(Msg.ErrorMsgChanged(e.message ?: "Unknown error"))
            }
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

