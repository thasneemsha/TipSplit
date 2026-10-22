package com.example.tipsplit

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TipViewModel : ViewModel() {

    // I used StateFlow instead of mutableStateOf
    // because StateFlow is lifecycle-aware and thread-safe.
    // It keeps my ViewModel completely independent of Compose UI libraries.
    private val _uiState = MutableStateFlow(TipUiState())
    val uiState: StateFlow<TipUiState> = _uiState.asStateFlow()

    /** EVENT: User types in the Bill field (prevents letters/multiple decimals) */
    fun onBillChanged(newValue: String) {
        val isValid = newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}$"))
        if (isValid) _uiState.update { it.copy(billInput = newValue) }
    }

    /** EVENT: User clicks a tip chip (10, 15, 20, etc)*/
    fun onTipSelected(percent: Int) {
        _uiState.update { it.copy(tipPercentage = percent.coerceIn(0, 100)) }
    }

    /** EVENT: User clicks the "+" button : how many people are splitting the bill */
    fun onIncrementSplit() {
        _uiState.update { it.copy(splitCount = it.splitCount + 1) }
    }

    /** EVENT: User clicks the "-" button (prevents going below 1 person) */
    fun onDecrementSplit() {
        _uiState.update { it.copy(splitCount = (it.splitCount - 1).coerceAtLeast(1)) }
    }

    /** EVENT: User clicks Rounding chips */
    fun onRoundingSelected(mode: RoundingMode) {
        _uiState.update { it.copy(roundingMode = mode) }
    }
}