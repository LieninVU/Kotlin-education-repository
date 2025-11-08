package com.example.weatherapp

data class WeatherResponse(
    val daily: Daily
)

data class Daily(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val weathercode: List<Int>,
    val precipitation_probability_max: List<Double>?
)


data class WeatherItem(
    val date: String,
    val maxTemp: Double,
    val weatherCode: Int,
    val rainProbability: Double?
)