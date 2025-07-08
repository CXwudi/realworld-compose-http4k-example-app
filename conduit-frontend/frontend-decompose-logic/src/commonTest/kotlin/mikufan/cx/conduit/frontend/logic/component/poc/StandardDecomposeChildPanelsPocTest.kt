package mikufan.cx.conduit.frontend.logic.component.poc

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.panels.ChildPanelsMode
import com.arkivanov.decompose.router.panels.Panels
import com.arkivanov.decompose.router.panels.PanelsNavigation
import com.arkivanov.decompose.router.panels.navigate
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.statekeeper.StateKeeperDispatcher
import com.arkivanov.essenty.backhandler.BackHandlerDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Test class for StandardDecomposeChildPanelsPoC.
 * Validates that the corrected implementation works correctly and handles all three panes.
 */
@OptIn(ExperimentalDecomposeApi::class)
class StandardDecomposeChildPanelsPocTest {
    
    @Serializable
    data class MainConfig(val id: String)
    
    @Serializable
    data class DetailConfig(val id: String)
    
    @Serializable
    data class ExtraConfig(val id: String)
    
    data class MainComponent(val config: MainConfig)
    data class DetailComponent(val config: DetailConfig)
    data class ExtraComponent(val config: ExtraConfig)
    
    private fun createTestComponentContext(): ComponentContext {
        val lifecycle = LifecycleRegistry()
        val stateKeeper = StateKeeperDispatcher()
        val backHandler = BackHandlerDispatcher()
        
        return object : ComponentContext {
            override val lifecycle: Lifecycle = lifecycle
            override val stateKeeper = stateKeeper
            override val instanceKeeper = com.arkivanov.essenty.instancekeeper.InstanceKeeperDispatcher()
            override val backHandler = backHandler
        }
    }
    
    @Test
    fun testStandardChildPanelsBasicFunctionality() = runTest {
        val componentContext = createTestComponentContext()
        (componentContext.lifecycle as LifecycleRegistry).resume()
        
        val navigation = PanelsNavigation<MainConfig, DetailConfig, ExtraConfig>()
        
        val poc = StandardDecomposeChildPanelsPoC(
            componentContext = componentContext,
            source = navigation,
            initialPanels = { Panels(MainConfig("main1")) },
            key = "test",
            serializers = Triple(
                MainConfig.serializer(),
                DetailConfig.serializer(),
                ExtraConfig.serializer()
            ),
            mainFactory = { config, _ -> MainComponent(config) },
            detailsFactory = { config, _ -> DetailComponent(config) },
            extraFactory = { config, _ -> ExtraComponent(config) },
            multiModeBackHandling = false
        )
        
        val initialPanels = poc.panels.value
        assertNotNull(initialPanels.main)
        assertEquals("main1", initialPanels.main.instance.config.id)
        assertNull(initialPanels.details)
        assertNull(initialPanels.extra)
        assertEquals(ChildPanelsMode.SINGLE, initialPanels.mode)
    }
    
    @Test
    fun testNavigationToDetailsPanel() = runTest {
        val componentContext = createTestComponentContext()
        (componentContext.lifecycle as LifecycleRegistry).resume()
        
        val navigation = PanelsNavigation<MainConfig, DetailConfig, ExtraConfig>()
        
        val poc = StandardDecomposeChildPanelsPoC(
            componentContext = componentContext,
            source = navigation,
            initialPanels = { Panels(MainConfig("main1")) },
            key = "test",
            serializers = Triple(
                MainConfig.serializer(),
                DetailConfig.serializer(),
                ExtraConfig.serializer()
            ),
            mainFactory = { config, _ -> MainComponent(config) },
            detailsFactory = { config, _ -> DetailComponent(config) },
            extraFactory = { config, _ -> ExtraComponent(config) },
            multiModeBackHandling = false
        )
        
        // Navigate to detail panel
        navigation.navigate { panels ->
            panels.copy(details = DetailConfig("detail1"), mode = ChildPanelsMode.DUAL)
        }
        
        val updatedPanels = poc.panels.value
        assertNotNull(updatedPanels.details)
        assertEquals("detail1", updatedPanels.details!!.instance.config.id)
        assertEquals(ChildPanelsMode.DUAL, updatedPanels.mode)
    }
    
    @Test
    fun testNavigationToExtraPanel() = runTest {
        val componentContext = createTestComponentContext()
        (componentContext.lifecycle as LifecycleRegistry).resume()
        
        val navigation = PanelsNavigation<MainConfig, DetailConfig, ExtraConfig>()
        
        val poc = StandardDecomposeChildPanelsPoC(
            componentContext = componentContext,
            source = navigation,
            initialPanels = { Panels(MainConfig("main1")) },
            key = "test",
            serializers = Triple(
                MainConfig.serializer(),
                DetailConfig.serializer(),
                ExtraConfig.serializer()
            ),
            mainFactory = { config, _ -> MainComponent(config) },
            detailsFactory = { config, _ -> DetailComponent(config) },
            extraFactory = { config, _ -> ExtraComponent(config) },
            multiModeBackHandling = false
        )
        
        // Navigate to extra panel
        navigation.navigate { panels ->
            panels.copy(
                details = DetailConfig("detail1"),
                extra = ExtraConfig("extra1"),
                mode = ChildPanelsMode.TRIPLE
            )
        }
        
        val updatedPanels = poc.panels.value
        assertNotNull(updatedPanels.details)
        assertNotNull(updatedPanels.extra)
        assertEquals("detail1", updatedPanels.details!!.instance.config.id)
        assertEquals("extra1", updatedPanels.extra!!.instance.config.id)
        assertEquals(ChildPanelsMode.TRIPLE, updatedPanels.mode)
    }
    
    @Test
    fun testSimplifiedExtensionFunction() = runTest {
        val componentContext = createTestComponentContext()
        (componentContext.lifecycle as LifecycleRegistry).resume()
        
        val navigation = PanelsNavigation<MainConfig, DetailConfig, Nothing>()
        
        val panels = componentContext.standardChildPanelsWithBackHandler(
            source = navigation,
            serializers = MainConfig.serializer() to DetailConfig.serializer(),
            initialPanels = { Panels(MainConfig("main1")) },
            key = "test",
            multiModeBackHandling = true,
            mainFactory = { config, _ -> MainComponent(config) },
            detailsFactory = { config, _ -> DetailComponent(config) }
        )
        
        val initialPanels = panels.value
        assertNotNull(initialPanels.main)
        assertEquals("main1", initialPanels.main.instance.config.id)
        assertNull(initialPanels.details)
        assertNull(initialPanels.extra)
        assertEquals(ChildPanelsMode.SINGLE, initialPanels.mode)
    }
    
    @Test
    fun testStateChangeCallback() = runTest {
        val componentContext = createTestComponentContext()
        (componentContext.lifecycle as LifecycleRegistry).resume()
        
        val navigation = PanelsNavigation<MainConfig, DetailConfig, ExtraConfig>()
        
        var stateChangeCallCount = 0
        var lastNewState: Panels<MainConfig, DetailConfig, ExtraConfig>? = null
        var lastOldState: Panels<MainConfig, DetailConfig, ExtraConfig>? = null
        
        val poc = StandardDecomposeChildPanelsPoC(
            componentContext = componentContext,
            source = navigation,
            initialPanels = { Panels(MainConfig("main1")) },
            key = "test",
            serializers = Triple(
                MainConfig.serializer(),
                DetailConfig.serializer(),
                ExtraConfig.serializer()
            ),
            mainFactory = { config, _ -> MainComponent(config) },
            detailsFactory = { config, _ -> DetailComponent(config) },
            extraFactory = { config, _ -> ExtraComponent(config) },
            multiModeBackHandling = false,
            onStateChanged = { newState, oldState ->
                stateChangeCallCount++
                lastNewState = newState
                lastOldState = oldState
            }
        )
        
        // Navigate to detail panel
        navigation.navigate { panels ->
            panels.copy(details = DetailConfig("detail1"), mode = ChildPanelsMode.DUAL)
        }
        
        // State change callback should have been called
        assertEquals(2, stateChangeCallCount) // Initial state + navigation
        assertNotNull(lastNewState)
        assertNotNull(lastOldState)
        assertEquals(ChildPanelsMode.DUAL, lastNewState?.mode)
        assertEquals(ChildPanelsMode.SINGLE, lastOldState?.mode)
    }
}