package mikufan.cx.conduit.frontend.logic.component.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import mikufan.cx.conduit.frontend.logic.component.util.LocalKoinComponent
import mikufan.cx.conduit.frontend.logic.component.util.MviComponent
import org.lighthousegames.logging.logging

interface MainNavComponent : MviComponent<MainNavIntent ,MainNavState>

sealed interface MainNavComponentChild {

  // TODO: each class need a component class, e.g. LandingPageComponent
  data object MainFeed : MainNavComponentChild
  data object Favourite : MainNavComponentChild
  data object Me : MainNavComponentChild
  data object SignInUp : MainNavComponentChild

}

class DefaultMainNavComponent(
  componentContext: ComponentContext,
  private val koin: LocalKoinComponent,
  private val storeFactory: MainNavStoreFactory,
) : MainNavComponent, ComponentContext by componentContext {

  override val state: Value<MainNavState> = TODO("Not yet implemented")

  override fun send(intent: MainNavIntent) {
    TODO("Not yet implemented")
  }


  @Serializable
  sealed interface Config {

    @Serializable
    data object MainFeed : Config

    @Serializable
    data object Favourite : Config

    @Serializable
    data object Me : Config

    @Serializable
    data object SignInUp : Config
  }

}

private val log = logging()