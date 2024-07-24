package mikufan.cx.conduit.frontend.logic.component.main


data class MainNavState(
  val menuItems: List<MainNavMenuItem>
)

enum class MainNavMenuItem(
  val menuName: String,
) {
  Feed("Feeds"),
  Favourite("Favourites"),
  Me("Me"),
  SignInUp("Sign in/up"),
}

sealed interface MainNavIntent {
  data object ToMainFeed : MainNavIntent
  data object ToFavourite : MainNavIntent
  data object ToMe : MainNavIntent
  data object ToSignInUp : MainNavIntent
}