
# TipSplit — Tip Calculator & Bill Splitter

**Course:** Mobile App Development — Midterm Project  
**App Choice:** Option #5 — Tip Calculator & Bill Splitter



## Table of Contents
1. [Overview of the Project](#1-overview-of-the-project)
2. [Event Handling & Data Flow](#2-event-handling--data-flow)
3. [Deep Dive into the Code](#3-deep-dive-into-the-code)
4. [Live Demo (App Walkthrough)](#4-live-demo-app-walkthrough)
5. [Technical Reflection Questions](#5-technical-reflection-questions)




## 1. Overview of the Project

### ~ App Choice & Purpose
I chose **Option #5: The Tip Calculator & Bill Splitter**. 
The app is a clean, single-screen utility designed to help users calculate a tip, total bill, and per-person split in real-time. 
*   **UI Elements:** It features a bill amount `TextField`, a row of Material 3 `FilterChip`s for quick tip percentages (10%, 15%, 20%, plus a custom option), and a row of `+`/`-` buttons to adjust the number of people splitting the bill.
*   **Output:** A breakdown card displays the tip amount, total bill, and per-person share.

### ~ State Management Approach

I used a **single `TipUiState` data class** wrapped inside a `StateFlow`. 

> **Motivation:** 
> The three main inputs (`billInput`, `tipPercentage`, `splitCount`) are tightly coupled because every output depends on all three. Bundling them into a single immutable data class ensures **atomic updates**—the UI will never see a half-updated state (e.g., a new bill amount with an old tip percentage). 
> 
> Additionally, derived values (like `totalBill` and `perPersonExact`) are implemented as **computed getters** inside the data class. This means they automatically recalculate without needing extra mutable state variables.

### ~ ViewModel Overview

The `TipViewModel` exposes a single `uiState: StateFlow<TipUiState>` to the UI, alongside key event functions:
*   `onBillChanged(newValue: String)`
*   `onTipSelected(percent: Int)`
*   `onIncrementSplit()` / `onDecrementSplit()`
*   `onRoundingSelected(mode: RoundingMode)`


## 2. Event Handling & Data Flow

Let's trace one core interaction: **The user typing into the Bill amount field.**

### ~ User Event (Composable)
When the user types, the `OutlinedTextField` triggers its `onValueChange` event, which calls the hoisted callback:
```kotlin
OutlinedTextField(
    value = state.billInput,
    onValueChange = onBillChange, // Triggers the event
    // ...
)
```

### ~ ViewModel Processing
The call goes into `TipViewModel.onBillChanged(newValue)`. 
The ViewModel first **validates** the input using a Regex to reject non-numeric characters and prevent more than two decimal places. If valid, it updates the state:

```kotlin
fun onBillChanged(newValue: String) {
    val isValid = newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}$"))
    if (isValid) {
        _uiState.update { it.copy(billInput = newValue) } // Updates state
    }
}
```

### ~ UI Update (Recomposition)
The `TipScreen` uses `collectAsStateWithLifecycle()` to observe the `StateFlow`. 
When the ViewModel emits the new `TipUiState`, the Composable receives it and triggers a **recomposition**. Because values like `tipAmount` are computed getters, they instantly re-evaluate and display the new totals on the screen.





## 3. Deep Dive into the Code

### ~ State Hoisting & Composites
The UI is split into two functions to keep it clean:
*    **`TipScreen` (Stateful Wrapper):** Connects to the ViewModel and extracts the current state.
*    **`TipContent` (Stateless UI):** Accepts the `TipUiState` and event callbacks as parameters. It contains **no** `remember` blocks or ViewModel references. This keeps the UI testable and easily previewable.

### ~ LazyColumn Implementation
The app uses a `LazyColumn` to display the per-person breakdown. 
```kotlin
LazyColumn {
    items(
        count = state.splitCount,
        key = { index -> "person_$index" } // Unique Keys!
    ) { index -> 
        // Row UI here...
    }
}
```
By providing a unique `key` for each item, Compose can preserve the identity of each row. If the split count changes, Compose only recomposes the rows that were added or removed.

### ~ Material 3 UI
*   `OutlinedTextField`: For clean numeric input.
*   `FilterChip`: For tip and rounding options (communicates "single selection" better than radio buttons).
*   `Card`: To visually group the breakdown metrics.



## 4. Live Demo (App Walkthrough)

*   **Happy Path:** Type `100` in the bill. Tap `20%`. Tap `+` twice to split between 3 people. The LazyColumn shows 3 rows updating instantly.
*   **Edge Cases:**
    *   *Invalid Input:* Try typing `abc`. The ViewModel Regex rejects it.
    *   *Divide by Zero:* Tap `−` until split count is 1. The button disables itself to prevent a crash.
*  **Extra Feature (Rounding):** Enter `$128` with a `15%` tip, split 2 ways. Click **Round Down**. Each person pays `$73.00`. A red warning appears: **"$1.20 short of total"**. This shows real-world product thinking by alerting the user to the leftover cents.



## 5. Technical Reflection Questions

### Q1. State Design Motivation
> **If you used a single UIState data class, why did you choose that pattern?**

- I used a single `TipUiState` data class because the inputs are interdependent. A single immutable state object guarantees atomic updates, preventing UI glitches. The trade-off is that changing any single field causes the entire state object to update, which could cause unnecessary recompositions in a massive app. However, for a 3-input screen, the simplicity and bug-prevention heavily outweigh the minor performance trade-off.

### Q2. Recomposition & LazyColumns
> **How does Compose handle recomposition in a LazyColumn when an item updates, and why is using unique keys important for performance?**
- A `LazyColumn` only composes items currently visible on screen. When an item changes, Compose uses its unique `key` to find that specific slot in the composition. If the key is stable, only that item recomposes. Without keys, Compose uses positional identity—if you insert an item at the top, every item below shifts, forcing Compose to recompose the entire list. Unique keys (like `"person_$index"`) ensure Compose accurately reuses existing UI nodes.

### Q3. ViewModel Independence
> **Why is managing state inside a ViewModel beneficial for handling configuration changes?**
- When an Android device rotates, the Activity and Composable tree are destroyed and recreated. If the state lived in the Composable, the user's input would be lost. Because the ViewModel is retained by the system across configuration changes, the `uiState` survives the rotation. It also cleanly separates business logic from UI, making the app easier to test.
