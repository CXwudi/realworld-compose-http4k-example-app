# Compose UI

The structure of the UI is basically just following the tree structure of the decompose module. Usually each node in the tree in the decompose module will have a corresponding Composable function in the Compose module.

## Local Utils

The Compose module contains some our own spacing and padding settings in [`LocalSpace`](../../conduit-frontend/frontend-compose-ui/src/commonMain/kotlin/mikufan/cx/conduit/frontend/ui/theme/Space.kt) composition local. Hence when defining spacing and padding using `.dp` unit, always use the values from the `LocalSpace`, or a formula based on the values, instead of hardcoding the values

When using layout such as `Column`, `Row`, `LazyVerticalGrid`, etc, prefer to build-in parameters, such as  `verticalArrangement` and `horizontalArrangement` for `Column` for example, to specify the base spacing between the items. Then use `Spacer` with `LocalSpace` composition local if you need to specify some custom spacing between two particular items.

## UI Guidance

The root Composable only contains a `Surface` with `fillMaxSize()` modifier and `background` color set to `MaterialTheme.colorScheme.background` (see [`MainUI.kt`](../../conduit-frontend/frontend-compose-ui/src/commonMain/kotlin/mikufan/cx/conduit/frontend/ui/MainUI.kt)), so no default padding from the root Composable. Each screen should add its own padding.

For `Column` and `Row` (and their Lazy variants), prefer to only set the padding on the direction. For example, a `Row` should only set `modifier.padding(horizontal = LocalSpace.current...)` and a `Column` should only set `modifier.padding(vertical = LocalSpace.current...)`. This gives maximum flexibility to the layout setup.

The Android app has `enableEdgeToEdge()` and we don't have a global one-fit-all WindowInsets padding on root Composable. Instead, each screen setup its own WindowInsets padding. Usually, applying `WindowInsets.systemBars` and `WindowInsets.ime` is enough.
