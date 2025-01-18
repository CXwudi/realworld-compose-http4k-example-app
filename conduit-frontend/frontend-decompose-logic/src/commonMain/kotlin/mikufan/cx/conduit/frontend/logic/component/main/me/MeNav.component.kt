package mikufan.cx.conduit.frontend.logic.component.main.me

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceAll
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
    initialConfiguration = Config.MePage(),
    serializer = Config.serializer(),
    handleBackButton = true,
    childFactory = ::childFactory
  )

  private fun childFactory(
    config: Config,
    componentContext: ComponentContext
  ): MeNavComponentChild {
    when (config) {
      is Config.MePage -> {
        val mePageComponent = mePageComponentFactory.create(
          componentContext = componentContext,
          preloadedMe = config.preloadedMe,
          onEditProfile = { loadedMe -> stackNavigation.pushNew(Config.EditProfile(loadedMe)) },
          onAddArticle = { stackNavigation.pushNew(Config.AddArticle) },
        )
        return MeNavComponentChild.MePage(mePageComponent)
      }

      is Config.EditProfile -> {
        val editProfileComponent = editProfileComponentFactory.create(
          componentContext = componentContext,
          loadedMe = config.loadedMe,
          onSaveSuccess = { newMe ->
            // we can also have the returned new me being directly passed into the existing me page component
            // and update the store through the component.
            // However, that was too much rigging, so we are just lazy and recreate the me page component.
            stackNavigation.replaceAll(Config.MePage(newMe))
          },
          onBackWithoutSave = { stackNavigation.pop() },
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
    data class MePage(val preloadedMe: LoadedMe? = null) : Config

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

