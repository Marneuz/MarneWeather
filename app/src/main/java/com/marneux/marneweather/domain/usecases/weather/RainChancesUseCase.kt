package com.marneux.marneweather.domain.usecases.weather

import com.marneux.marneweather.domain.repositories.weather.WeatherRepository
import com.marneux.marneweather.model.weather.RainChances
import java.time.LocalDate
import java.time.LocalDateTime

class RainChancesUseCase(
    private val weatherRepository: WeatherRepository
) {
    suspend fun execute(
        latitude: String,
        longitude: String,
    ): Result<List<RainChances>> {
        return try {
            val probabilitiesForNext24hours =
                weatherRepository.rainChances(
                    latitude = latitude,
                    longitude = longitude,
                    dateRange = LocalDate.now()..LocalDate.now().plusDays(1)
                ).getOrThrow().filter {
                    val currentTime = LocalDateTime.now()
                    val isSameDay = it.dateTime.toLocalDate() == currentTime.toLocalDate()
                    if (isSameDay) it.dateTime.toLocalTime() >= currentTime.toLocalTime()
                    else it.dateTime > currentTime
                }.take(24)
            Result.success(probabilitiesForNext24hours)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}