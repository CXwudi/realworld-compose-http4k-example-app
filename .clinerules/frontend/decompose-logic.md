# Decompose Logic

The decompose module serves as the business logic and the common navigation logic for the frontend. It is diagnosed to any UI framework despite currently only Compose UI is using it.

The whole decompose module is a tree:

```mermaid
graph TD
    root-nav --> landing-page
    root-nav --> main-nav
    main-nav --> main-feed-nav
    main-nav --> favourite
    main-nav --> me-nav
    main-nav --> auth-page
    main-feed-nav --> article-list
    main-feed-nav --> article-detail
    me-nav --> me-page
    me-nav --> edit-profile
    me-nav --> add-article
```

Each node is either a component or a navigation node, with an exception of `me-nav` that is both a component and a navigation node.

## Navigation Node

A navigation node typically using one of the [Decompose routing](https://arkivanov.github.io/Decompose/navigation/overview/) like Child Stack, Child Panels, etc.

A navigation node sometimes can also have dependencies from the [service layer](../../conduit-frontend/frontend-decompose-logic/src/commonMain/kotlin/mikufan/cx/conduit/frontend/logic/service).

## Component Node

A component node is a class that implements the [MviComponent](../../conduit-frontend/frontend-decompose-logic/src/commonMain/kotlin/mikufan/cx/conduit/frontend/logic/component/util/MviComponent.kt) interface, it is implemented using [Decompose's ComponentContext](https://arkivanov.github.io/Decompose/component/overview/) and [MVIKotlin's store](https://arkivanov.github.io/MVIKotlin/store.html).

For component node, it is formed by three files. The models like state, intent, etc are saved in a file postfixed with `.model.kt`. the component class is saved in a file postfixed with `.component.kt`. Lastly the MVI store is saved in a file postfixed with `.store.kt`.

The MVI store usually will use some [service layer](../../conduit-frontend/frontend-decompose-logic/src/commonMain/kotlin/mikufan/cx/conduit/frontend/logic/service) classes to do the actual business work

## Navigation Node with Component Stuff

`me-nav` is the exception where it is both a component and a navigation node. It is in fact a navigation node, but the navigation is dynamic based on the user login state, which is expressed as a MVIKotlin store. Based on the state, the number of tabs in the navigation bar changes.
This exception works because there is a clear way to map the state into a tab navigation that implemented by using a Child Stack navigation.
