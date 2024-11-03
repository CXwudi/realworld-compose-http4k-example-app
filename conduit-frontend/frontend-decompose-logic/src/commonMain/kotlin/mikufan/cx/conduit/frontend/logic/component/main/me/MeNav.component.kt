package mikufan.cx.conduit.frontend.logic.component.main.me

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

interface MeNavComponent {
  val childStack: Value<ChildStack<*, MeNavComponentChild>>
}

class DefaultMeNavComponent(
  componentContext: ComponentContext,
) : MeNavComponent, ComponentContext by componentContext {

  private val stackNavigation = StackNavigation<Config>()
  override val childStack: Value<ChildStack<*, MeNavComponentChild>> = childStack(
    source = stackNavigation,
    initialConfiguration = Config.MeComponent,
    serializer = Config.serializer(),
    childFactory = ::childFactory
  )

  private fun childFactory(
    config: Config,
    componentContext: ComponentContext
  ): MeNavComponentChild {
    // TODO: switch to real creation
    return MeNavComponentChild.MeComponent
  }

  @Serializable
  sealed interface Config {
    @Serializable
    data object MeComponent : Config
  }
}

class MeNavComponentFactory(
  // TODO: add dependencies
) {
  fun create(componentContext: ComponentContext) = DefaultMeNavComponent(
    componentContext = componentContext,
  )
}

