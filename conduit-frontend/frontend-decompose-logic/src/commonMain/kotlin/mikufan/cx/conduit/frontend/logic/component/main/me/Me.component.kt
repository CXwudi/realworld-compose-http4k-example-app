package mikufan.cx.conduit.frontend.logic.component.main.me

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.flow.StateFlow
import mikufan.cx.conduit.frontend.logic.component.util.MviComponent

interface MePageComponent : MviComponent<MePageIntent, MePageState>

class DefaultMePageComponent(
  componentContext: ComponentContext,
  meStoreFactory: MeStoreFactory,
  preloadedMe: LoadedMe? = null,
  private val onEditProfile: (LoadedMe) -> Unit,
  private val onAddArticle: () -> Unit,
) : MePageComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore { meStoreFactory.createStore(preloadedMe = preloadedMe) }

  override val state: StateFlow<MePageState> = store.stateFlow(coroutineScope())

  override fun send(intent: MePageIntent) {
    when (intent) {
      is MePageIntent.EditProfile -> {
        val stateValue = state.value
        if (stateValue is MePageState.Loaded) {
          onEditProfile(LoadedMe(stateValue.email, stateValue.username, stateValue.bio, stateValue.imageUrl))
        }
      }
      is MePageIntent.AddArticle -> onAddArticle()
      else -> store.accept(intent)
    }
  }
}

class MePageComponentFactory(
  private val meStoreFactory: MeStoreFactory,
) {
  fun create(
    componentContext: ComponentContext,
    preloadedMe: LoadedMe? = null,
    onEditProfile: (LoadedMe) -> Unit,
    onAddArticle: () -> Unit,
  ) =
    DefaultMePageComponent(
      componentContext = componentContext,
      meStoreFactory = meStoreFactory,
      preloadedMe = preloadedMe,
      onEditProfile = onEditProfile,
      onAddArticle = onAddArticle,
    )
}
