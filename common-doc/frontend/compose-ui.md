# Compose UI

The structure of the UI is basically just following the tree structure of the decompose module. Usually each node in the tree in the decompose module will have a corresponding Composable function in the Compose module.

## Local Utils

The Compose module contains some our own spacing and padding settings in [`LocalSpace`](../../conduit-frontend/frontend-compose-ui/src/commonMain/kotlin/mikufan/cx/conduit/frontend/ui/theme/Space.kt) composition local. Hence when defining spacing and padding using `.dp` unit, always use the values from the `LocalSpace`, or a formula based on the values, instead of hardcoding the values

## UI Guidance

The root Composable only contains a `Surface` with `fillMaxSize()` modifier and `background` color set to `MaterialTheme.colorScheme.background` (see [`MainUI.kt`](../../conduit-frontend/frontend-compose-ui/src/commonMain/kotlin/mikufan/cx/conduit/frontend/ui/MainUI.kt)), so no default padding from the root Composable. Each screen should add its own padding.

When using layout such as `Column`, `Row`, `LazyVerticalGrid`, etc, first specify the base spacing using build-in parameters, such as  `verticalArrangement` and `horizontalArrangement` for `Column` for example. Then use `Spacer` with `LocalSpace` composition local if you need to specify some custom spacing between two particular items.

For `Column` and `Row`, prefer to only set the padding on the direction. For example, a `Row` should only set `modifier.padding(horizontal = LocalSpace.current...)` and a `Column` should only set `modifier.padding(vertical = LocalSpace.current...)`. This gives maximum flexibility to the layout setup.

The Android app has `enableEdgeToEdge()` and we don't have a global one-fit-all WindowInsets padding on root Composable. Instead, each screen setup its own WindowInsets padding. Usually, applying `WindowInsets.safeDrawing` and `WindowInsets.ime` is enough.

When applied `WindowInsets` padding, check if the current or parent layout already have some padding other than `WindowInsets` applied (typically `modifier.padding()`, `Scaffold`'s inner padding, etc). If so, use `consumeWindowInsets` so that the `WindowInsets` padding will not be added to the existing padding. Otherwise, it will cause double padding.

When using lazy layouts such as `LazyRow`, `LazyColumn`, `LazyVerticalGrid`, etc, prefer to use `contentPadding` parameter instead of `padding` modifier, so that contents can scroll under the system bar, camera, etc. In order to apply both `WindowInsets` and `LocalSpace` padding, you can use `.asPaddingValues()` on `WindowInsets` (e.g. `WindowInsets.safeDrawing.asPaddingValues()`) and do a `max()` operation with `LocalSpace` padding on each side of the padding.

## Compose Guidance

When creating Composable that need to pass variables delegated from `State<>`, prefer to pass the `State<T>` variable instead of the `T` variable. This is because `State<T>` is traded as an immutable variable by Compose. Hence value changes in `State<T>` will not trigger a whole recomposition of the Composable like `T` does. And only the part of the Composable that actually read the `State<T>` will be recomposed.
