# Standard Decompose PoC - Usage Guide

This PoC demonstrates how to replace custom Decompose solutions with standard library implementations.

## Quick Start

### 1. **Drop-in Replacement**

Replace your existing custom implementation:

```kotlin
// Before: Custom implementation
val panels = customizableBackHandlerChildPanels(
    source = panelNavigation,
    key = "MyPanels",
    serializers = MainConfig.serializer() to DetailConfig.serializer(),
    initialPanels = { Panels(MainConfig()) },
    handleBackButton = true,
    backHandler = MultiModeChildPanelsBackHandler(),
    mainFactory = ::createMainComponent,
    detailsFactory = ::createDetailComponent,
)

// After: Standard implementation
val panels = standardChildPanelsWithBackHandler(
    source = panelNavigation,
    key = "MyPanels",
    serializers = MainConfig.serializer() to DetailConfig.serializer(),
    initialPanels = { Panels(MainConfig()) },
    multiModeBackHandling = true, // Equivalent to MultiModeChildPanelsBackHandler
    mainFactory = ::createMainComponent,
    detailsFactory = ::createDetailComponent,
)
```

### 2. **Full Three-Pane Support**

For components that need extra pane support:

```kotlin
val panels = componentContext.standardChildPanelsWithBackHandler(
    source = navigation,
    serializers = Triple(
        MainConfig.serializer(),
        DetailConfig.serializer(),
        ExtraConfig.serializer()
    ),
    initialPanels = { Panels(MainConfig()) },
    multiModeBackHandling = true,
    mainFactory = { config, ctx -> MainComponent(config, ctx) },
    detailsFactory = { config, ctx -> DetailComponent(config, ctx) },
    extraFactory = { config, ctx -> ExtraComponent(config, ctx) },
)
```

## API Reference

### `standardChildPanelsWithBackHandler`

#### Parameters

- **`source`**: `PanelsNavigation<MC, DC, EC>` - The navigation source
- **`serializers`**: Serializers for state persistence
  - Two-pane: `Pair<KSerializer<MC>, KSerializer<DC>>`
  - Three-pane: `Triple<KSerializer<MC>, KSerializer<DC>, KSerializer<EC>>`
- **`initialPanels`**: `() -> Panels<MC, DC, EC>` - Initial panel state
- **`key`**: `String` - Unique key for this panel navigation (default: "DefaultChildPanels")
- **`multiModeBackHandling`**: `Boolean` - Enable multi-mode back handling (default: false)
- **`onStateChanged`**: State change callback (optional)
- **`mainFactory`**: Factory for main component
- **`detailsFactory`**: Factory for detail component
- **`extraFactory`**: Factory for extra component (three-pane only)

#### Back Handler Behavior

When `multiModeBackHandling = true`:

- **SINGLE mode**: Closes extra → details → no action
- **DUAL mode**: Closes details → switches to SINGLE mode
- **TRIPLE mode**: Closes extra → switches to DUAL mode

## Migration Guide

### Step 1: Import the PoC

```kotlin
import mikufan.cx.conduit.frontend.logic.component.poc.standardChildPanelsWithBackHandler
```

### Step 2: Update Your Component

```kotlin
class MyComponent(
    componentContext: ComponentContext,
    // ... other dependencies
) : ComponentContext by componentContext {
    
    private val navigation = PanelsNavigation<MainConfig, DetailConfig, Nothing>()
    
    // Replace customizableBackHandlerChildPanels with standardChildPanelsWithBackHandler
    val panels = standardChildPanelsWithBackHandler(
        source = navigation,
        serializers = MainConfig.serializer() to DetailConfig.serializer(),
        initialPanels = { Panels(MainConfig()) },
        multiModeBackHandling = true, // Set to true for MultiModeChildPanelsBackHandler behavior
        mainFactory = ::createMainComponent,
        detailsFactory = ::createDetailComponent,
    )
    
    // Rest of your component remains the same...
}
```

### Step 3: Update Imports

Remove imports for custom implementations:
```kotlin
// Remove these imports
import mikufan.cx.conduit.frontend.logic.component.custom.customizableBackHandlerChildPanels
import mikufan.cx.conduit.frontend.logic.component.custom.MultiModeChildPanelsBackHandler
```

### Step 4: Test Thoroughly

Ensure all navigation behaviors work as expected:
- Panel opening/closing
- Back button handling
- Mode transitions
- State persistence

## Examples

### Example 1: Articles List/Detail Navigation

See `ArticlesListDetailNav.component.poc.kt` for a complete example of migrating the articles navigation component.

### Example 2: Basic Two-Pane Layout

```kotlin
@OptIn(ExperimentalDecomposeApi::class)
class TwoPaneComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    
    private val navigation = PanelsNavigation<MainConfig, DetailConfig, Nothing>()
    
    val panels = standardChildPanelsWithBackHandler(
        source = navigation,
        serializers = MainConfig.serializer() to DetailConfig.serializer(),
        initialPanels = { Panels(MainConfig("default")) },
        multiModeBackHandling = true,
        mainFactory = { config, ctx -> MainComponent(config, ctx) },
        detailsFactory = { config, ctx -> DetailComponent(config, ctx) },
    )
    
    fun openDetail(detailId: String) {
        navigation.navigate { panels ->
            panels.copy(
                details = DetailConfig(detailId),
                mode = ChildPanelsMode.DUAL
            )
        }
    }
    
    fun closeDetail() {
        navigation.navigate { panels ->
            panels.copy(details = null, mode = ChildPanelsMode.SINGLE)
        }
    }
}
```

### Example 3: Three-Pane Layout

```kotlin
@OptIn(ExperimentalDecomposeApi::class)
class ThreePaneComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    
    private val navigation = PanelsNavigation<MainConfig, DetailConfig, ExtraConfig>()
    
    val panels = standardChildPanelsWithBackHandler(
        source = navigation,
        serializers = Triple(
            MainConfig.serializer(),
            DetailConfig.serializer(),
            ExtraConfig.serializer()
        ),
        initialPanels = { Panels(MainConfig("default")) },
        multiModeBackHandling = true,
        mainFactory = { config, ctx -> MainComponent(config, ctx) },
        detailsFactory = { config, ctx -> DetailComponent(config, ctx) },
        extraFactory = { config, ctx -> ExtraComponent(config, ctx) },
    )
    
    fun openExtra(extraId: String) {
        navigation.navigate { panels ->
            panels.copy(
                extra = ExtraConfig(extraId),
                mode = ChildPanelsMode.TRIPLE
            )
        }
    }
}
```

## Testing

The PoC includes comprehensive tests in `StandardDecomposeChildPanelsPocTest.kt` demonstrating:
- Basic panel navigation
- Detail panel handling
- Extra panel handling
- State change callbacks
- Extension function usage

## Benefits

1. **Standard APIs**: Uses official Decompose `childPanels()` function
2. **Dynamic Back Handler**: No conflicts with other back handlers
3. **Complete Three-Pane Support**: Handles main, detail, and extra panes
4. **Equivalent Behavior**: Maintains all existing functionality
5. **Future-Proof**: Better compatibility with future Decompose versions
6. **Reduced Maintenance**: No custom components to maintain

## Troubleshooting

### Common Issues

1. **Back Handler Not Working**: Ensure `multiModeBackHandling = true`
2. **Compilation Errors**: Check import statements and component context usage
3. **State Not Persisting**: Verify serializers are properly configured
4. **Memory Leaks**: ComponentContext lifecycle is properly managed automatically

### Debug Tips

1. **Enable Logging**: Add logging to state change callbacks
2. **Check Panel State**: Use `panels.value` to inspect current state
3. **Verify Navigation**: Ensure navigation calls are properly structured

## Next Steps

1. **Test the PoC** in your specific use case
2. **Compare Performance** with existing implementation
3. **Plan Migration** if PoC proves successful
4. **Remove Custom Code** once migration is complete

## Conclusion

This PoC demonstrates that standard Decompose APIs can fully replace custom implementations while maintaining equivalent functionality and improving maintainability.