package mikufan.cx.conduit.frontend.ui.screen.main

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import mikufan.cx.conduit.frontend.logic.component.main.MainNavComponent

@Composable
fun MainPage(component: MainNavComponent, modifier: Modifier = Modifier) {

  val mainNavState by component.state.collectAsState()
  val mainNavManuItems by remember { derivedStateOf { mainNavState.menuItems } }

  Column {
    Text("TODO Menus:")
    Text("$mainNavManuItems")
  }
}
