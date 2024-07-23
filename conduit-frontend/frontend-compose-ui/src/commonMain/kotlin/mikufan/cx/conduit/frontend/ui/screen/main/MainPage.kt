package mikufan.cx.conduit.frontend.ui.screen.main

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import mikufan.cx.conduit.frontend.logic.component.main.MainNavComponent
import org.lighthousegames.logging.logging

@Composable
fun MainPage(component: MainNavComponent, modifier: Modifier = Modifier) {

  val mainNavState by component.state.subscribeAsState()
  val mainNavManuItems by remember { derivedStateOf { mainNavState.menuItems } }

  log.d { "I am recomposing :)" }
  Column {
    Text("Menus:")
    Text("$mainNavManuItems")
  }
}

private val log = logging()