package com.marneux.marneweather.data.weather.mapper

import com.marneux.marneweather.data.weather.database.CurrentWeatherEntity
import com.marneux.marneweather.data.weather.remote.models.CurrentWeatherResponse
import com.marneux.marneweather.data.weather.remote.models.HourlyWeatherInfoResponse
import com.marneux.marneweather.data.weather.remote.models.getWeatherIconResForCode
import com.marneux.marneweather.data.weather.remote.models.getWeatherImageForCode
import com.marneux.marneweather.model.location.Coordinates
import com.marneux.marneweather.model.location.SavedLocation
import com.marneux.marneweather.model.weather.BriefWeatherDetails
import com.marneux.marneweather.model.weather.CurrentWeather
import com.marneux.marneweather.model.weather.HourlyForecast
import com.marneux.marneweather.model.weather.PrecipitationProbability
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.roundToInt

fun CurrentWeather.toBriefWeatherDetails() = BriefWeatherDetails(
    nameLocation = nameLocation,
    currentTemperatureRoundedToInt = temperatureRoundedToInt,
    shortDescription = weatherCondition,
    shortDescriptionIcon = iconResId,
    coordinates = coordinates
)

fun HourlyWeatherInfoResponse.toHourlyForecasts(): List<HourlyForecast> {
    val hourlyForecastList = mutableListOf<HourlyForecast>()
    for (i in hourlyForecast.timestamps.indices) {
        val epochSeconds = hourlyForecast.timestamps[i].toLong()
        val correspondingLocalTime = LocalDateTime
            .ofInstant(
                Instant.ofEpochSecond(epochSeconds),
                ZoneId.systemDefault()
            )
        val weatherIconResId = getWeatherIconResForCode(
            weatherCode = hourlyForecast.weatherCodes[i],
            isDay = correspondingLocalTime.hour < 19
        )
        val hourlyForecast = HourlyForecast(
            dateTime = correspondingLocalTime,
            weatherIconResId = weatherIconResId,
            temperature = hourlyForecast.temperatureForecasts[i].roundToInt()
        )
        hourlyForecastList.add(hourlyForecast)
    }
    return hourlyForecastList
}

fun BriefWeatherDetails.toSavedWeatherLocationEntity() =
    CurrentWeatherEntity(
        nameLocation = nameLocation,
        latitude = coordinates.latitude,
        longitude = coordinates.longitude
    )

fun CurrentWeatherEntity.toSavedLocation() = SavedLocation(
    nameLocation = nameLocation,
    coordinates = Coordinates(latitude = latitude, longitude = longitude)
)

fun HourlyWeatherInfoResponse.toPrecipitationProbabilities(): List<PrecipitationProbability> {
    val probabilitiesList = mutableListOf<PrecipitationProbability>()
    for (i in hourlyForecast.timestamps.indices) {
        val epochSeconds = hourlyForecast.timestamps[i].toLong()
        val correspondingLocalDateTime = LocalDateTime
            .ofInstant(
                Instant.ofEpochSecond(epochSeconds),
                ZoneId.systemDefault()
            )

        val precipitationProbability = PrecipitationProbability(
            dateTime = correspondingLocalDateTime,
            probabilityPercentage = hourlyForecast.precipitationProbabilityPercentages[i],
            latitude = latitude,
            longitude = longitude
        )
        probabilitiesList.add(precipitationProbability)
    }
    return probabilitiesList
}

fun CurrentWeatherResponse.toCurrentWeather(nameLocation: String) = CurrentWeather(
    temperatureRoundedToInt = currentWeatherData.temperature.roundToInt(),
    nameLocation = nameLocation,
    weatherCondition = weatherCodeToDescriptionMap.getValue(currentWeatherData.weatherCode),
    isDay = currentWeatherData.isDay,
    iconResId = getWeatherIconResForCode(
        weatherCode = currentWeatherData.weatherCode,
        isDay = currentWeatherData.isDay == 1
    ),
    imageResId = getWeatherImageForCode(
        weatherCode = currentWeatherData.weatherCode,
        isDay = currentWeatherData.isDay == 1
    ),
    coordinates = Coordinates(
        latitude = latitude,
        longitude = longitude,
    )
)


private val weatherCodeToDescriptionMap = mapOf(
    0 to "Clear sky",
    1 to "Mainly clear",
    2 to "Partly cloudy",
    3 to "Overcast",
    45 to "Fog",
    48 to "Depositing rime fog",
    51 to "Drizzle",
    53 to "Drizzle",
    55 to "Drizzle",
    56 to "Freezing drizzle",
    57 to "Freezing drizzle",
    61 to "Slight rain",
    63 to "Moderate rain",
    65 to "Heavy rain",
    66 to "Light freezing rain",
    67 to "Heavy freezing rain",
    71 to "Slight snow fall",
    73 to "Moderate snow fall",
    75 to "Heavy snow fall",
    77 to "Snow grains",
    80 to "Slight rain showers",
    81 to "Moderate rain showers",
    82 to "Violent rain showers",
    85 to "Slight snow showers",
    86 to "Heavy snow showers",
    95 to "Thunderstorms",
    96 to "Thunderstorms with slight hail",
    99 to "Thunderstorms with heavy hail",
)