package mikufan.cx.conduit.frontend.ui.screen

import NotificationType
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import createNotification
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageComponent
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageIntent
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageLabel
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace

@Composable
fun LandingPage(component: LandingPageComponent, modifier: Modifier = Modifier) {

  val state by component.state.collectAsState()
  val urlText = remember { derivedStateOf { state.url } }

  LaunchedEffect(Unit) {
    component.labels.collect { label ->
      when (label) {
        is LandingPageLabel.Failure -> {
          createNotification(NotificationType.ALERT).show(label.message)
        }
        // Handle other labels if necessary
        else -> {}
      }
    }
  }

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier.fillMaxSize().windowInsetsPadding(WindowInsets.ime)
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(LocalSpace.current.vertical.spacingLarge * 2)
    ) {
      OutlinedTextField(
        value = urlText.value,
        label = { Text("URL") },
        onValueChange = { component.send(LandingPageIntent.TextChanged(it)) },
        singleLine = true,
      )
      Button(onClick = { component.send(LandingPageIntent.CheckAndMoveToMainPage) }) {
        Text("Connect")
      }
    }

  }
}
