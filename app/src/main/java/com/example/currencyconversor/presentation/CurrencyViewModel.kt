package com.example.currencyconversor.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconversor.data.model.Currency
import com.example.currencyconversor.domain.model.ConversionResult
import com.example.currencyconversor.domain.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CurrencyUiState(
    val currencies: List<Currency> = emptyList(),
    val fromCurrency: Currency? = null,
    val toCurrency: Currency? = null,
    val amount: String = "",
    val conversionResult: ConversionResult? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    init {
        loadCurrencies()
    }

    private fun loadCurrencies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.getCurrencies()
                .onSuccess { currencies ->
                    _uiState.update { state ->
                        state.copy(
                            currencies = currencies,
                            fromCurrency = currencies.firstOrNull { it.code == "USD" },
                            toCurrency = currencies.firstOrNull { it.code == "BRL" },
                            isLoading = false
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Erro ao carregar moedas: ${exception.message}"
                        )
                    }
                }
        }
    }

    fun onFromCurrencySelected(currency: Currency) {
        _uiState.update { it.copy(fromCurrency = currency, conversionResult = null) }
    }

    fun onToCurrencySelected(currency: Currency) {
        _uiState.update { it.copy(toCurrency = currency, conversionResult = null) }
    }

    fun onAmountChanged(amount: String) {
        _uiState.update { it.copy(amount = amount, conversionResult = null) }
    }

    fun convertCurrency() {
        val state = _uiState.value
        val from = state.fromCurrency ?: return
        val to = state.toCurrency ?: return
        val amount = state.amount.toDoubleOrNull() ?: return

        if (amount <= 0) {
            _uiState.update { it.copy(error = "Digite um valor válido") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.convertCurrency(from.code, to.code, amount)
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            conversionResult = result,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Erro na conversão: ${exception.message}"
                        )
                    }
                }
        }
    }

    fun swapCurrencies() {
        val state = _uiState.value
        _uiState.update {
            it.copy(
                fromCurrency = state.toCurrency,
                toCurrency = state.fromCurrency,
                conversionResult = null
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}