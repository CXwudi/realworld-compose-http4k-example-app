package mikufan.cx.conduit.frontend.ui.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageComponent
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageIntent
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageLabel
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageState
import mikufan.cx.conduit.frontend.ui.screen.LandingPage
import mikufan.cx.conduit.frontend.ui.util.SetupPreviewUI

@Composable
@Preview
fun LandingPagePreview() {

  SetupPreviewUI {
    val mockComponent = object : LandingPageComponent {
      override val state: StateFlow<LandingPageState> = MutableStateFlow(LandingPageState("bla bla URL"))

      override fun send(intent: LandingPageIntent) {
      }

      override val labels: Flow<LandingPageLabel> = MutableStateFlow(LandingPageLabel.Failure("some error"))
    }
    LandingPage(mockComponent)
  }
}