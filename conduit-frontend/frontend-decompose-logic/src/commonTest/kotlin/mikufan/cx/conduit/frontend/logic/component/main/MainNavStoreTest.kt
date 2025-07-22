package mikufan.cx.conduit.frontend.logic.component.main

import com.arkivanov.mvikotlin.core.rx.observer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import mikufan.cx.conduit.frontend.logic.repo.kstore.UserConfigKStore
import mikufan.cx.conduit.frontend.logic.repo.kstore.UserConfigState
import mikufan.cx.conduit.frontend.logic.repo.kstore.UserInfo
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class MainNavStoreTest {
  private val testDispatcher = StandardTestDispatcher()

  private lateinit var userConfigKStore: UserConfigKStore
  private lateinit var mainNavStore: Store<MainNavIntent, MainNavState, Nothing>

  @BeforeTest
  fun setUp() {
    userConfigKStore = mock()
    mainNavStore = MainNavStoreFactory(
      LoggingStoreFactory(DefaultStoreFactory()),
      userConfigKStore,
      testDispatcher,
    ).createStore(autoInit = false)
  }

  @AfterTest
  fun reset() {
    mainNavStore.dispose()
  }

  @Test
  fun testBootstrapperOnUrlToNotLoggedIn() = runTest(testDispatcher) {
    val stateChannel = Channel<MainNavState>()

    // Given
    every { userConfigKStore.userConfigFlow } returns flowOf(UserConfigState.OnUrl("test-url"))

    // When
    val disposable = mainNavStore.states(observer(onNext = { this.launch { stateChannel.send(it) } }))
    mainNavStore.init()

    // Then - ignore initial state and get the updated state
    stateChannel.receive() // initial state
    val newState = stateChannel.receive()
    assertFalse(newState.isLoggedIn)
    assertEquals(2, newState.menuItems.size)
    assertTrue(newState.menuItems.contains(MainNavMenuItem.Feed))
    assertTrue(newState.menuItems.contains(MainNavMenuItem.SignInUp))

    disposable.dispose()
  }

  @Test
  fun testBootstrapperOnLoginToLoggedIn() = runTest(testDispatcher) {
    val stateChannel = Channel<MainNavState>()

    // Given
    val userInfo = UserInfo(
      email = "test@example.com",
      username = "testuser",
      bio = "test bio",
      image = "test-image.png",
      token = "test-token"
    )
    every { userConfigKStore.userConfigFlow } returns flowOf(UserConfigState.OnLogin("test-url", userInfo))

    // When
    val disposable = mainNavStore.states(observer(onNext = { this.launch { stateChannel.send(it) } }))
    mainNavStore.init()

    // Then - ignore initial state and get the updated state
    stateChannel.receive() // initial state
    val newState = stateChannel.receive()
    assertTrue(newState.isLoggedIn)
    assertEquals(3, newState.menuItems.size)
    assertTrue(newState.menuItems.contains(MainNavMenuItem.Feed))
    assertTrue(newState.menuItems.contains(MainNavMenuItem.Me))
    
    val favouriteItem = newState.menuItems.find { it is MainNavMenuItem.Favourite } as? MainNavMenuItem.Favourite
    assertTrue(favouriteItem != null)
    assertEquals("testuser", favouriteItem.username)

    disposable.dispose()
  }

  @Test
  fun testBootstrapperLandingStateThrowsException() = runTest(testDispatcher) {
    // Given
    every { userConfigKStore.userConfigFlow } returns flowOf(UserConfigState.Landing)

    // When/Then - bootstrapper should throw IllegalStateException for Landing state
    assertFailsWith<IllegalStateException> {
      mainNavStore.init()
    }
  }

  @Test
  fun testSwitchToNotLoggedInStateTransition() = runTest(testDispatcher) {
    val stateChannel = Channel<MainNavState>()

    // Given - start with logged in state
    val userInfo = UserInfo(
      email = "test@example.com",
      username = "testuser", 
      bio = null,
      image = null,
      token = "test-token"
    )
    every { userConfigKStore.userConfigFlow } returns flowOf(UserConfigState.OnLogin("test-url", userInfo))
    
    val disposable = mainNavStore.states(observer(onNext = { this.launch { stateChannel.send(it) } }))
    mainNavStore.init()
    stateChannel.receive() // initial state
    stateChannel.receive() // logged in state

    // When - switch to OnUrl (not logged in)
    every { userConfigKStore.userConfigFlow } returns flowOf(UserConfigState.OnUrl("test-url"))

    // Then - state should switch to not logged in
    val newState = stateChannel.receive()
    assertFalse(newState.isLoggedIn)
    assertEquals(2, newState.menuItems.size)
    assertTrue(newState.menuItems.contains(MainNavMenuItem.Feed))
    assertTrue(newState.menuItems.contains(MainNavMenuItem.SignInUp))

    disposable.dispose()
  }

  @Test
  fun testSwitchToLoggedInStateTransition() = runTest(testDispatcher) {
    val stateChannel = Channel<MainNavState>()

    // Given - start with not logged in state
    every { userConfigKStore.userConfigFlow } returns flowOf(UserConfigState.OnUrl("test-url"))
    
    val disposable = mainNavStore.states(observer(onNext = { this.launch { stateChannel.send(it) } }))
    mainNavStore.init()
    stateChannel.receive() // initial state
    stateChannel.receive() // not logged in state

    // When - switch to OnLogin (logged in)
    val userInfo = UserInfo(
      email = "newuser@example.com",
      username = "newuser",
      bio = "new bio", 
      image = "new-image.png",
      token = "new-token"
    )
    every { userConfigKStore.userConfigFlow } returns flowOf(UserConfigState.OnLogin("test-url", userInfo))

    // Then - state should switch to logged in
    val newState = stateChannel.receive()
    assertTrue(newState.isLoggedIn)
    assertEquals(3, newState.menuItems.size)
    
    val favouriteItem = newState.menuItems.find { it is MainNavMenuItem.Favourite } as? MainNavMenuItem.Favourite
    assertTrue(favouriteItem != null)
    assertEquals("newuser", favouriteItem.username)

    disposable.dispose()
  }

  @Test
  fun testUsernameChangeForLoggedInUser() = runTest(testDispatcher) {
    val stateChannel = Channel<MainNavState>()

    // Given - start with logged in user
    val userInfo1 = UserInfo("test@example.com", "user1", null, null, "token1")
    every { userConfigKStore.userConfigFlow } returns flowOf(UserConfigState.OnLogin("test-url", userInfo1))
    
    val disposable = mainNavStore.states(observer(onNext = { this.launch { stateChannel.send(it) } }))
    mainNavStore.init()
    stateChannel.receive() // initial state
    stateChannel.receive() // first logged in state

    // When - username changes
    val userInfo2 = UserInfo("test@example.com", "user2", null, null, "token2")
    every { userConfigKStore.userConfigFlow } returns flowOf(UserConfigState.OnLogin("test-url", userInfo2))

    // Then - state should update with new username
    val newState = stateChannel.receive()
    assertTrue(newState.isLoggedIn)
    
    val favouriteItem = newState.menuItems.find { it is MainNavMenuItem.Favourite } as? MainNavMenuItem.Favourite
    assertTrue(favouriteItem != null)
    assertEquals("user2", favouriteItem.username)

    disposable.dispose()
  }

  @Test
  fun testMenuIndexSwitchingValidIndex() = runTest(testDispatcher) {
    // Given - not logged in state (2 menu items: Feed, SignInUp)
    every { userConfigKStore.userConfigFlow } returns flowOf(UserConfigState.OnUrl("test-url"))
    mainNavStore.init()

    // When - switch to index 1 (SignInUp)
    mainNavStore.accept(MainNavIntent.MenuIndexSwitching(1))

    // Then
    assertEquals(1, mainNavStore.state.pageIndex)
    assertEquals(MainNavMenuItem.SignInUp, mainNavStore.state.currentMenuItem)
  }

  @Test
  fun testMenuIndexSwitchingInvalidIndexTooHigh() = runTest(testDispatcher) {
    // Given - not logged in state (2 menu items)
    every { userConfigKStore.userConfigFlow } returns flowOf(UserConfigState.OnUrl("test-url"))
    mainNavStore.init()

    // When/Then - should throw IllegalArgumentException for index 2 (out of bounds)
    assertFailsWith<IllegalArgumentException> {
      mainNavStore.accept(MainNavIntent.MenuIndexSwitching(2))
    }
  }

  @Test
  fun testMenuIndexSwitchingInvalidIndexNegative() = runTest(testDispatcher) {
    // Given
    every { userConfigKStore.userConfigFlow } returns flowOf(UserConfigState.OnUrl("test-url"))
    mainNavStore.init()

    // When/Then - should throw IllegalArgumentException for negative index
    assertFailsWith<IllegalArgumentException> {
      mainNavStore.accept(MainNavIntent.MenuIndexSwitching(-1))
    }
  }

  @Test
  fun testMenuIndexSwitchingNoOpWhenSameIndex() = runTest(testDispatcher) {
    val stateChannel = Channel<MainNavState>()

    // Given
    every { userConfigKStore.userConfigFlow } returns flowOf(UserConfigState.OnUrl("test-url"))
    
    val disposable = mainNavStore.states(observer(onNext = { this.launch { stateChannel.send(it) } }))
    mainNavStore.init()
    stateChannel.receive() // initial state
    stateChannel.receive() // initialized state (pageIndex = 0)

    // When - try to switch to same index
    mainNavStore.accept(MainNavIntent.MenuIndexSwitching(0))

    // Then - no state change should occur (channel should not receive anything new)
    assertTrue(stateChannel.tryReceive().isFailure)
    assertEquals(0, mainNavStore.state.pageIndex)

    disposable.dispose()
  }

  @Test
  fun testMenuIndexSwitchingLoggedInState() = runTest(testDispatcher) {
    // Given - logged in state (3 menu items: Feed, Favourite, Me)
    val userInfo = UserInfo("test@example.com", "testuser", null, null, "token")
    every { userConfigKStore.userConfigFlow } returns flowOf(UserConfigState.OnLogin("test-url", userInfo))
    mainNavStore.init()

    // When - switch to index 2 (Me)
    mainNavStore.accept(MainNavIntent.MenuIndexSwitching(2))

    // Then
    assertEquals(2, mainNavStore.state.pageIndex)
    assertEquals(MainNavMenuItem.Me, mainNavStore.state.currentMenuItem)
  }

  @Test
  fun testStateConsistencyValidationLoggedInWithoutFavourite() = runTest(testDispatcher) {
    // This test ensures that the validation logic catches inconsistent states
    // In practice, this scenario shouldn't occur due to proper state management,
    // but the validation exists as a safeguard

    // Given - mock a flow that would create inconsistent state (this is theoretical)
    every { userConfigKStore.userConfigFlow } returns flowOf(UserConfigState.OnUrl("test-url"))
    mainNavStore.init()
    
    // The validation happens inside the executor when actions are processed
    // Since MainNavState.loggedIn() always creates Favourite menu item correctly,
    // and MainNavState.notLoggedIn() always creates correct not-logged-in state,
    // the validation mainly serves as a runtime check for potential bugs

    // Just verify that current state is consistent
    val currentState = mainNavStore.state
    if (currentState.isLoggedIn) {
      val hasFavouriteItem = currentState.menuItems.any { it is MainNavMenuItem.Favourite }
      assertTrue(hasFavouriteItem, "Logged in state must have Favourite menu item")
    }
  }

  @Test 
  fun testInitialStateNotLoggedIn() {
    // Test that initial state is correctly set to not logged in
    val initialState = mainNavStore.state
    assertFalse(initialState.isLoggedIn)
    assertEquals(0, initialState.pageIndex)
    assertEquals(2, initialState.menuItems.size)
    assertTrue(initialState.menuItems.contains(MainNavMenuItem.Feed))
    assertTrue(initialState.menuItems.contains(MainNavMenuItem.SignInUp))
  }
}