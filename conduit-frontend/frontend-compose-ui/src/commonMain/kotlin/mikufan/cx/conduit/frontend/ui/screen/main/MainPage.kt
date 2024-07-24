package mikufan.cx.conduit.frontend.ui.screen.main

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import mikufan.cx.conduit.frontend.logic.component.main.MainNavComponent

@Composable
fun MainPage(component: MainNavComponent, modifier: Modifier = Modifier) {

  val mainNavState by component.state.subscribeAsState()

  Column {
    Text("TODO Menus:")
  }
}
