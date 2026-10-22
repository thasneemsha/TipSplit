package com.example.tipsplit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/** STATE HOISTING:
 * If user type $100 into the text field and rotate the phone,
 * the UI is destroyed and recreated. If the UI held the state, the screen goes blank.
 * By putting the state in the ViewModel (via hoisting), the text field remembers $100 even after rotation. */

// ─── STATEFUL WRAPPER ─────────────────────────────────────────────
// This connects the ViewModel to the UI.
//It knows about the ViewModel. It pulls the data out.
@Composable
fun TipScreen(vm: TipViewModel = viewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    // STATE HOISTING: We pass the state and the event callbacks down to TipContent
    TipContent(
        state = state,
        onBillChange = vm::onBillChanged,
        onTipSelect = vm::onTipSelected,
        onIncrement = vm::onIncrementSplit,
        onDecrement = vm::onDecrementSplit,
        onRoundingSelect = vm::onRoundingSelected,
    )
}

// ─── STATELESS CONTENT ────────────────────────────────────────────
// This is a pure UI function. It takes state in, and sends events out.
//It doesn't know the ViewModel exists. You just hand it the data
@Composable
fun TipContent(
    state: TipUiState,
    onBillChange: (String) -> Unit,
    onTipSelect: (Int) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRoundingSelect: (RoundingMode) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("TipSplit", style = MaterialTheme.typography.headlineMedium)

        // 1. BILL INPUT TEXT FIELD
        OutlinedTextField(
            value = state.billInput,
            onValueChange = onBillChange, // Triggers the ViewModel event
            label = { Text("Bill amount ($)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        // 2. TIP CHIPS (10%, 15%, 20%, Custom)
        Text("Tip %", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(10, 15, 20).forEach { pct ->
                FilterChip(
                    selected = state.tipPercentage == pct,
                    onClick = { onTipSelect(pct) },
                    label = { Text("$pct%") },
                )
            }
            FilterChip(
                selected = state.tipPercentage !in listOf(10, 15, 20),
                onClick = { onTipSelect(25) }, // Simple custom option
                label = { Text("Custom (25%)") },
            )
        }

        // 3. ROUNDING CHIPS (Our extra product feature)
        Text("Rounding", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = state.roundingMode == RoundingMode.EXACT,
                onClick = { onRoundingSelect(RoundingMode.EXACT) },
                label = { Text("Exact") },
            )
            FilterChip(
                selected = state.roundingMode == RoundingMode.DOWN,
                onClick = { onRoundingSelect(RoundingMode.DOWN) },
                label = { Text("Round Down") },
            )
            FilterChip(
                selected = state.roundingMode == RoundingMode.UP,
                onClick = { onRoundingSelect(RoundingMode.UP) },
                label = { Text("Round Up") },
            )
        }

        // 4. SPLIT COUNT (+ / - Buttons)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Split between:")
            Button(onClick = onDecrement, enabled = state.splitCount > 1) {
                Text("−")
            }
            Text("${state.splitCount}", style = MaterialTheme.typography.titleLarge)
            Button(onClick = onIncrement) {
                Text("+")
            }
            Text(if (state.splitCount == 1) "person" else "people")
        }

        // 5. BREAKDOWN CARD
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text("Breakdown", style = MaterialTheme.typography.titleMedium)
                Text("Tip: $${"%.2f".format(state.tipAmount)}")
                Text("Total: $${"%.2f".format(state.totalBill)}")

                // Show Rounded amount prominently, with exact amount as subtitle
                Text(
                    "Per person: $${"%.2f".format(state.perPersonRounded)}",
                    style = MaterialTheme.typography.titleLarge,
                )

                // If rounding is used, show the exact amount AND the delta warning
                if (state.roundingMode != RoundingMode.EXACT) {
                    Text(
                        "(exact: $${"%.2f".format(state.perPersonExact)} each)",
                        style = MaterialTheme.typography.bodySmall,
                    )

                    val delta = state.roundingDelta
                    val deltaText = when {
                        delta > 0.001 -> "+$${"%.2f".format(delta)} extra collected"
                        delta < -0.001 -> "$${"%.2f".format(-delta)} short of total"
                        else -> "Perfectly even"
                    }
                    Text(
                        text = deltaText,
                        color = MaterialTheme.colorScheme.error, // Makes it red so it stands out
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // 6. LAZYCOLUMN (Required for the assignment)
        Text("Each person pays", style = MaterialTheme.typography.labelLarge)
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(
                count = state.splitCount,
                key = { index -> "person_$index" }, // Unique keys!
            ) { index ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("Person ${index + 1}")
                        Text("$${"%.2f".format(state.perPersonRounded)}")
                    }
                }
            }
        }
    }
}