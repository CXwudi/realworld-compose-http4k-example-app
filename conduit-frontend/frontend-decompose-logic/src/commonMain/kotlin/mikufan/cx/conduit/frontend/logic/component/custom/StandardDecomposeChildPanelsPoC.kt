@file:OptIn(ExperimentalDecomposeApi::class)

package mikufan.cx.conduit.frontend.logic.component.custom

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.GenericComponentContext
import com.arkivanov.decompose.router.panels.ChildPanels
import com.arkivanov.decompose.router.panels.ChildPanelsMode
import com.arkivanov.decompose.router.panels.Panels
import com.arkivanov.decompose.router.panels.PanelsNavigation
import com.arkivanov.decompose.router.panels.childPanels
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.essenty.statekeeper.SerializableContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.NothingSerializer

/**
 * PoC: Standard Decompose childPanels with dynamic back handler
 * 
 * This demonstrates how to replace custom solutions with standard Decompose APIs
 * while maintaining the same dynamic back handling behavior.
 */
class StandardDecomposeChildPanelsPoC {

    /**
     * Creates child panels using standard Decompose API with dynamic back handler registration.
     * 
     * This approach:
     * 1. Uses standard Decompose childPanels() API
     * 2. Implements dynamic back handler registration based on panel state
     * 3. Avoids conflicts with parent/child component back handlers
     * 4. Maintains the same behavior as the custom implementation
     */
    fun <Ctx, MC : Any, MT : Any, DC : Any, DT : Any> Ctx.standardChildPanelsWithDynamicBackHandler(
        source: PanelsNavigation<MC, DC, Nothing>,
        serializers: Pair<KSerializer<MC>, KSerializer<DC>>?,
        initialPanels: () -> Panels<MC, DC, Nothing>,
        key: String = "DefaultChildPanels",
        onStateChanged: (newState: Panels<MC, DC, Nothing>, oldState: Panels<MC, DC, Nothing>?) -> Unit = { _, _ -> },
        backHandlerMode: BackHandlerMode = BackHandlerMode.MULTI_MODE,
        mainFactory: (configuration: MC, Ctx) -> MT,
        detailsFactory: (configuration: DC, Ctx) -> DT,
    ): Value<ChildPanels<MC, MT, DC, DT, Nothing, Nothing>> where Ctx : GenericComponentContext<Ctx>, Ctx : BackHandlerOwner {

        // Create standard child panels without custom back handling
        val panels = childPanels(
            source = source,
            serializers = serializers?.let { Triple(it.first, it.second, NothingSerializer()) },
            initialPanels = initialPanels,
            key = key,
            onStateChanged = onStateChanged,
            // Don't use standard back handling - we'll implement our own
            handleBackButton = false,
            mainFactory = mainFactory,
            detailsFactory = detailsFactory,
        )

        // Set up dynamic back handler registration
        setupDynamicBackHandler(panels, backHandlerMode, source)

        return panels
    }

    /**
     * Overloaded version with save/restore functionality
     */
    fun <Ctx, MC : Any, MT : Any, DC : Any, DT : Any> Ctx.standardChildPanelsWithDynamicBackHandler(
        source: PanelsNavigation<MC, DC, Nothing>,
        initialPanels: () -> Panels<MC, DC, Nothing>,
        savePanels: (Panels<MC, DC, Nothing>) -> SerializableContainer?,
        restorePanels: (SerializableContainer) -> Panels<MC, DC, Nothing>?,
        key: String = "DefaultChildPanels",
        onStateChanged: (newState: Panels<MC, DC, Nothing>, oldState: Panels<MC, DC, Nothing>?) -> Unit = { _, _ -> },
        backHandlerMode: BackHandlerMode = BackHandlerMode.MULTI_MODE,
        mainFactory: (configuration: MC, Ctx) -> MT,
        detailsFactory: (configuration: DC, Ctx) -> DT,
    ): Value<ChildPanels<MC, MT, DC, Nothing, Nothing, Nothing>> where Ctx : GenericComponentContext<Ctx>, Ctx : BackHandlerOwner {

        // Create standard child panels without custom back handling
        val panels = childPanels(
            source = source,
            initialPanels = initialPanels,
            savePanels = savePanels,
            restorePanels = restorePanels,
            key = key,
            onStateChanged = onStateChanged,
            // Don't use standard back handling - we'll implement our own
            handleBackButton = false,
            mainFactory = mainFactory,
            detailsFactory = detailsFactory,
        )

        // Set up dynamic back handler registration
        setupDynamicBackHandler(panels, backHandlerMode, source)

        return panels
    }

    /**
     * Sets up dynamic back handler registration that responds to panel state changes
     */
    private fun <Ctx, MC : Any, DC : Any> Ctx.setupDynamicBackHandler(
        panels: Value<ChildPanels<MC, *, DC, *, Nothing, Nothing>>,
        backHandlerMode: BackHandlerMode,
        source: PanelsNavigation<MC, DC, Nothing>
    ) where Ctx : GenericComponentContext<Ctx>, Ctx : BackHandlerOwner {
        
        // Track current back handler registration
        val currentBackHandlerRegistration = MutableStateFlow<com.arkivanov.essenty.backhandler.BackHandler.Registration?>(null)
        
        // Monitor panel state changes and register/unregister back handler accordingly
        coroutineScope().launch {
            panels.collectLatest { currentPanels ->
                // Determine if we should handle back button based on current state
                val shouldHandleBack = when (backHandlerMode) {
                    BackHandlerMode.SINGLE_MODE -> {
                        // Only handle back in SINGLE mode when details panel is open
                        currentPanels.mode == ChildPanelsMode.SINGLE && currentPanels.details != null
                    }
                    BackHandlerMode.MULTI_MODE -> {
                        // Handle back in all modes when details panel is open or in dual/triple mode
                        currentPanels.details != null || currentPanels.mode != ChildPanelsMode.SINGLE
                    }
                    BackHandlerMode.DISABLED -> false
                }

                // Update back handler registration
                if (shouldHandleBack) {
                    // Unregister previous handler if exists
                    currentBackHandlerRegistration.value?.unregister()
                    
                    // Register new handler
                    val registration = backHandler.register(enabled = true) {
                        handleBackPress(currentPanels, backHandlerMode, source)
                    }
                    currentBackHandlerRegistration.value = registration
                } else {
                    // Unregister handler if not needed
                    currentBackHandlerRegistration.value?.unregister()
                    currentBackHandlerRegistration.value = null
                }
            }
        }
    }

    /**
     * Handles back button press with the same logic as the custom implementation
     */
    private fun <MC : Any, DC : Any> handleBackPress(
        panels: ChildPanels<MC, *, DC, *, Nothing, Nothing>,
        backHandlerMode: BackHandlerMode,
        source: PanelsNavigation<MC, DC, Nothing>
    ): Boolean {
        val currentPanels = Panels(
            main = panels.main.configuration,
            details = panels.details?.configuration,
            extra = null,
            mode = panels.mode
        )

        val newPanels = when (backHandlerMode) {
            BackHandlerMode.SINGLE_MODE -> handleBackPressSingleMode(currentPanels)
            BackHandlerMode.MULTI_MODE -> handleBackPressMultiMode(currentPanels)
            BackHandlerMode.DISABLED -> null
        }

        return if (newPanels != null) {
            source.navigate { newPanels }
            true // Consumed the back press
        } else {
            false // Let other handlers handle it
        }
    }

    /**
     * Single mode back handling - same as SingleModeChildPanelsBackHandler
     */
    private fun <MC : Any, DC : Any> handleBackPressSingleMode(
        panels: Panels<MC, DC, Nothing>
    ): Panels<MC, DC, Nothing>? = when {
        (panels.mode == ChildPanelsMode.SINGLE) && (panels.details != null) -> {
            panels.copy(details = null)
        }
        else -> null
    }

    /**
     * Multi mode back handling - same as MultiModeChildPanelsBackHandler
     */
    private fun <MC : Any, DC : Any> handleBackPressMultiMode(
        panels: Panels<MC, DC, Nothing>
    ): Panels<MC, DC, Nothing>? = when {
        (panels.mode == ChildPanelsMode.SINGLE) && (panels.details != null) -> {
            panels.copy(details = null)
        }
        (panels.mode == ChildPanelsMode.DUAL) && (panels.details != null) -> {
            panels.copy(details = null, mode = ChildPanelsMode.SINGLE)
        }
        // Note: TRIPLE mode handling would go here if needed
        else -> null
    }

    /**
     * Configuration for back handler behavior
     */
    enum class BackHandlerMode {
        /** Only handle back in SINGLE mode (matches SingleModeChildPanelsBackHandler) */
        SINGLE_MODE,
        /** Handle back in all modes (matches MultiModeChildPanelsBackHandler) */
        MULTI_MODE,
        /** Disable custom back handling */
        DISABLED
    }
}

/**
 * Convenience extension functions for easier usage
 */
@ExperimentalDecomposeApi
fun <Ctx, MC : Any, MT : Any, DC : Any, DT : Any> Ctx.standardChildPanelsWithBackHandler(
    source: PanelsNavigation<MC, DC, Nothing>,
    serializers: Pair<KSerializer<MC>, KSerializer<DC>>?,
    initialPanels: () -> Panels<MC, DC, Nothing>,
    key: String = "DefaultChildPanels",
    onStateChanged: (newState: Panels<MC, DC, Nothing>, oldState: Panels<MC, DC, Nothing>?) -> Unit = { _, _ -> },
    multiModeBackHandling: Boolean = true,
    mainFactory: (configuration: MC, Ctx) -> MT,
    detailsFactory: (configuration: DC, Ctx) -> DT,
): Value<ChildPanels<MC, MT, DC, DT, Nothing, Nothing>> where Ctx : GenericComponentContext<Ctx>, Ctx : BackHandlerOwner {
    return with(StandardDecomposeChildPanelsPoC()) {
        standardChildPanelsWithDynamicBackHandler(
            source = source,
            serializers = serializers,
            initialPanels = initialPanels,
            key = key,
            onStateChanged = onStateChanged,
            backHandlerMode = if (multiModeBackHandling) {
                StandardDecomposeChildPanelsPoC.BackHandlerMode.MULTI_MODE
            } else {
                StandardDecomposeChildPanelsPoC.BackHandlerMode.SINGLE_MODE
            },
            mainFactory = mainFactory,
            detailsFactory = detailsFactory,
        )
    }
}