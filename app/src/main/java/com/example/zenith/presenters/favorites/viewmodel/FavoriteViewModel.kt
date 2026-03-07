package com.example.zenith.presenters.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zenith.data.datasource.local.FavoriteCityEntity
import com.example.zenith.data.repo.WeatherRepository
import com.example.zenith.data.model.WeatherData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

data class FavoriteCity(
    val id: Int,
    val name: String,
    val country: String,
    val tempC: Float,
    val localTime: String,
    val description: String,
    val condition: WeatherCondition,
    val entity: FavoriteCityEntity
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

class FavoriteViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoriteUiState>(FavoriteUiState.Loading)
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    private val _detailState = MutableStateFlow(FavoriteDetailState())
    val detailState: StateFlow<FavoriteDetailState> = _detailState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            repository.allFavorites
                .catch { e -> _uiState.value = FavoriteUiState.Error(e.message ?: "Error") }
                .collect { entities ->
                    if (entities.isEmpty()) {
                        _uiState.value = FavoriteUiState.Empty
                    } else {
                        val cities = entities.map { entity ->
                            async(kotlinx.coroutines.Dispatchers.IO) {
                                try {
                                    val response = repository.getCurrentWeather(entity.lat, entity.lon)
                                    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                                    calendar.add(Calendar.SECOND, response.timezone.toLong().toInt())
                                    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                                    val cityTime = sdf.format(calendar.time)

                                    FavoriteCity(
                                        id = entity.id,
                                        name = response.name.ifEmpty { entity.name },
                                        country = response.sys.country.ifEmpty { entity.country },
                                        tempC = response.main.temp.toFloat(),
                                        localTime = cityTime,
                                        description = response.weather.getOrNull(0)?.description?.replaceFirstChar { it.uppercase() } ?: "Clear",
                                        condition = WeatherCondition.fromIconCode(response.weather.getOrNull(0)?.icon ?: ""),
                                        entity = entity
                                    )
                                } catch (e: Exception) {
                                    FavoriteCity(
                                        id = entity.id,
                                        name = entity.name,
                                        country = entity.country,
                                        tempC = 0f,
                                        localTime = "--:--",
                                        description = "Error: ${e.localizedMessage?.take(12)}",
                                        condition = WeatherCondition.Sunny,
                                        entity = entity
                                    )
                                }
                            }
                        }.awaitAll()
                        _uiState.value = FavoriteUiState.Success(cities)
                    }
                }
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