# Standard Decompose PoC - Usage Guide

## Overview

This PoC demonstrates how to replace custom Decompose solutions with standard library implementations using a dynamic back handler approach.

## Files Structure

```
custom/
├── StandardDecomposeChildPanelsPoC.kt           # Core PoC implementation
├── StandardDecomposeValidation.kt               # Compilation validation
├── StandardDecomposeChildPanelsPocTest.kt       # Test scenarios
├── STANDARD_DECOMPOSE_POC_FINDINGS.md           # Detailed findings
├── README_POC.md                                # This file
└── main/feed/
    └── ArticlesListDetailNav.component.poc.kt  # Real-world usage example
```

## Quick Start

### 1. Basic Usage

Replace your custom implementation:

```kotlin
// Before: Custom implementation
customizableBackHandlerChildPanels(
    source = panelNavigation,
    handleBackButton = true,
    backHandler = MultiModeChildPanelsBackHandler(),
    // ... other parameters
)

// After: Standard implementation
standardChildPanelsWithBackHandler(
    source = panelNavigation,
    multiModeBackHandling = true, // Simple boolean flag
    // ... other parameters
)
```

### 2. Advanced Usage

For more control, use the full API:

```kotlin
with(StandardDecomposeChildPanelsPoC()) {
    standardChildPanelsWithDynamicBackHandler(
        source = panelNavigation,
        serializers = serializers,
        initialPanels = initialPanels,
        backHandlerMode = BackHandlerMode.MULTI_MODE,
        mainFactory = mainFactory,
        detailsFactory = detailsFactory
    )
}
```

## Testing the PoC

### 1. Compilation Test

```kotlin
// Check that everything compiles
validatePoCCompilation()
```

### 2. Behavior Validation

```kotlin
val validation = StandardDecomposeValidation()
validation.validateStandardImplementation(componentContext)
validation.validateCustomImplementation(componentContext)
validation.validateBehaviorEquivalence(componentContext)
```

### 3. Real-world Integration

Use the `ArticlesListDetailNav.component.poc.kt` as a reference for migrating your actual components.

## Key Features

### ✅ **Dynamic Back Handler**
- Automatically registers/unregisters based on panel state
- Avoids conflicts with parent/child components
- Proper lifecycle management

### ✅ **Behavior Modes**
- `SINGLE_MODE`: Only handles back in SINGLE mode (like `SingleModeChildPanelsBackHandler`)
- `MULTI_MODE`: Handles back in all modes (like `MultiModeChildPanelsBackHandler`)
- `DISABLED`: Disables custom back handling

### ✅ **Drop-in Replacement**
- Minimal changes to existing code
- Compatible with existing usage patterns
- Maintains the same API surface

## Migration Steps

### Step 1: Add PoC Files
Copy the PoC files to your project.

### Step 2: Update Imports
```kotlin
// Add these imports
import mikufan.cx.conduit.frontend.logic.component.custom.standardChildPanelsWithBackHandler
```

### Step 3: Replace Function Call
```kotlin
// Replace customizableBackHandlerChildPanels with standardChildPanelsWithBackHandler
val panels = standardChildPanelsWithBackHandler(
    source = panelNavigation,
    serializers = serializers,
    initialPanels = initialPanels,
    multiModeBackHandling = true, // true = MultiMode, false = SingleMode
    mainFactory = mainFactory,
    detailsFactory = detailsFactory
)
```

### Step 4: Test Thoroughly
- Test all navigation scenarios
- Verify back button behavior
- Check for handler conflicts
- Validate responsive behavior

### Step 5: Remove Custom Code
After verification, remove:
- `ChildPanelsBackHandler.kt`
- `CustomizableBackHandlerChildPanelsFactory.kt`
- Related custom implementations

## Testing Scenarios

### Critical Test Cases

1. **Initial State**
   - SINGLE mode, no details panel
   - Main component visible

2. **Open Details (SINGLE Mode)**
   - Navigate to details
   - Verify SINGLE mode, details visible
   - Back button should close details

3. **Open Details (DUAL Mode)**
   - Navigate to details in DUAL mode
   - Verify DUAL mode, both panels visible
   - Back button should close details, switch to SINGLE

4. **Handler Conflicts**
   - Multiple components with back handlers
   - Verify proper precedence
   - No overriding issues

5. **Lifecycle Management**
   - Component creation/destruction
   - Handler cleanup
   - Memory leak prevention

### Performance Testing

- Monitor memory usage during handler registration/cleanup
- Verify no memory leaks from coroutine scopes
- Check back button responsiveness

## Troubleshooting

### Common Issues

1. **"ComponentContext must implement BackHandlerOwner"**
   - Ensure your ComponentContext implements BackHandlerOwner
   - This is standard in Decompose 3.x

2. **Back Handler Not Working**
   - Check that `multiModeBackHandling` is set correctly
   - Verify panel state is as expected
   - Check for conflicts with other handlers

3. **Memory Leaks**
   - Ensure proper lifecycle management
   - Check coroutine scope cleanup
   - Verify handler unregistration

### Debug Tips

- Enable logging in `StandardDecomposeChildPanelsPoC`
- Monitor panel state changes
- Track handler registration/unregistration
- Use component lifecycle callbacks

## Performance Considerations

- Dynamic registration has minimal overhead
- Handler cleanup is automatic
- Memory usage is optimized
- No performance regressions expected

## Compatibility

- **Decompose**: 3.3.0+ (tested with 3.3.0)
- **Essenty**: 2.5.0+ (tested with 2.5.0)
- **Kotlin**: 2.2.0+ (tested with 2.2.0)
- **Platforms**: All KMP targets supported

## Next Steps

1. **Review the PoC** - Understand the implementation
2. **Test thoroughly** - Verify behavior matches expectations
3. **Migrate incrementally** - Start with one component
4. **Monitor performance** - Check for any regressions
5. **Remove custom code** - Clean up after successful migration

## Support

If you encounter issues:
1. Check the `STANDARD_DECOMPOSE_POC_FINDINGS.md` for detailed analysis
2. Review the test scenarios in `StandardDecomposeChildPanelsPocTest.kt`
3. Compare with the working example in `ArticlesListDetailNav.component.poc.kt`

---

*PoC Status: Ready for Testing*  
*Last Updated: 2025-07-08*