package com.example.tipsplit

/**
 * ROUNDING FEATURE
 * This is an extra feature for this project
 * added to let the user decide if they want to round the split to whole dollars.
 */

// I chose an enum for the rounding mode because there are exactly three distinct states,
// and an enum prevents invalid states and typos, which is much safer than using Booleans or Strings
enum class RoundingMode { EXACT, DOWN, UP }

/**
 * Required State Variables:
 * billInput : String value for the initial bill.
 * tipPercentage : Integer or float representing the selected tip rate.
 * splitCount : Integer representing how many people are paying.
 * bundling all these into a single data class.
 *
 * REQUIREMENT MET: Event Logic (No delay/coroutine overhead)
 * Because all calculations below are computed dynamically using `get()`,
 * the moment a variable changes, the calculation updates instantly.
 */

//using data class as it can auto generate methods like .copy()
//which helps when updating new user value

data class TipUiState(

    // 1. billInput: String value for the initial bill
    val billInput: String = "",

    // 2. tipPercentage: Integer representing the selected tip rate
    // I chose 15 because it's the standard default tip in the US
    val tipPercentage: Int = 15,

    // 3. splitCount: Integer representing how many people are paying
    val splitCount: Int = 1,

    // The variable holding the rounding choice
    val roundingMode: RoundingMode = RoundingMode.EXACT,
) {
    // Safely convert the text input into a number (returns 0.0 if empty or invalid)
    val billAmount: Double
        get() = billInput.toDoubleOrNull() ?: 0.0

    // Calculate the tip amount instantly
    val tipAmount: Double
        get() = billAmount * tipPercentage / 100.0

    // Calculate the total bill (bill + tip) instantly
    val totalBill: Double
        get() = billAmount + tipAmount

    // Calculate the exact per-person share (with cents)
    val perPersonExact: Double
        get() = if (splitCount > 0) totalBill / splitCount else 0.0

    // Calculate the final rounded per-person share based on the user's choice
    val perPersonRounded: Double
        get() = when (roundingMode) {
            RoundingMode.EXACT -> perPersonExact
            RoundingMode.DOWN  -> kotlin.math.floor(perPersonExact)
            RoundingMode.UP    -> kotlin.math.ceil(perPersonExact)
        }

    // Calculate the total actually collected when using the rounded amount
    val totalCollected: Double
        get() = perPersonRounded * splitCount

    // Calculate the difference between what's collected and what's owed
    val roundingDelta: Double
        get() = totalCollected - totalBill
}