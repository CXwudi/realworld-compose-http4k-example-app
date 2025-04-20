# Compose UI

The structure of the UI is basically just following the tree structure of the decompose module. Usually each node in the tree in the decompose module will have a corresponding Composable function in the Compose module.

The Compose module contains some our own spacing and padding settings in [`LocalSpace`](../../conduit-frontend/frontend-compose-ui/src/commonMain/kotlin/mikufan/cx/conduit/frontend/ui/theme/Space.kt) composition local, when generating Compose UI code with layouts, always use the values from the `LocalSpace`, or a formula based on the values, instead of hardcoding the values

When using layout such as `Column`, `Row`, `LazyVerticalGrid`, etc, prefer to build-in parameters, such as  `verticalArrangement` and `horizontalArrangement` for `Column` for example, to specify the base spacing between the items. Then if you need to specify some custom spacing between two particular items, use the `LocalSpace` composition local to calculate the custom spacing and then put `Spacer` between the items.
