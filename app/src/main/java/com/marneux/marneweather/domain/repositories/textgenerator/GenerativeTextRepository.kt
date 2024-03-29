package com.marneux.marneweather.domain.repositories.textgenerator

import com.marneux.marneweather.model.weather.CurrentWeather

interface GenerativeTextRepository {
    suspend fun generateTextWeatherDetails(weatherDetails: CurrentWeather): Result<String>
}
