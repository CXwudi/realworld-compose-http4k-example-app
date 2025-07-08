# Standard Decompose PoC Findings

## Overview

This document contains the findings and corrected implementation for replacing custom Decompose solutions with standard library implementations.

## Issues Identified in Previous PoC

### 1. **Incorrect Value Observation**
**Issue**: Used `collectLatest()` on Decompose `Value<>` which doesn't exist.
```kotlin
// ❌ INCORRECT - Value doesn't have collectLatest()
panels.collectLatest { currentPanels ->
    // ...
}
```

**Solution**: Use proper Decompose `Value` observation APIs:
```kotlin
// ✅ CORRECT - Using Value.subscribe()
panels.subscribe { currentPanels ->
    // ...
}

// ✅ CORRECT - Convert to StateFlow using utility
val panelsStateFlow = panels.toStateFlow(componentContext.coroutineScope())
panelsStateFlow.collectLatest { currentPanels ->
    // ...
}
```

### 2. **Missing Extra Pane Handling**
**Issue**: Previous PoC only handled main and detail panes, omitting extra pane support.

**Solution**: Implemented full three-pane support in the corrected PoC:
- Main pane (always present)
- Detail pane (optional)
- Extra pane (optional) - **Now properly supported**

## Corrected Implementation

### 1. **StandardDecomposeChildPanelsPoC.kt**

Key features:
- Uses standard Decompose `childPanels()` API
- Implements dynamic back handler registration using `Value.subscribe()`
- Handles all three panes (main, detail, extra)
- Equivalent behavior to `MultiModeChildPanelsBackHandler`
- Proper lifecycle management with automatic cleanup

### 2. **Dynamic Back Handler Logic**

The corrected implementation uses dynamic back handler registration:

```kotlin
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
```

### 3. **Complete Three-Pane Back Handler Logic**

The corrected implementation handles all three panes properly:

```kotlin
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
```

## API Comparison

### Before (Custom Implementation)
```kotlin
val panels = customizableBackHandlerChildPanels(
    source = panelNavigation,
    key = "ArticlesListDetailPanel",
    serializers = Config.ArticlesList.serializer() to Config.ArticleDetail.serializer(),
    initialPanels = { Panels(Config.ArticlesList) },
    handleBackButton = true,
    backHandler = MultiModeChildPanelsBackHandler(),
    mainFactory = ::mainComponent,
    detailsFactory = ::detailComponent,
)
```

### After (Standard Implementation)
```kotlin
val panels = standardChildPanelsWithBackHandler(
    source = panelNavigation,
    key = "ArticlesListDetailPanel",
    serializers = Config.ArticlesList.serializer() to Config.ArticleDetail.serializer(),
    initialPanels = { Panels(Config.ArticlesList) },
    multiModeBackHandling = true, // Equivalent to MultiModeChildPanelsBackHandler
    mainFactory = ::mainComponent,
    detailsFactory = ::detailComponent,
)
```

## Benefits of Corrected Implementation

### 1. **Standard Decompose APIs**
- Uses official `childPanels()` function
- Better compatibility with future Decompose versions
- Follows established patterns and conventions

### 2. **Proper Value Observation**
- Uses correct Decompose `Value.subscribe()` API
- Converts to StateFlow using existing utilities
- Proper coroutine integration

### 3. **Dynamic Back Handler**
- Registers/unregisters based on panel state
- Avoids conflicts with other back handlers
- Proper lifecycle management

### 4. **Complete Three-Pane Support**
- Handles main, detail, and extra panes
- Equivalent behavior to `MultiModeChildPanelsBackHandler`
- Proper mode transitions

### 5. **Reduced Maintenance**
- No custom components to maintain
- Standard library updates automatically
- Cleaner codebase

## Validation

The corrected implementation includes:
- ✅ Comprehensive unit tests
- ✅ Integration example with `ArticlesListDetailNav`
- ✅ Compilation validation
- ✅ Behavior equivalence verification

## Migration Path

1. **Replace custom factory calls** with `standardChildPanelsWithBackHandler()`
2. **Update imports** to use the PoC components
3. **Set `multiModeBackHandling = true`** for equivalent behavior
4. **Test thoroughly** to ensure no regressions

## Conclusion

The corrected PoC successfully demonstrates that:
- **Standard Decompose APIs can fully replace custom implementations**
- **Dynamic back handler registration solves the override issue**
- **All three panes are properly supported**
- **Behavior is equivalent to existing custom solutions**
- **Implementation is production-ready**

The corrected solution is **ready for integration** and provides a clear migration path away from custom Decompose implementations.