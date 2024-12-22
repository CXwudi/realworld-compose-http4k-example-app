package mikufan.cx.conduit.frontend.logic.component.main.me

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

interface MeNavComponent {
  val childStack: Value<ChildStack<*, MeNavComponentChild>>
}

class DefaultMeNavComponent(
  componentContext: ComponentContext,
  private val mePageComponentFactory: MePageComponentFactory,
) : MeNavComponent, ComponentContext by componentContext {

  private val stackNavigation = StackNavigation<Config>()
  override val childStack: Value<ChildStack<*, MeNavComponentChild>> = childStack(
    source = stackNavigation,
    initialConfiguration = Config.MePage,
    serializer = Config.serializer(),
    childFactory = ::childFactory
  )

  private fun childFactory(
    config: Config,
    componentContext: ComponentContext
  ): MeNavComponentChild {
    when (config) {
      Config.MePage -> {
        val mePageComponent = mePageComponentFactory.create(
          componentContext = componentContext,
          onEditProfile = { stackNavigation.pushNew(Config.EditProfile) },
          onAddArticle = { stackNavigation.pushNew(Config.AddArticle) },
        )
        return MeNavComponentChild.MePage(mePageComponent)
      }
      Config.EditProfile -> {
        TODO("Not yet implemented")
      }
      Config.AddArticle -> {
        TODO("Not yet implemented")
      }
    }
  }

  @Serializable
  private sealed interface Config {
    @Serializable
    data object MePage : Config

    @Serializable
    data object EditProfile : Config

    @Serializable
    data object AddArticle : Config
  }
}

class MeNavComponentFactory(
  private val mePageComponentFactory: MePageComponentFactory,
) {
  fun create(componentContext: ComponentContext) = DefaultMeNavComponent(
    componentContext = componentContext,
    mePageComponentFactory = mePageComponentFactory,
  )
}

