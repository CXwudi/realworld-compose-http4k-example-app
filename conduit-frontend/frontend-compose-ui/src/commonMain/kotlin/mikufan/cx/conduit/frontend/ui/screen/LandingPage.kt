package mikufan.cx.conduit.frontend.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageComponent
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageIntent
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace

@Composable
fun LandingPage(component: LandingPageComponent, modifier: Modifier = Modifier) {

  val state by component.state.collectAsState()
  val urlText by remember { derivedStateOf { state.url } }
  val errMsg by remember { derivedStateOf { state.errorMsg } }

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier.windowInsetsPadding(WindowInsets.safeDrawing)
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
      if (errMsg.isNotBlank()) {
        ErrorMessage(errMsg)
      }
      Button(onClick = { component.send(LandingPageIntent.ToNextPage) }) {
        Text("Connect")
      }
    }

  }

}

@Composable
fun ColumnScope.ErrorMessage(message: String, modifier: Modifier = Modifier) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.align(Alignment.Start)
  ) {
    Icon(
      imageVector = Icons.Filled.Warning,
      contentDescription = "Error",
      tint = androidx.compose.ui.graphics.Color.Red
    )
    Spacer(modifier = Modifier.width(LocalSpace.current.horizontal.spacing))
    Text(
      text = message,
      color = androidx.compose.ui.graphics.Color.Red
    )
  }
}