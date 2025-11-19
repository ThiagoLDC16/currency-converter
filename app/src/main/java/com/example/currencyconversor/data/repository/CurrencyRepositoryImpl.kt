package com.example.currencyconversor.data.repository

import com.example.currencyconversor.data.model.Currency
import com.example.currencyconversor.data.remote.FrankfurterApi
import com.example.currencyconversor.domain.model.ConversionResult
import com.example.currencyconversor.domain.repository.CurrencyRepository
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val api: FrankfurterApi
) : CurrencyRepository {

    override suspend fun getCurrencies(): Result<List<Currency>> {
        return try {
            val currenciesMap = api.getCurrencies()
            val currencies = currenciesMap.map { (code, name) ->
                Currency(code, name)
            }.sortedBy { it.code }
            Result.success(currencies)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun convertCurrency(
        from: String,
        to: String,
        amount: Double
    ): Result<ConversionResult> {
        return try {
            val response = api.getLatestRates(from, to, amount)
            val rate = response.rates[to] ?: 0.0
            val convertedAmount = amount * rate

            val result = ConversionResult(
                fromCurrency = from,
                toCurrency = to,
                originalAmount = amount,
                convertedAmount = convertedAmount,
                rate = rate,
                date = response.date
            )
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}