package com.example.zenith.data.model

import com.example.zenith.data.datasource.remote.ForecastResponse
import com.example.zenith.data.datasource.remote.WeatherResponse
import java.text.SimpleDateFormat
import java.util.*

fun mapResponseToData(current: WeatherResponse, forecast: ForecastResponse): WeatherData {
    val sdf = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault())

    val hourly = forecast.list.take(8).map {
        val hourSdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        HourlyForecast(
            time = hourSdf.format(Date(it.dt * 1000L)),
            temp = "${it.main.temp.toInt()}°",
            icon = it.weather[0].icon
        )
    }

    val daily = forecast.list.filter { it.dt_txt.contains("12:00:00") }.map {
        val daySdf = SimpleDateFormat("EEEE", Locale.getDefault())
        DailyForecast(
            day = daySdf.format(Date(it.dt * 1000L)),
            highTemp = "${it.main.temp_max.toInt()}°",
            lowTemp = "${it.main.temp_min.toInt()}°",
            description = it.weather[0].description,
            icon = it.weather[0].icon
        )
    }

    return WeatherData(
        localTime = sdf.format(Date()),
        city = current.name,
        country = current.sys.country,
        temperature = "${current.main.temp.toInt()}°",
        description = current.weather[0].description,
        clouds = "${current.clouds.all}",
        pressure = "${current.main.pressure}",
        icon = current.weather[0].icon,
        humidity = "${current.main.humidity}%",
        windSpeed = "${current.wind.speed} km/h",
        hourlyForecast = hourly,
        dailyForecast = daily,
        isDay = current.weather[0].icon.contains("d")
    )
}