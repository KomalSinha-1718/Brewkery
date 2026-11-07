# Brewkery — Android take-home (Kotlin · Jetpack Compose)

A native Android coffee & bakery ordering app.
Flow: Menu → Item Detail → Cart → Order Status.

| Menu | Item Detail | Cart | Order Status |
|---|---|---|---|
| Store banner, search, category chips, item cards, cart bar, active-order card | Hero, ingredients, size / milk / sugar with live price | Quantities, subtotal + $2.50 delivery + 8% tax | Ticket `#BK-xxxxx`, `PREPARING` |

## Tech stack
Kotlin · Jetpack Compose (Material 3) · Navigation Compose · ViewModel + StateFlow · Retrofit + OkHttp ·
kotlinx.serialization · Coil · Coroutines · JUnit 4 + kotlinx-coroutines-test.


## Architecture
```
ui/        Compose screens + ViewModels (state in, events out; no business logic in composables)
domain/    Pure Kotlin: models, Money, PriceCalculator  ← every price on screen comes from here
data/      MenuRepository (Retrofit + in-memory cache), CartRepository, OrderRepository, DTO→domain mappers
```
Unidirectional data flow. ViewModels expose one immutable `UiState` via `StateFlow`; screens render it.
Manual DI (`AppContainer`) instead of Hilt — 3 repositories don't justify the annotation-processing cost
  for a 4–6 hour scope. Swapping to Hilt later only touches `AppContainer` and the ViewModel factories.
Money is `Long` cents everywhere; doubles from JSON are converted once at the mapper boundary.
Cart / order repositories are singletons held by the container, so Menu, Detail and Cart share state and
  the cart survives rotation. It resets when the process dies (explicitly allowed by the brief).

## Tests (`app/src/test`)
`PriceCalculatorTest` — option math, the exact $9.40 / $2.50 / $0.75 / $12.65 scenario from the mock, quantity, empty cart, half-up rounding
`MoneyTest` — float→cents drift (`4.85 * 100`), formatting
`CartRepositoryTest` — merge, separate configs, same option ids on different items, remove-at-zero, max quantity
`MenuRepositoryTest` — DTO→domain mapping, missing optional fields, surcharge parsing, cache vs. fallback, failure path
`CartViewModelTest` — order placement, cart cleared, ticket/status/count/total, empty-cart + double-tap guard

## How I used AI

Tools: ChatGPT (code assistance), GitHub Copilot (code suggestions and correction/solutions), Claude (review and documentation)

Prompts (2–3 real ones):
1. help me understand the project requirements and divide the whole project developement into smaller task.
2. implement the functionality while keeping the project structure unchanged 
3. what is wrong in this code why it is giving error here. suggest a solution and explain it also 


One thing AI got right: AI helped me in making my task simpler and saving my time which can be utilised to develop the more intreracting ideas. it helped me identify better way to structure my code and gave me a cleaner implementation for some features. also helped me finding error faster and improving it.

One thing AI got wrong — at first when i asked copilot for the correct solution when i got an error it does not gave me the fully matching existing project structure which caused me some issue so i checked for the issue i was getting and again explained it to the copilot and the mismatch that was appearing and modified it according to that
