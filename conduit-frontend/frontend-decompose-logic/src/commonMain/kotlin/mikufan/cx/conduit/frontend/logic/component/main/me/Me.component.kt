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
  onEditProfile: () -> Unit,
  onAddArticle: () -> Unit,
) : MePageComponent, ComponentContext by componentContext {

  private val navigationToActionMap: Map<MePageIntent, () -> Unit> = mapOf(
    MePageIntent.EditProfile to onEditProfile,
    MePageIntent.AddArticle to onAddArticle,
  )

  private val store = instanceKeeper.getStore { meStoreFactory.createStore() }

  override val state: StateFlow<MePageState> = store.stateFlow(coroutineScope())

  override fun send(intent: MePageIntent) {
    if (intent in navigationToActionMap.keys) {
      navigationToActionMap[intent]!!.invoke()
    } else {
      store.accept(intent)
    }
  }
}

class MePageComponentFactory(
  private val meStoreFactory: MeStoreFactory,
) {
  fun create(
    componentContext: ComponentContext,
    onEditProfile: () -> Unit,
    onAddArticle: () -> Unit,
  ) =
    DefaultMePageComponent(
      componentContext = componentContext,
      meStoreFactory = meStoreFactory,
      onEditProfile = onEditProfile,
      onAddArticle = onAddArticle,
    )
}
