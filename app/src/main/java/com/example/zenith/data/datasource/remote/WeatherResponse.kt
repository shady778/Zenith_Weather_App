package com.example.zenith.data.datasource.remote

import com.google.gson.annotations.SerializedName


data class WeatherResponse(
    val name: String,
    val main: MainData,
    val weather: List<WeatherDescription>,
    val wind: WindData,
    val sys: SysData,
    val dt: Long
)

data class ForecastResponse(
    val list: List<ForecastItem>,
    val city: CityData
)

data class ForecastItem(
    val dt: Long,
    val main: MainData,
    val weather: List<WeatherDescription>,
    @SerializedName("dt_txt") val dt_txt: String
)

data class MainData(
    val temp: Double,
    val temp_min: Double,
    val temp_max: Double,
    val humidity: Int
)

data class WeatherDescription(
    val description: String,
    val icon: String
)

data class WindData(val speed: Double)
data class SysData(val country: String)
data class CityData(val name: String, val country: String)