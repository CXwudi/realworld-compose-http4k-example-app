package mikufan.cx.conduit.frontend.logic.component.main

import mikufan.cx.conduit.frontend.logic.component.main.MainNavIntent.ToFavouritePage
import mikufan.cx.conduit.frontend.logic.component.main.MainNavIntent.ToFeedPage
import mikufan.cx.conduit.frontend.logic.component.main.MainNavIntent.ToMePage
import mikufan.cx.conduit.frontend.logic.component.main.MainNavIntent.ToSignInUpPage

data class MainNavState(
  val mode: MainNavMode,
  /**
   * This index is used for navigation bar to indicate which page is selected
   */
  val pageIndex: Int,
) {
  val currentMenuItem: MainNavMenuItem
    get() = mode.menuItems[pageIndex]


  fun indexOfMenuItem(menuItem: MainNavMenuItem): Int? = mode.indexMap[menuItem]
}

/**
 * This class can probably be inlined,
 * but it is better to keep it as it serves as an indicator telling if the user is logged in
 */
enum class MainNavMode(
  val menuItems: List<MainNavMenuItem>,
) {
  NOT_LOGGED_IN(
    listOf(
      MainNavMenuItem.Feed,
      MainNavMenuItem.SignInUp
    )
  ),
  LOGGED_IN(
    listOf(
      MainNavMenuItem.Feed,
      MainNavMenuItem.Favourite,
      MainNavMenuItem.Me
    )
  );
  internal val indexMap: Map<MainNavMenuItem, Int> = menuItems.withIndex().associate { it.value to it.index }
}

enum class MainNavMenuItem(
  val menuName: String,
) {
  Feed("Feeds"),
  Favourite("Favourites"),
  Me("Me"),
  SignInUp("Sign in/up"),
}

sealed interface ModeSwitchingIntent
sealed interface PageSwitchingIntent {

  companion object {
    fun pageSwitchingIntent2MenuItem(intent: PageSwitchingIntent): MainNavMenuItem = when (intent) {
      is ToFeedPage -> MainNavMenuItem.Feed
      is ToFavouritePage -> MainNavMenuItem.Favourite
      is ToMePage -> MainNavMenuItem.Me
      is ToSignInUpPage -> MainNavMenuItem.SignInUp
    }
  }
}
sealed interface MainNavIntent {

  data object ToSignInMode: MainNavIntent, ModeSwitchingIntent
  data object ToLogoutMode: MainNavIntent, ModeSwitchingIntent

  data object ToFeedPage: MainNavIntent, PageSwitchingIntent
  data object ToFavouritePage: MainNavIntent, PageSwitchingIntent
  data object ToMePage: MainNavIntent, PageSwitchingIntent
  data object ToSignInUpPage: MainNavIntent, PageSwitchingIntent

}