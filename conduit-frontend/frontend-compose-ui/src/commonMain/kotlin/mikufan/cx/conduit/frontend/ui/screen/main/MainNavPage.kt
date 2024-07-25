package mikufan.cx.conduit.frontend.ui.screen.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import mikufan.cx.conduit.frontend.logic.component.main.MainNavComponent
import mikufan.cx.conduit.frontend.logic.component.main.MainNavComponentChild
import mikufan.cx.conduit.frontend.logic.component.main.MainNavIntent
import mikufan.cx.conduit.frontend.logic.component.main.MainNavMenuItem
import mikufan.cx.conduit.frontend.logic.component.main.MainNavMode

@Composable
fun MainNavPage(component: MainNavComponent, modifier: Modifier = Modifier) {

  val mainNavState by component.state.subscribeAsState()
  val slot by component.childSlot.subscribeAsState()

  val selectedIndex by remember { derivedStateOf { mainNavState.pageIndex } }
  val mainStateMode by remember { derivedStateOf { mainNavState.mode } }

  Scaffold(
    bottomBar = {
      BottomNavigationBar(mainStateMode, component::send, selectedIndex)
    }
  ) {
    AnimatedContentTransition<MainNavComponentChild?>(
      targetState = slot.child?.instance
    ) {
      when (it) {
        is MainNavComponentChild.MainFeed -> Text("Main Feed")
        is MainNavComponentChild.Favourite -> Text("Favourite")
        is MainNavComponentChild.Me -> Text("Me")
        is MainNavComponentChild.SignInUp -> Text("Sign in/up")
        null -> error("Unexpected null child in childSlot in Main Page")
      }
    }
  }
}

@Composable
private fun BottomNavigationBar(
  mainStateMode: MainNavMode,
  onSend: (MainNavIntent) -> Unit,
  selectedIndex: Int,
) {
  NavigationBar {
    val items = mainStateMode.menuItems.withIndex().map { (index, value) ->
      mapMenuItemEnum2NavItem(value, index, onSend)
    }
    items.forEach { item ->
      NavigationBarItem(
        icon = { Icon(item.icon, contentDescription = item.label) },
        label = { Text(item.label) },
        selected = selectedIndex == item.index,
        onClick = {
          item.onClick()
        }
      )
    }
  }
}

@Composable
private fun <S> AnimatedContentTransition(
  targetState: S,
  modifier: Modifier = Modifier,
  content: @Composable AnimatedContentScope.(targetState: S) -> Unit
) {
  AnimatedContent(
    targetState = targetState,
    modifier = modifier,
    content = content
  )
}

data class NavigationItem(
  val label: String,
  val icon: ImageVector,
  val index: Int,
  val onClick: () -> Unit
)

private fun mapMenuItemEnum2NavItem(
  value: MainNavMenuItem,
  index: Int,
  onSend: (MainNavIntent) -> Unit
) = when (value) {
  MainNavMenuItem.Feed -> NavigationItem(
    value.menuName,
    Icons.Filled.Home,
    index
  ) { onSend(MainNavIntent.ToFeedPage) }

  MainNavMenuItem.Favourite -> NavigationItem(
    value.menuName,
    Icons.Filled.Favorite,
    index
  ) { onSend(MainNavIntent.ToFavouritePage) }

  MainNavMenuItem.Me -> NavigationItem(
    value.menuName,
    Icons.Filled.Person,
    index
  ) { onSend(MainNavIntent.ToMePage) }

  MainNavMenuItem.SignInUp -> NavigationItem(
    value.menuName,
    Icons.Filled.Person,
    index
  ) { onSend(MainNavIntent.ToSignInUpPage) }
}