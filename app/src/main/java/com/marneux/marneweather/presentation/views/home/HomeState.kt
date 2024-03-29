package com.marneux.marneweather.presentation.views.home

import com.marneux.marneweather.model.location.AutoSuggestLocation
import com.marneux.marneweather.model.weather.BriefWeatherDetails
import com.marneux.marneweather.model.weather.HourlyForecast

data class HomeState(
    val weatherDetailsCurrentLocation: BriefWeatherDetails? = null,
    val hourlyForecastsCurrentLocation: List<HourlyForecast>? = null,
    val autofillSuggestions: List<AutoSuggestLocation> = emptyList(),
    val weatherSavedLocations: List<BriefWeatherDetails> = emptyList(),
    val isLoadingAutofillSuggestions: Boolean = false,
    val isLoadingSavedLocations: Boolean = false,
    val isLoadingWeatherCurrentLocation: Boolean = false,
    val errorFetchWeatherCurrentLocation: Boolean = false,
    val errorFetchWeatherSavedLocations: Boolean = false,
    val errorFetchAutofillSuggestions: Boolean = false,
)