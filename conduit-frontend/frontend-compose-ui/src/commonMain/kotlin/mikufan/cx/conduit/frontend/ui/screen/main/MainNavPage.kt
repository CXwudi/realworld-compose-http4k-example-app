package mikufan.cx.conduit.frontend.ui.screen.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import mikufan.cx.conduit.frontend.ui.screen.main.auth.AuthPage
import mikufan.cx.conduit.frontend.ui.screen.main.feed.ArticlesListDetailPanel
import mikufan.cx.conduit.frontend.ui.screen.main.me.MeNavPage

@Composable
fun MainNavPage(component: MainNavComponent, modifier: Modifier = Modifier) {

  val mainNavState by component.state.collectAsState()
  val stack by component.childStack.subscribeAsState()

  val selectedIndex by remember { derivedStateOf { mainNavState.pageIndex } }
  val mainStateMode by remember { derivedStateOf { mainNavState.mode } }

  val manuItems = remember(mainStateMode) { // not using derivedStateOf because it updates whenever mainStateMode changes
    navigationItems(mainStateMode, component::send)
  }

  NavigationSuiteScaffold(
    navigationSuiteItems = {
      manuItems.forEach { item ->
        item(
          icon = { Icon(item.icon, contentDescription = item.label) },
          label = { Text(item.label) },
          selected = selectedIndex == item.index,
          onClick = item.onClick
        )
      }
    },
    modifier = modifier
  ) {
    AnimatedContent(
      targetState = stack.active.instance,
    ) {
      when (it) {
        is MainNavComponentChild.MainFeed -> ArticlesListDetailPanel(it.testComponent)
        is MainNavComponentChild.Favourite -> Text("Favourite")
        is MainNavComponentChild.Me -> MeNavPage(it.component)
        is MainNavComponentChild.SignInUp -> AuthPage(it.component)
      }
    }
  }
}


private fun navigationItems(
  mainStateMode: MainNavMode,
  onSend: (MainNavIntent) -> Unit,
): List<NavigationItem> {
  return mainStateMode.menuItems.withIndex().map { (index, value) ->
    mapMenuItemEnum2NavItem(value, index, onSend)
  }
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
): NavigationItem {
  val icon = when (value) {
    MainNavMenuItem.Feed -> Icons.Filled.Home
    MainNavMenuItem.Favourite -> Icons.Filled.Favorite
    MainNavMenuItem.Me -> Icons.Filled.Person
    MainNavMenuItem.SignInUp -> Icons.Filled.Person
  }

  val item = NavigationItem(value.menuName, icon, index) {
    onSend(MainNavIntent.MenuItemSwitching(value))
  }

  return item
}