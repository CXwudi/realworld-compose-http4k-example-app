package mikufan.cx.conduit.frontend.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageComponent
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageIntent
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace

@Composable
fun LandingPage(component: LandingPageComponent, modifier: Modifier = Modifier) {

  val state by component.state.subscribeAsState()
  val urlText by remember { derivedStateOf { state.url } }

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(LocalSpace.current.vertical.spacing)
    ) {
      OutlinedTextField(
        value = urlText,
        label = { Text("URL") },
        onValueChange = { component.send(LandingPageIntent.TextChanged(it)) }
      )
      Button(onClick = { component.send(LandingPageIntent.ToNextPage) }) {
        Text("Connect")
      }
    }

  }

}