package com.dean.kmm_sample

import io.github.aakira.napier.Napier
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import kotlinx.datetime.*
import kotlinx.serialization.Serializable

private const val API_ENDPOINT = "https://gitcdn.link/cdn/KaterinaPetrova/greeting/7d47a42fc8d28820387ac7f4aaf36d69e434adc1/greetings.json"

@Serializable
data class Hello(
    val string: String
)

class Greeting {
    private val httpClient = httpClient {
        install(Logging) {
            level = LogLevel.HEADERS
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.v(tag = "HTTP Client", message = message)
                }
            }
        }
        install(JsonFeature) {
            val json = kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            }
            serializer = KotlinxSerializer(json)
        }
    }.also { initLogger() }

    @Throws(Exception::class)
    suspend fun greeting(): String {
        return "${getHello().random().string}, ${Platform().platform}!"
    }

    private suspend fun getHello(): List<Hello> {
        return httpClient.get(API_ENDPOINT)
    }
}

private fun daysUntilNewYear(): Int {
    val today = Clock.System.todayAt(TimeZone.currentSystemDefault())
    val closestNewYear = LocalDate(today.year + 1, 1, 1)
    return today.daysUntil(closestNewYear)
}
