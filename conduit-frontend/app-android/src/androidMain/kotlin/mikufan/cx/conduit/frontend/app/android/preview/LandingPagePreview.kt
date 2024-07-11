package mikufan.cx.conduit.frontend.app.android.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageComponent
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageIntent
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageState
import mikufan.cx.conduit.frontend.ui.screen.LandingPage
import mikufan.cx.conduit.frontend.ui.util.SetupUI

@Composable
@Preview
fun LandingPagePreview() {

  SetupUI {
    val mockComponent = object : LandingPageComponent {
      override val state: Value<LandingPageState> = MutableValue(LandingPageState("bla bla URL", "bla error"))

      override fun send(intent: LandingPageIntent) {
      }
    }
    LandingPage(mockComponent)
  }
}