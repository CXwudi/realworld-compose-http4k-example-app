package mikufan.cx.conduit.frontend.logic.component.main.me

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mikufan.cx.conduit.frontend.logic.component.util.rethrowIfShouldNotBeHandled
import mikufan.cx.conduit.frontend.logic.service.main.MePageService

class MeStoreFactory(
  private val storeFactory: StoreFactory,
  private val mePageService: MePageService,
  private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
) {

  private val executorFactory = coroutineExecutorFactory<MePageIntent, Action, MePageState, Msg, Unit>(mainDispatcher) {
    onAction<Action.LoadMeSuccess> {
      val loadedMe = it.loadedMe
      dispatch(Msg.LoadMe(loadedMe))
    }

    onAction<Action.LoadMeError> {
      val errorMsg = it.errorMsg
      dispatch(Msg.LoadMeError(errorMsg))
    }

    onIntent<MePageIntent.Logout> {
      launch {
        try {
          withContext(Dispatchers.Default) {
            mePageService.logout()
          }
          publish(Unit) // purely for test purpose
        } catch (t: Throwable) {
          rethrowIfShouldNotBeHandled(t) { e ->
            log.error(e) { "Failed to logout" }
          }
        }
      }

    }

    onIntent<MePageIntent.SwitchServer> {
      launch {
        try {
          withContext(Dispatchers.Default) {
            mePageService.switchServer()
          }
          publish(Unit) // purely for test purpose
        } catch (t: Throwable) {
          rethrowIfShouldNotBeHandled(t) { e ->
            log.error(e) { "Failed to switch server" }
          }
        }
      }
    }
  }

  private fun createBootstrapper() = coroutineBootstrapper(mainDispatcher) {
    launch {
      try {
        val currentUser = withContext(Dispatchers.Default) {
          mePageService.getCurrentUser()
        }
        dispatch(Action.LoadMeSuccess(currentUser))
      } catch (t: Throwable) {
        rethrowIfShouldNotBeHandled(t) { e ->
          log.error(e) { "Failed to load current user" }
          dispatch(Action.LoadMeError(e.message ?: "Failed to load current user"))
        }
      }
    }
  }

  private val reducer = Reducer<MePageState, Msg> { msg ->
    when (msg) {
      is Msg.LoadMe -> MePageState.Loaded(
        email = msg.loadedMe.email,
        imageUrl = msg.loadedMe.imageUrl,
        username = msg.loadedMe.username,
        bio = msg.loadedMe.bio,
      )
      is Msg.LoadMeError -> MePageState.Error(msg.errorMsg)
    }
  }

  fun createStore(autoInit: Boolean = true) = storeFactory.create(
    name = "MePageStore",
    autoInit = autoInit,
    initialState = MePageState.Loading,
    executorFactory = executorFactory,
    bootstrapper = createBootstrapper(),
    reducer = reducer
  )

  private sealed interface Action{
    data class LoadMeSuccess(val loadedMe: LoadedMe) : Action
    data class LoadMeError(val errorMsg: String) : Action
  }

  private sealed interface Msg {
    data class LoadMe(val loadedMe: LoadedMe) : Msg
    data class LoadMeError(val errorMsg: String) : Msg
  }
}

private val log = KotlinLogging.logger {}
