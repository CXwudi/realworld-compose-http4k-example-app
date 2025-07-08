@file:OptIn(ExperimentalDecomposeApi::class)

package mikufan.cx.conduit.frontend.logic.component.poc

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.panels.ChildPanels
import com.arkivanov.decompose.router.panels.ChildPanelsMode
import com.arkivanov.decompose.router.panels.Panels
import com.arkivanov.decompose.router.panels.PanelsNavigation
import com.arkivanov.decompose.router.panels.childPanels
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.NothingSerializer
import mikufan.cx.conduit.frontend.logic.component.util.toStateFlow
import kotlinx.coroutines.flow.collectLatest
import com.arkivanov.essenty.backhandler.BackCallback

/**
 * A PoC demonstrating how to replace custom Decompose solutions with standard library implementations.
 * This implementation uses the standard `childPanels()` API with dynamic back handler registration.
 * 
 * Key improvements over the custom implementation:
 * - Uses standard Decompose `childPanels()` API
 * - Implements dynamic back handler registration that doesn't override other handlers
 * - Handles all three panes (main, detail, extra) properly
 * - Uses proper Decompose `Value.subscribe()` for observation
 * - Better integration with Decompose's lifecycle management
 */
@ExperimentalDecomposeApi
class StandardDecomposeChildPanelsPoC<MC : Any, MT : Any, DC : Any, DT : Any, EC : Any, ET : Any>(
    private val componentContext: ComponentContext,
    private val source: PanelsNavigation<MC, DC, EC>,
    private val initialPanels: () -> Panels<MC, DC, EC>,
    private val key: String = "DefaultChildPanels",
    private val serializers: Triple<KSerializer<MC>, KSerializer<DC>, KSerializer<EC>>? = null,
    private val mainFactory: (configuration: MC, ComponentContext) -> MT,
    private val detailsFactory: (configuration: DC, ComponentContext) -> DT,
    private val extraFactory: (configuration: EC, ComponentContext) -> ET,
    private val multiModeBackHandling: Boolean = false,
    private val onStateChanged: (newState: Panels<MC, DC, EC>, oldState: Panels<MC, DC, EC>?) -> Unit = { _, _ -> }
) : ComponentContext by componentContext {
    
    // Create standard Decompose childPanels without custom back handling
    val panels: Value<ChildPanels<MC, MT, DC, DT, EC, ET>> = childPanels(
        source = source,
        key = key,
        serializers = serializers,
        initialPanels = initialPanels,
        onStateChanged = onStateChanged,
        mainFactory = mainFactory,
        detailsFactory = detailsFactory,
        extraFactory = extraFactory
    )
    
    // Dynamic back handler registration
    private var currentBackCallback: BackCallback? = null
    
    init {
        if (multiModeBackHandling) {
            setupDynamicBackHandler()
        }
    }
    
    /**
     * Sets up dynamic back handler registration based on panel state.
     * This approach avoids the issues with handler overriding by dynamically
     * registering/unregistering the back handler as needed.
     */
    private fun setupDynamicBackHandler() {
        // Convert Value to StateFlow for proper observation
        val panelsStateFlow = panels.toStateFlow(componentContext.coroutineScope())
        
        // Launch coroutine to observe panel state changes
        componentContext.coroutineScope().launch {
            panelsStateFlow.collectLatest { currentPanels ->
                val shouldHandleBack = shouldHandleBackForCurrentState(currentPanels)
                
                if (shouldHandleBack) {
                    // Register back handler if not already registered
                    if (currentBackCallback == null) {
                        currentBackCallback = BackCallback(enabled = true) {
                            handleBackPress(currentPanels)
                        }
                        (componentContext as BackHandlerOwner).backHandler.register(currentBackCallback!!)
                    }
                } else {
                    // Unregister back handler if registered
                    currentBackCallback?.let { callback ->
                        (componentContext as BackHandlerOwner).backHandler.unregister(callback)
                        currentBackCallback = null
                    }
                }
            }
        }
    }
    
    /**
     * Determines whether we should handle back press for the current panel state.
     * This implements the equivalent logic to MultiModeChildPanelsBackHandler.
     */
    private fun shouldHandleBackForCurrentState(currentPanels: ChildPanels<MC, MT, DC, DT, EC, ET>): Boolean {
        return when {
            // In SINGLE mode, handle back if details or extra panel is open
            (currentPanels.mode == ChildPanelsMode.SINGLE) && 
            (currentPanels.details != null || currentPanels.extra != null) -> true
            
            // In DUAL mode, handle back if details panel is open
            (currentPanels.mode == ChildPanelsMode.DUAL) && 
            (currentPanels.details != null) -> true
            
            // In TRIPLE mode, handle back if extra panel is open
            (currentPanels.mode == ChildPanelsMode.TRIPLE) && 
            (currentPanels.extra != null) -> true
            
            else -> false
        }
    }
    
    /**
     * Handles back press based on current panel state.
     * This implements the equivalent logic to MultiModeChildPanelsBackHandler.
     */
    private fun handleBackPress(currentPanels: ChildPanels<MC, MT, DC, DT, EC, ET>) {
        source.navigate { panels ->
            when {
                // SINGLE mode: Close extra panel first, then details panel
                (panels.mode == ChildPanelsMode.SINGLE) && (panels.extra != null) -> {
                    panels.copy(extra = null)
                }
                
                (panels.mode == ChildPanelsMode.SINGLE) && (panels.details != null) -> {
                    panels.copy(details = null)
                }
                
                // TRIPLE mode: Close extra panel and switch to DUAL mode
                (panels.mode == ChildPanelsMode.TRIPLE) && (panels.extra != null) -> {
                    panels.copy(extra = null, mode = ChildPanelsMode.DUAL)
                }
                
                // DUAL mode: Close details panel and switch to SINGLE mode
                (panels.mode == ChildPanelsMode.DUAL) && (panels.details != null) -> {
                    panels.copy(details = null, mode = ChildPanelsMode.SINGLE)
                }
                
                // No handling needed, let other handlers do their job
                else -> panels
            }
        }
    }
}

/**
 * Extension function to create StandardDecomposeChildPanelsPoC with simplified API.
 * This provides a drop-in replacement for the custom `customizableBackHandlerChildPanels` function.
 */
@ExperimentalSerializationApi
@ExperimentalDecomposeApi
fun <MC : Any, MT : Any, DC : Any, DT : Any> ComponentContext.standardChildPanelsWithBackHandler(
    source: PanelsNavigation<MC, DC, Nothing>,
    serializers: Pair<KSerializer<MC>, KSerializer<DC>>?,
    initialPanels: () -> Panels<MC, DC, Nothing>,
    key: String = "DefaultChildPanels",
    multiModeBackHandling: Boolean = false,
    onStateChanged: (newState: Panels<MC, DC, Nothing>, oldState: Panels<MC, DC, Nothing>?) -> Unit = { _, _ -> },
    mainFactory: (configuration: MC, ComponentContext) -> MT,
    detailsFactory: (configuration: DC, ComponentContext) -> DT,
): Value<ChildPanels<MC, MT, DC, DT, Nothing, Nothing>> {
    val poc = StandardDecomposeChildPanelsPoC(
        componentContext = this,
        source = source,
        initialPanels = initialPanels,
        key = key,
        serializers = serializers?.let { Triple(it.first, it.second, NothingSerializer()) },
        mainFactory = mainFactory,
        detailsFactory = detailsFactory,
        extraFactory = { _, _ -> error("Can't instantiate Nothing") },
        multiModeBackHandling = multiModeBackHandling,
        onStateChanged = onStateChanged
    )
    return poc.panels
}

/**
 * Extension function to create StandardDecomposeChildPanelsPoC with full three-pane support.
 */
@ExperimentalDecomposeApi
fun <MC : Any, MT : Any, DC : Any, DT : Any, EC : Any, ET : Any> ComponentContext.standardChildPanelsWithBackHandler(
    source: PanelsNavigation<MC, DC, EC>,
    serializers: Triple<KSerializer<MC>, KSerializer<DC>, KSerializer<EC>>?,
    initialPanels: () -> Panels<MC, DC, EC>,
    key: String = "DefaultChildPanels",
    multiModeBackHandling: Boolean = false,
    onStateChanged: (newState: Panels<MC, DC, EC>, oldState: Panels<MC, DC, EC>?) -> Unit = { _, _ -> },
    mainFactory: (configuration: MC, ComponentContext) -> MT,
    detailsFactory: (configuration: DC, ComponentContext) -> DT,
    extraFactory: (configuration: EC, ComponentContext) -> ET,
): Value<ChildPanels<MC, MT, DC, DT, EC, ET>> {
    val poc = StandardDecomposeChildPanelsPoC(
        componentContext = this,
        source = source,
        initialPanels = initialPanels,
        key = key,
        serializers = serializers,
        mainFactory = mainFactory,
        detailsFactory = detailsFactory,
        extraFactory = extraFactory,
        multiModeBackHandling = multiModeBackHandling,
        onStateChanged = onStateChanged
    )
    return poc.panels
}