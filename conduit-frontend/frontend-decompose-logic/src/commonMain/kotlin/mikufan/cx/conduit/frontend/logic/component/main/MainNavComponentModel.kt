package mikufan.cx.conduit.frontend.logic.component.main

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

/**
 * Intents for navigation in the main page
 */
sealed interface MainNavIntent {
  data class ModeSwitching(val targetMode: MainNavMode): MainNavIntent
  data class MenuItemSwitching(val targetMenuItem: MainNavMenuItem): MainNavIntent
}
