package mikufan.cx.conduit.frontend.ui.util

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Stable

/**
 * Simply just `fadeIn() togetherWith fadeOut()`
 *
 * The key advantage is that there is no delay between enter and exit,
 * which will make the content transition looks smoother without having the blink effect.
 *
 * @see fadeIn
 * @see fadeOut
 */
@Stable
fun fadeInAndOut(): ContentTransform = fadeIn() togetherWith fadeOut()
