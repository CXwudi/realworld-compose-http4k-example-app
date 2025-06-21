package mikufan.cx.conduit.frontend.ui.common

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.compose.rememberConstraintsSizeResolver
import coil3.request.ImageRequest
import mikufan.cx.conduit.frontend.ui.resources.Res
import mikufan.cx.conduit.frontend.ui.resources.outlined_broken_image
import mikufan.cx.conduit.frontend.ui.resources.user_default_avater
import org.jetbrains.compose.resources.painterResource

/**
 * Displays a profile image in a circular shape.
 * Handles loading, error, and empty states with fallback to default avatar.
 *
 * @param imageUrl URL of the profile image to load, can be null or blank
 * @param username Username used for accessibility description and cache keys
 * @param size Size of the image (both width and height)
 * @param modifier Optional modifier for customizing the layout
 */
@Composable
fun ProfileImage(
  imageUrl: String?,
  username: String,
  size: Dp,
  modifier: Modifier = Modifier
) {
  // If imageUrl is null or blank, directly use the default avatar
  if (imageUrl.isNullOrBlank()) {
    Image(
      painter = painterResource(Res.drawable.user_default_avater),
      contentDescription = "Default profile picture for $username",
      contentScale = ContentScale.Crop,
      modifier = modifier
        .size(size)
        .clip(CircleShape)
    )
    return
  }

  // Only use Coil for non-blank URLs
  val sizeResolver = rememberConstraintsSizeResolver()
  val painter = rememberAsyncImagePainter(
    model = ImageRequest.Builder(LocalPlatformContext.current)
      .data(imageUrl)
      .size(sizeResolver)
      .diskCacheKey(username)
      .memoryCacheKey(username)
      .build()
  )

  val painterState by painter.state.collectAsState()

  Crossfade(painterState) {
    when (painterState) {
      is AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Loading -> {
        CircularProgressIndicator(
          modifier = modifier
            .then(sizeResolver)
            .size(size)
            .clip(CircleShape)
        )
      }

      is AsyncImagePainter.State.Success -> {
        Image(
          painter = painter,
          contentDescription = "Profile picture of $username",
          contentScale = ContentScale.Crop,
          modifier = modifier
            .then(sizeResolver)
            .size(size)
            .clip(CircleShape)
        )
      }

      is AsyncImagePainter.State.Error -> {
        Icon(
          painter = painterResource(Res.drawable.outlined_broken_image),
          contentDescription = "Broken profile picture of $username",
          modifier = modifier
            .then(sizeResolver)
            .size(size)
            .clip(CircleShape)
        )
      }
    }
  }
}