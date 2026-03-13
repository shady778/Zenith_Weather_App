package com.example.zenith.data.model

import com.example.zenith.data.datasource.remote.ForecastResponse
import com.example.zenith.data.datasource.remote.WeatherResponse
import com.example.zenith.data.local.datastore.UserSettings
import java.text.SimpleDateFormat
import java.util.*

fun String.toArabicNumerals(): String {
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    val sb = StringBuilder()
    for (ch in this) {
        if (ch in '0'..'9') sb.append(arabicDigits[ch - '0']) else sb.append(ch)
    }
    return sb.toString()
}

fun String.localizeNumbers(isArabic: Boolean): String {
    return if (isArabic) this.toArabicNumerals() else this
}

fun convertTempFromMetric(celsiusValue: Double, unit: String): Int {
    return when (unit) {
        "KELVIN" -> (celsiusValue + 273.15).toInt()
        "FAHRENHEIT" -> (celsiusValue * 9 / 5 + 32).toInt()
        else -> celsiusValue.toInt()
    }
}

fun mapResponseToData(
    current: WeatherResponse,
    forecast: ForecastResponse,
    settings: UserSettings
): WeatherData {
    val isArabic = settings.language == "ARABIC"
    val locale = if (isArabic) Locale("ar") else Locale.ENGLISH
    val sdf = SimpleDateFormat("EEEE, dd MMM", locale)

    val tempUnit = settings.tempUnit
    val isImperialApi = tempUnit == "FAHRENHEIT"

    val hourly = forecast.list.take(8).map {
        val hourSdf = SimpleDateFormat("HH:mm", locale)
        val temp = if (isImperialApi) it.main.temp.toInt() else convertTempFromMetric(it.main.temp, tempUnit)
        HourlyForecast(
            time = hourSdf.format(Date(it.dt * 1000L)).localizeNumbers(isArabic),
            temp = "${temp}°".localizeNumbers(isArabic),
            icon = it.weather.firstOrNull()?.icon ?: "01d"
        )
    }

    val daily = forecast.list.filter { it.dt_txt.contains("12:00:00") }.map {
        val daySdf = SimpleDateFormat("EEEE", locale)
        val high = if (isImperialApi) it.main.temp_max.toInt() else convertTempFromMetric(it.main.temp_max, tempUnit)
        val low = if (isImperialApi) it.main.temp_min.toInt() else convertTempFromMetric(it.main.temp_min, tempUnit)
        DailyForecast(
            day = daySdf.format(Date(it.dt * 1000L)),
            highTemp = "${high}°".localizeNumbers(isArabic),
            lowTemp = "${low}°".localizeNumbers(isArabic),
            description = it.weather.firstOrNull()?.description ?: "No data",
            icon = it.weather.firstOrNull()?.icon ?: "01d"
        )
    }

    val temperatureUnit = when (tempUnit) {
        "FAHRENHEIT" -> "°F"
        "KELVIN" -> "K"
        else -> "°C"
    }

    val apiWindSpeed = current.wind.speed
    val convertedWindSpeed = when {
        isImperialApi && settings.windUnit == "MS" -> apiWindSpeed / 2.23694
        !isImperialApi && settings.windUnit == "MPH" -> apiWindSpeed * 2.23694
        else -> apiWindSpeed
    }

    val windUnitText = if (settings.windUnit == "MPH") (if (isArabic) "ميل/س" else "mph") else (if (isArabic) "م/ث" else "m/s")

    val currentTemp = if (isImperialApi) current.main.temp.toInt() else convertTempFromMetric(current.main.temp, tempUnit)

    return WeatherData(
        localTime = sdf.format(Date()).localizeNumbers(isArabic),
        city = current.name,
        country = current.sys.country,
        temperature = "${currentTemp}$temperatureUnit".localizeNumbers(isArabic),
        description = current.weather.firstOrNull()?.description ?: "No data",
        clouds = "${current.clouds.all}".localizeNumbers(isArabic),
        pressure = "${current.main.pressure}".localizeNumbers(isArabic),
        icon = current.weather.firstOrNull()?.icon ?: "01d",
        humidity = "${current.main.humidity}%".localizeNumbers(isArabic),
        windSpeed = "${String.format(Locale.ENGLISH, "%.1f", convertedWindSpeed)} $windUnitText".localizeNumbers(isArabic),
        hourlyForecast = hourly,
        dailyForecast = daily,
        isDay = current.weather.firstOrNull()?.icon?.contains("d") ?: true,
        lat = current.coord?.lat ?: 0.0,
        lon = current.coord?.lon ?: 0.0,
        isArabic = isArabic
    )
}