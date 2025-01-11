package mikufan.cx.conduit.frontend.logic.service.main

import mikufan.cx.conduit.common.UserReq
import mikufan.cx.conduit.common.UserUpdateDto
import mikufan.cx.conduit.frontend.logic.component.main.me.LoadedMe
import mikufan.cx.conduit.frontend.logic.repo.api.AuthApi
import mikufan.cx.conduit.frontend.logic.repo.api.util.getOrThrow


interface EditProfileService {
  suspend fun update(profileUpdate: UserUpdateDto): LoadedMe
}

class DefaultEditProfileService(
  private val authApi: AuthApi,
) : EditProfileService {

  override suspend fun update(profileUpdate: UserUpdateDto): LoadedMe {
    val rsp = authApi.updateCurrentUser(UserReq(profileUpdate))
    val newUser = rsp.getOrThrow().user

    return LoadedMe(
      email = newUser.email,
      username = newUser.username,
      bio = newUser.bio ?: "",
      imageUrl = newUser.image ?: "",
    )
  }
}