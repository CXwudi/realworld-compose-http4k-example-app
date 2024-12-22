package mikufan.cx.conduit.frontend.logic.component.main.me

sealed interface MePageState {
  data object Loading : MePageState
  data class Error(val errorMsg: String) : MePageState
  data class Loaded(
    val email: String,
    val imageUrl: String,
    val username: String = "",
    val bio: String = "",
  ) : MePageState
}

/**
 * Used for passing the information from service to store
 */
class LoadedMe(
  val email: String,
  val username: String,
  val bio: String = "",
  val imageUrl: String = "",
)

sealed interface MePageIntent {
  data object Logout : MePageIntent
  data object SwitchServer : MePageIntent
  data object EditProfile : MePageIntent
  data object AddArticle : MePageIntent
}

sealed interface MePageLabel {

}


