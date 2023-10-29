package com.marneux.marneweather.data.remote.languagemodel

object TextGeneratorClientConstants {
    /**
     * The base URL of the [TextGeneratorClient]'s API.
     */
    const val BASE_URL = "https://api.openai.com/v1/chat/"
    const val OPEN_AI_API_TOKEN = ""

    object Endpoints {

        const val CHAT_COMPLETION_END_POINT = "completions"
    }
}
