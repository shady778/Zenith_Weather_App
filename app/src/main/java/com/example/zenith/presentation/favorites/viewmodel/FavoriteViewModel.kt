package com.example.zenith.presentation.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zenith.data.datasource.local.database.FavoriteCityEntity
import com.example.zenith.data.repo.WeatherRepository
import com.example.zenith.data.model.WeatherData
import com.example.zenith.data.model.localizeNumbers
import com.example.zenith.data.datasource.remote.WeatherResponse // تأكد من المسار
import com.example.zenith.data.local.datastore.UserSettings // تأكد من المسار
import kotlinx.coroutines.flow.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch
import com.example.zenith.utils.NetworkMonitor

// --- Models & Sealed Classes ---

data class FavoriteCity(
    val id: Int,
    val name: String,
    val country: String,
    val tempC: Float,
    val localTime: String,
    val description: String,
    val condition: WeatherCondition,
    val entity: FavoriteCityEntity,
    val isArabic: Boolean = false,
    val tempUnit: String = "CELSIUS"
)

sealed class WeatherCondition(
    val icon: ImageVector,
    val tint: Color,
    val gradientStart: Color
) {
    object Sunny : WeatherCondition(Icons.Rounded.WbSunny, Color(0xFFFFD600), Color(0xFFFFAB40))
    object Cloudy : WeatherCondition(Icons.Rounded.Cloud, Color(0xFF90A4AE), Color(0xFFCFD8DC))
    object Rainy : WeatherCondition(Icons.Rounded.WaterDrop, Color(0xFF4FC3F7), Color(0xFF81D4FA))
    object Night : WeatherCondition(Icons.Rounded.NightsStay, Color(0xFF9FA8DA), Color(0xFF3F51B5))
    object Thunder : WeatherCondition(Icons.Rounded.Thunderstorm, Color(0xFFFF9800), Color(0xFFFF5722))
    object Snow : WeatherCondition(Icons.Rounded.AcUnit, Color(0xFFAFBFFF), Color(0xFFE3F2FD))

    companion object {
        fun fromIconCode(iconCode: String): WeatherCondition {
            return when {
                iconCode.startsWith("01") && iconCode.endsWith("d") -> Sunny
                iconCode.startsWith("01") && iconCode.endsWith("n") -> Night
                iconCode.startsWith("02") || iconCode.startsWith("03") || iconCode.startsWith("04") -> Cloudy
                iconCode.startsWith("09") || iconCode.startsWith("10") -> Rainy
                iconCode.startsWith("11") -> Thunder
                iconCode.startsWith("13") -> Snow
                else -> Sunny
            }
        }
    }
}

sealed class FavoriteUiState {
    object Loading : FavoriteUiState()
    data class Success(val cities: List<FavoriteCity>) : FavoriteUiState()
    data class Error(val message: String) : FavoriteUiState()
    object Empty : FavoriteUiState()
}

data class FavoriteDetailState(
    val weatherData: WeatherData? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

// --- ViewModel ---

class FavoriteViewModel(
    private val repository: WeatherRepository,
    private val networkMonitor: NetworkMonitor,
    private val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher = kotlinx.coroutines.Dispatchers.IO
) : ViewModel() {

    val isOnline: StateFlow<Boolean> = networkMonitor.isConnected
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private val _uiState = MutableStateFlow<FavoriteUiState>(FavoriteUiState.Loading)
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    private val _detailState = MutableStateFlow(FavoriteDetailState())
    val detailState: StateFlow<FavoriteDetailState> = _detailState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            combine(
                repository.allFavorites,
                repository.settingsDataStore.settingsFlow,
                networkMonitor.isConnected
            ) { entities, settings, online -> Triple(entities, settings, online) }
                .collect { (entities, settings, online) ->
                    if (entities.isEmpty()) {
                        _uiState.value = FavoriteUiState.Empty
                        return@collect
                    }

                    _uiState.value = FavoriteUiState.Loading

                    val units = if (settings.tempUnit == "FAHRENHEIT") "imperial" else "metric"
                    val lang = if (settings.language == "ARABIC") "ar" else "en"

                    val cities = entities.map { entity ->
                        async(ioDispatcher) {
                            try {
                                val response = repository.remoteDataSource.getCurrentWeather(
                                    entity.lat, entity.lon, units, lang
                                )
                                mapToFavoriteCity(entity, response, settings)
                            } catch (e: Exception) {
                                createOfflineFavoriteCity(entity, settings, online, e)
                            }
                        }
                    }.awaitAll()

                    _uiState.value = FavoriteUiState.Success(cities)
                }
        }
    }

    private fun mapToFavoriteCity(
        entity: FavoriteCityEntity,
        response: WeatherResponse,
        settings: UserSettings
    ): FavoriteCity {
        val isArabic = settings.language == "ARABIC"
        return FavoriteCity(
            id = entity.id,
            name = response.name.ifEmpty { entity.name },
            country = response.sys.country.ifEmpty { entity.country },
            tempC = formatTemperature(response.main.temp, settings.tempUnit),
            localTime = formatLocalTime(response.timezone, isArabic),
            description = response.weather.getOrNull(0)?.description
                ?.replaceFirstChar { it.uppercase() } ?: "Clear",
            condition = WeatherCondition.fromIconCode(response.weather.getOrNull(0)?.icon ?: ""),
            entity = entity,
            isArabic = isArabic,
            tempUnit = settings.tempUnit
        )
    }

    private fun createOfflineFavoriteCity(
        entity: FavoriteCityEntity,
        settings: UserSettings,
        online: Boolean,
        e: Exception
    ): FavoriteCity {
        val isArabic = settings.language == "ARABIC"
        val offlineMsg = if (isArabic) "غير متصل" else "Offline"
        return FavoriteCity(
            id = entity.id,
            name = entity.name,
            country = entity.country,
            tempC = 0f,
            localTime = "--:--",
            description = if (!online) offlineMsg else "Error: ${e.localizedMessage?.take(10)}",
            condition = WeatherCondition.Sunny,
            entity = entity,
            isArabic = isArabic,
            tempUnit = settings.tempUnit
        )
    }

    private fun formatLocalTime(timezoneOffset: Int, isArabic: Boolean): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.add(Calendar.SECOND, timezoneOffset)
        val locale = if (isArabic) Locale("ar") else Locale.ENGLISH
        val sdf = SimpleDateFormat("hh:mm a", locale).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return sdf.format(calendar.time).localizeNumbers(isArabic)
    }

    private fun formatTemperature(temp: Double, unit: String): Float {
        return when (unit) {
            "KELVIN" -> (temp + 273.15).toFloat()
            else -> temp.toFloat()
        }
    }

    fun addCity(city: FavoriteCityEntity) {
        viewModelScope.launch { repository.insert(city) }
    }

    fun removeCity(city: FavoriteCityEntity) {
        viewModelScope.launch { repository.delete(city) }
    }

    fun viewDetail(lat: Double, lon: Double) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            repository.getWeatherDataForLocation(lat, lon)
                .catch { e -> _detailState.update { it.copy(isLoading = false, error = e.localizedMessage) } }
                .collect { result ->
                    result.fold(
                        onSuccess = { data ->
                            _detailState.update { it.copy(isLoading = false, weatherData = data) }
                        },
                        onFailure = { e ->
                            _detailState.update { it.copy(isLoading = false, error = e.localizedMessage) }
                        }
                    )
                }
        }
    }

    fun clearDetail() {
        _detailState.update { FavoriteDetailState() }
    }
}