package mikufan.cx.conduit.frontend.logic.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import mikufan.cx.conduit.frontend.logic.component.landing.DefaultLandingPageComponent
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageComponent
import mikufan.cx.conduit.frontend.logic.component.main.DefaultMainNavComponent
import mikufan.cx.conduit.frontend.logic.component.main.MainNavComponent
import mikufan.cx.conduit.frontend.logic.component.util.LocalKoinComponent
import mikufan.cx.conduit.frontend.logic.service.UserConfigService
import mikufan.cx.conduit.frontend.logic.service.UserConfigState
import org.koin.core.component.get

interface RootNavComponent {
  val childSlot: Value<ChildSlot<*, RootComponentChild>>
}

sealed interface RootComponentChild {

  data object Loading : RootComponentChild
  data class LandingPage(
    val component: LandingPageComponent
  ) : RootComponentChild

  data class MainPage(
    val component: MainNavComponent
  ) : RootComponentChild

}

class DefaultRootNavComponent(
  componentContext: ComponentContext,
  private val koin: LocalKoinComponent,
  private val userConfigService: UserConfigService,
) : RootNavComponent, ComponentContext by componentContext {

  private val slotNavigation = SlotNavigation<Config>()

  override val childSlot: Value<ChildSlot<*, RootComponentChild>> =
    childSlot(
      source = slotNavigation,
      initialConfiguration = { Config.Loading },
      serializer = Config.serializer(),
      childFactory = ::childFactory
    )

  init {
    val lifecycleScope = coroutineScope()
    lifecycleScope.launch {
      userConfigService.userConfigStateFlow
        .map {
          when (it) {
            is UserConfigState.Loading -> Config.Loading
            is UserConfigState.Loaded -> {
              if (it.url.isNullOrBlank()) {
                Config.LandingPage
              } else {
                Config.MainPage
              }
            }
          }
        }
        .distinctUntilChanged()
        .collect {
          slotNavigation.activate(it)
        }
    }
  }

  private fun childFactory(
    config: Config,
    componentContext: ComponentContext
  ): RootComponentChild {
    val child = when (config) {
      Config.Loading -> RootComponentChild.Loading
      Config.LandingPage -> RootComponentChild.LandingPage(
        component = koin.createLandingPageComponent(componentContext)
      )
      Config.MainPage -> RootComponentChild.MainPage(
        component = koin.createMainNavComponent(componentContext)
      )
    }
    return child
  }

  private fun LocalKoinComponent.createLandingPageComponent(
    componentContext: ComponentContext
  ) = DefaultLandingPageComponent(
    componentContext = componentContext,
    koinComponent = this,
    storeFactory = get(),
  )

  private fun LocalKoinComponent.createMainNavComponent(
    componentContext: ComponentContext
  ) = DefaultMainNavComponent(
    componentContext = componentContext,
    koin = this,
    storeFactory = get(),
  )


  /**
   * This config doesn't need to contain any data,
   * because each page is pretty much fetching the data from services,
   *
   * E.g. Landing page pretty much fetch data from [UserConfigService], and
   * Main page will also fetch data mainly from various services.
   */
  @Serializable
  sealed interface Config {
    @Serializable
    data object Loading : Config

    @Serializable
    data object LandingPage : Config

    @Serializable
    data object MainPage : Config
  }
}
