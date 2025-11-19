package com.example.currencyconversor.domain.model

data class ConversionResult(
    val fromCurrency: String,
    val toCurrency: String,
    val originalAmount: Double,
    val convertedAmount: Double,
    val rate: Double,
    val date: String
)