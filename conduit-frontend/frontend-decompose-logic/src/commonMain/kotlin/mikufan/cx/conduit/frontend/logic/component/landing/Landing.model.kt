package mikufan.cx.conduit.frontend.logic.component.landing

import kotlinx.serialization.Serializable

sealed interface LandingPageIntent {
  data class TextChanged(val text: String) : LandingPageIntent
  data object CheckAndMoveToMainPage : LandingPageIntent
}

data object LandingPageToNextPageLabel

@Serializable
data class LandingPageState(
  val url: String,
  val errorMsg: String,
)