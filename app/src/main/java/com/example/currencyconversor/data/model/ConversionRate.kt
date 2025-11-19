package com.example.currencyconversor.data.model

data class ConversionRate(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)