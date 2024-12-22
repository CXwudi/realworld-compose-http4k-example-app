package mikufan.cx.conduit.frontend.logic.component.main.me

data class EditProfileState(
  val email: String,
  val username: String,
  val bio: String,
  val imageUrl: String,
  /**
   * use null to indicate no change to password
   */
  val password: String? = null,
) {
  init {
    if (password != null) {
      require(password.isNotEmpty()) { "Password cannot be empty" }
    }
  }
}

sealed interface EditProfileIntent {
  data class EmailChanged(val email: String) : EditProfileIntent
  data class UsernameChanged(val username: String) : EditProfileIntent
  data class BioChanged(val bio: String) : EditProfileIntent
  data class ImageUrlChanged(val imageUrl: String) : EditProfileIntent
  data class PasswordChanged(val password: String) : EditProfileIntent
  data object Save : EditProfileIntent
}

sealed interface EditProfileLabel {
  data object SaveSuccessLabel : EditProfileLabel
  /**
   * Just for test purpose
   */
  data object Unit : EditProfileLabel
}

