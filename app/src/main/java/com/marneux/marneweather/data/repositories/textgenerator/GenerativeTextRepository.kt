package com.marneux.marneweather.data.repositories.textgenerator

import com.marneux.marneweather.data.getBodyOrThrowException
import com.marneux.marneweather.data.local.textgeneration.GeneratedTextCacheDatabaseDao
import com.marneux.marneweather.data.local.textgeneration.GeneratedTextForLocationEntity
import com.marneux.marneweather.data.remote.languagemodel.TextGeneratorClient
import com.marneux.marneweather.data.remote.languagemodel.models.MessageDTO
import com.marneux.marneweather.data.remote.languagemodel.models.TextGenerationPromptBody
import com.marneux.marneweather.domain.models.weather.CurrentWeatherDetails
import kotlinx.coroutines.CancellationException



class GenerativeTextRepository (
    private val textGeneratorClient: TextGeneratorClient,
    private val generatedTextCacheDatabaseDao: GeneratedTextCacheDatabaseDao,
) : GenerativeTextRepositoryImpl {

    override suspend fun generateTextForWeatherDetails(weatherDetails: CurrentWeatherDetails): Result<String> {
        val generatedTextEntity =
            generatedTextCacheDatabaseDao.getSavedGeneratedTextForDetails(
                nameLocation = weatherDetails.nameLocation,
                temperature = weatherDetails.temperatureRoundedToInt,
                conciseWeatherDescription = weatherDetails.weatherCondition
            )
        if (generatedTextEntity != null) return Result.success(generatedTextEntity.generatedDescription)
        // idioma hardcodeado por el moemento
        // problemas al realizar peticion en español
        //TODO asignar variable de idioma de sistema
        val systemPrompt = """
            Think that you are a dressmaker and you have to say what clothes to wear, it has to be brief,
              a brief description of the time with the parameters that I give you below, and
              then you have to tell me top, bottom, accessories, and footwear .
        """.trimIndent()
        val userPrompt = """
            location = ${weatherDetails.nameLocation};
            currentTemperature = ${weatherDetails.temperatureRoundedToInt};
            weatherCondition = ${weatherDetails.weatherCondition};
            isNight = ${weatherDetails.isDay != 1}
        """.trimIndent()
        // prompts
        val promptMessages = listOf(
            MessageDTO(role = "system", content = systemPrompt),
            MessageDTO(role = "user", content = userPrompt)
        )
        val textGenerationPrompt = TextGenerationPromptBody(
            messages = promptMessages,
            model = "gpt-3.5-turbo-0613"
        )
        // peticion de generacion de texto
        return try {
            // genera texto
            val generatedTextResponse = textGeneratorClient.getModelResponseForConversations(
                textGenerationPostBody = textGenerationPrompt
            ).getBodyOrThrowException()
                .generatedResponses
                .first().message
                .content
            // guarda el texto generado para agilizar futuras peticiones
            val generatedTextForLocationEntity = GeneratedTextForLocationEntity(
                nameLocation = weatherDetails.nameLocation,
                temperature = weatherDetails.temperatureRoundedToInt,
                conciseWeatherDescription = weatherDetails.weatherCondition,
                generatedDescription = generatedTextResponse
            )
            generatedTextCacheDatabaseDao.addGeneratedTextForLocation(generatedTextForLocationEntity)
            Result.success(generatedTextResponse)
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(exception)
        }
    }

}