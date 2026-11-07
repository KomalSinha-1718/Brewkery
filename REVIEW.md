# Senior Android review of this submission

**Reviewer stance:** hiring-bar review of a 4–6 hour take-home. Honest, including what is *not* verified.

## 0. Verification status (read first)
| Check | Status |
|---|---|
| Code written against the real API schema (`data.json` + `api/items/1.json` fetched) | ✅ |
| Pricing scenario from the design mock re-computed independently ($9.40 / $0.75 / $12.65; qty case $18.59) | ✅ |
| Compiled with Gradle / run on an emulator | ❌ **Not done** — the authoring sandbox has no Android SDK. |
| Unit tests executed | ❌ Not executed (same reason). Written to be deterministic and JVM-only. |
| Debug APK built | ❌ You must build it (`./gradlew assembleDebug`). |

Treat the first Android Studio sync as the real review. Likely friction points, if any: dependency/AGP version
mismatches, a missing import, Compose API drift. Each is a one-line fix.

## 1. Requirement checklist
| Requirement | Result |
|---|---|
| Kotlin project, Retrofit + coroutines, fetch JSON | ✅ `BrewkeryApi`, `MenuRepository` |
| Screen 1: banner, categories row, items list (thumb, badge, title, rating, price) | ✅ plus search + category filter |
| Active order tracking after placing | ✅ `ActiveOrderCard` on Menu |
| Screen 2: hero, info, ingredients, sizes / milk / sugar, price recalculation | ✅ unit price live; button = unit × qty |
| Screen 3: cart list, quantities, subtotal + $2.50 + 8% tax | ✅ matches mock to the cent |
| Screen 4: place order → clear cart, ticket id, `PREPARING` | ✅ `BK-#####`, back-stack trimmed |
| Images via Coil | ✅ |
| Loading + error state | ✅ with Try again |
| ≥1 unit test | ✅ 5 files, ~30 tests |
| README + AI section | ✅ (you must personalise the prompts) |
| Room bonus | ➖ skipped deliberately (scope) |

## 2. Scorecard
| Area | Score | Why |
|---|---|---|
| Spec / design fidelity | 9/10 | Matches mock math exactly; cart rows omit the sugar choice to mirror the mock (stored in the line, one-line change to show). |
| Architecture | 8.5/10 | Clean layering, pure `PriceCalculator`, UDF, testable VMs with injected clock-free dependencies. Manual DI is a conscious trade-off. |
| Correctness & edge cases | 8.5/10 | Cents, half-up tax, merge keys, qty cap, empty-cart guard, double-tap safety, cancellation-safe `Result`. |
| Testing | 8/10 | Meaningful domain + VM + repository coverage. No Compose UI tests. |
| UI polish | 7.5/10 | Faithful structure; fonts differ from mock (system font vs. Plus Jakarta); emoji icons; no shimmer/animations. |
| Accessibility | 7/10 | Roles + labels on custom controls; stepper targets are 36dp (<48dp guideline). |
| Production readiness | 6.5/10 | No persistence, analytics, offline cache, or CI — appropriate for scope. |
| **Overall (if it builds and runs clean)** | **≈ 8 / 10** | Strong take-home; differentiators are the data-quirk handling and the tests. |

## 3. Data/API quirks found (talk about these in the interview)
1. Option ids repeat across items (`sz_medium`, `m_oat`) → cart key must include `itemId`.
2. A price surcharge is embedded in text (`"…Honey (+0.40)"`) → parsed in the mapper.
3. `raw.githubusercontent.com` serves JSON as `text/plain` → fine with the kotlinx converter (ignores response type).
4. The menu payload already holds the full item → avoid a redundant network call per tap.
5. Category `item_count` is not used for filtering (derived from the list itself, so it can't go stale).
6. Mock shows shortened names in the cart ("Caramel Macchiato"); the app uses the real name.

## 4. Risks & trade-offs
* **No Hilt** — fine at this size; mention how you'd migrate.
* **Cart lost on process death** — allowed by brief; a Room `CartDao` is the natural bonus.
* **Static `PREPARING`** — no polling/real status machine.
* **Emoji as icons** — zero asset cost, but rendering varies by OEM font.
* **`FlowRow` is `@ExperimentalLayoutApi`** at this Compose BOM; opt-in is localised to one file.

## 5. Interview prep — be ready to explain
1. Why `Long` cents and why `HALF_UP`? (float drift: `4.85*100 = 485.00000000000006`)
2. Why does tax exclude delivery? How did you verify? (mock arithmetic)
3. Walk through the data flow from tap on "Add to Cart" to the cart bar updating. (VM → `CartRepository` `StateFlow` → `combine` in `MenuViewModel`)
4. Why `stateIn(WhileSubscribed(5_000))` and why seed with the current value?
5. Why is `getItem` cache-first? When would you prefer the network?
6. How would you add Room? (`CartLineEntity`, DAO returning `Flow`, repository maps entity↔domain, same `lines` API so UI is untouched)
7. How would you test the Compose screens? (`createComposeRule`, fake VM state, semantics matchers)
8. What did the AI get wrong and how did you catch it?

## 6. Before you submit
- [ ] Open in Android Studio → sync → run `./gradlew testDebugUnitTest`
- [ ] Run on emulator; compare each screen with the 4 screenshots
- [ ] Fix anything the compiler flags (and note it in the README "AI got wrong" section — that's *good* signal)
- [ ] Replace prompts 2–3 in the README with your real ones
- [ ] `./gradlew assembleDebug`; optional 1–2 min screen recording
- [ ] Push to a **public** GitHub repo; email link + APK to **Hr@clickretina.co.in** (subject: *Brewkery Android Assignment Submission*)
