package com.marneux.marneweather.domain.usecases.weather

import com.marneux.marneweather.domain.repositories.weather.WeatherRepository
import com.marneux.marneweather.model.weather.BriefWeatherDetails

class DeleteWeatherLocationFromSavedItemUseCase(
    private val weatherRepository: WeatherRepository
) {
    suspend fun execute(
        briefWeatherDetails: BriefWeatherDetails
    ) {
        return weatherRepository.deleteWeatherLocationFromSavedItems(
            briefWeatherDetails
        )
    }
}