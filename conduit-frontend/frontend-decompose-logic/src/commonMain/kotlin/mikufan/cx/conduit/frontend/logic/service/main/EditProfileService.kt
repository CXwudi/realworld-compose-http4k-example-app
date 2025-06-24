package mikufan.cx.conduit.frontend.logic.service.main

import mikufan.cx.conduit.common.UserReq
import mikufan.cx.conduit.common.UserUpdateDto
import mikufan.cx.conduit.frontend.logic.component.main.me.LoadedMe
import mikufan.cx.conduit.frontend.logic.repo.api.AuthApi
import mikufan.cx.conduit.frontend.logic.repo.api.util.getOrThrow
import mikufan.cx.conduit.frontend.logic.repo.kstore.UserInfo


interface EditProfileService {
  suspend fun update(profileUpdate: UserUpdateDto): LoadedMe
  suspend fun updateAndGetUserInfo(profileUpdate: UserUpdateDto): UserInfo
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

  override suspend fun updateAndGetUserInfo(profileUpdate: UserUpdateDto): UserInfo {
    val rsp = authApi.updateCurrentUser(UserReq(profileUpdate))
    val newUser = rsp.getOrThrow().user

    return UserInfo(
      email = newUser.email,
      username = newUser.username,
      bio = newUser.bio,
      image = newUser.image,
      token = newUser.token
    )
  }
}