package mikufan.cx.conduit.frontend.logic.component.main.me

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

interface MeNavComponent {
  val childStack: Value<ChildStack<*, MeNavComponentChild>>
}

class DefaultMeNavComponent(
  componentContext: ComponentContext,
  private val mePageComponentFactory: MePageComponentFactory,
  private val editProfileComponentFactory: EditProfileComponentFactory,
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
          onEditProfile = { loadedMe -> stackNavigation.pushNew(Config.EditProfile(loadedMe)) },
          onAddArticle = { stackNavigation.pushNew(Config.AddArticle) },
        )
        return MeNavComponentChild.MePage(mePageComponent)
      }
      is Config.EditProfile -> {
        val editProfileComponent = editProfileComponentFactory.create(
          componentContext = componentContext,
          loadedMe = config.loadedMe,
          onSaveSuccess = { stackNavigation.pop() },
        )
        return MeNavComponentChild.EditProfile(editProfileComponent)
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
    data class EditProfile(val loadedMe: LoadedMe) : Config

    @Serializable
    data object AddArticle : Config
  }
}

class MeNavComponentFactory(
  private val mePageComponentFactory: MePageComponentFactory,
  private val editProfileComponentFactory: EditProfileComponentFactory,
) {
  fun create(componentContext: ComponentContext) = DefaultMeNavComponent(
    componentContext = componentContext,
    mePageComponentFactory = mePageComponentFactory,
    editProfileComponentFactory = editProfileComponentFactory,
  )
}

