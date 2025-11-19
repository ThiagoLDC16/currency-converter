package com.example.currencyconversor.domain.repository

import com.example.currencyconversor.data.model.Currency
import com.example.currencyconversor.domain.model.ConversionResult

interface CurrencyRepository {
    suspend fun getCurrencies(): Result<List<Currency>>
    suspend fun convertCurrency(
        from: String,
        to: String,
        amount: Double
    ): Result<ConversionResult>
}