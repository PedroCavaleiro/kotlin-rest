@file:Suppress("unused")

package pt.pcdev.kotlin_rest

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pt.pcdev.kotlin_rest.interfaces.ApiAuthenticationInterface
import pt.pcdev.kotlin_rest.interfaces.EndpointInterface
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
//https://products.codeporting.ai/convert/swift-to-kotlin
class SwiftlyRest private constructor() {

    companion object {
        val shared = SwiftlyRest()
    }

    private var baseURL: String? = null
    private var loggingEnabled: Boolean = false
    private val tag: String = "[SwiftlyRest]"
    private var apiAuthConfiguration: ApiAuthenticationInterface? = null
    private var jwtToken: String? = null

    /**
     * Configures the base URL to be used
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param url The base URL to use
     */
    fun setBaseURL(url: String) {
        this.baseURL = url
    }

    /**
     * Configures the API Auth with the `ApiAuthenticationInterface` protocol
     * When set the headers will be automatically added to the request
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param apiAuthConfiguration The API configuration
     */
    fun configureApiAuth(apiAuthConfiguration: ApiAuthenticationInterface) {
        this.apiAuthConfiguration = apiAuthConfiguration
    }

    /**
     * Sets the JWT token for the requests
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param jwtToken The JWT to use in the requests, when not `nil` they will be automatically added to the request
     */
    fun setJwtToken(jwtToken: String?) {
        this.jwtToken = jwtToken
    }

    /**
     * Enables/Disabled the request logging
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param enabled The state of the logging
     */
    fun loggingEnabled(enabled: Boolean) {
        this.loggingEnabled = enabled
    }

    /**
     * Performs a HTTP GET request
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param endpoint The endpoint where to perform the request
     * @param headers Extra headers to send to the server
     * @return `Result<T, KotlinRestError>` Where the .Success contains the parsed response or a `KotlinRestError`
     */
    suspend fun get(
        endpoint: EndpointInterface,
        headers: Map<String, String> = emptyMap()
    ): Result<Serializable> {
        return makeRequest(endpoint, HTTPMethod.GET, null, headers)
    }

    /**
     * Performs a HTTP POST request
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param endpoint The endpoint where to perform the request
     * @param body The request body to send to the server
     * @param headers Extra headers to send to the server
     * @return `Result<T, KotlinRestError>` Where the .Success contains the parsed response or a `KotlinRestError`
     */
    suspend fun post(
        endpoint: EndpointInterface,
        body: Serializable,
        headers: Map<String, String> = emptyMap()
    ): Result<Serializable> {
        return makeRequest(endpoint, HTTPMethod.POST, body, headers)
    }

    /**
     * Performs a HTTP PATCH request
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param endpoint The endpoint where to perform the request
     * @param body The request body to send to the server
     * @param headers Extra headers to send to the server
     * @return `Result<T, KotlinRestError>` Where the .Success contains the parsed response or a `KotlinRestError`
     */
    suspend fun patch(
        endpoint: EndpointInterface,
        body: Serializable,
        headers: Map<String, String> = emptyMap()
    ): Result<Serializable> {
        return makeRequest(endpoint, HTTPMethod.PATCH, body, headers)
    }

    /**
     * Performs a HTTP PUT request
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param endpoint The endpoint where to perform the request
     * @param body The request body to send to the server
     * @param headers Extra headers to send to the server
     * @return `Result<T, KotlinRestError>` Where the .Success contains the parsed response or a `KotlinRestError`
     */
    suspend fun put(
        endpoint: EndpointInterface,
        body: Serializable,
        headers: Map<String, String> = emptyMap()
    ): Result<Serializable> {
        return makeRequest(endpoint, HTTPMethod.PUT, body, headers)
    }

    /**
     * Performs a HTTP DELETE request
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param endpoint The endpoint where to perform the request
     * @param headers Extra headers to send to the server
     * @return `Result<T, KotlinRestError>` Where the .Success contains the parsed response or a `KotlinRestError`
     */
    suspend fun delete(
        endpoint: EndpointInterface,
        headers: Map<String, String> = emptyMap()
    ): Result<Serializable> {
        return makeRequest(endpoint, HTTPMethod.DELETE, null, headers)
    }

    /**
     * Generates the headers for a request
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param method The `HTTPMethod` of the request
     * @param body The body of the request as nullable data
     * @return The headers for a request
     */
    fun generateHeaders(method: HTTPMethod, body: Serializable?): Map<String, String> {
        val headers: MutableMap<String, String> = mutableMapOf()
        apiAuthConfiguration?.let { authConfig ->
            val authHeaders = authConfig.generateHeaders(method, body, jwtToken)
            headers.putAll(authHeaders)
        } ?: jwtToken?.let {
            headers["Authorization"] = "Bearer $it"
        }

        return headers
    }

    /**
     * Performs a HTTP request to a REST API
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param endpoint The endpoint where to send the request
     * @param method The `HTTPMethod` of the request
     * @param body The body of the request, can be null
     * @param headers The extra headers to send to the server
     */
    private fun makeRequest(
        endpoint: EndpointInterface,
        method: HTTPMethod,
        body: Serializable? = null,
        headers: Map<String, String> = emptyMap()
    ): Result<Serializable> {

        if (baseURL == null)
            return Result.Failure(KotlinRestError.InvalidURL)

        try {
            endpoint.build()
        } catch (_: Exception) {
            writeLog("$tag[invalidURL] Failed to build the URL: ${endpoint.url}")
            return Result.Failure(KotlinRestError.InvalidURL)
        }

        val requestUrl = try {
            URI("${baseURL}${endpoint}")
        } catch (_: Exception) {
            writeLog("$tag[invalidURL] Failed to build the URL: ${baseURL?.toString()}${endpoint.url}")
            return Result.Failure(KotlinRestError.InvalidURL)
        }

        writeLog("$tag[requestURL] Request URL: $requestUrl")
        val request = HttpRequest.newBuilder()
            .uri(requestUrl)
            .method(method.name, HttpRequest.BodyPublishers.ofString(body?.toString() ?: ""))
            .headers(*headers.flatMap { listOf(it.key, it.value) }.toTypedArray())
            .build()

        val response = try {
            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofByteArray())
        } catch (_: Exception) {
            writeLog("$tag[requestError] No response from the server")
            return Result.Failure(KotlinRestError.NoResponse)
        }

        writeLog("$tag[response] Server Response: ${String(response.body())}")

        return when (response.statusCode()) {
            in 200..299 -> {
                val decodedData = Json.decodeFromString<Serializable>(response.body().decodeToString())
                Result.Success(decodedData)
            }
            400 -> Result.Failure(KotlinRestError.BadRequest(String(response.body())))
            401 -> Result.Failure(KotlinRestError.Unauthorized)
            403 -> Result.Failure(KotlinRestError.Forbidden)
            404 -> Result.Failure(KotlinRestError.NotFound)
            500 -> Result.Failure(KotlinRestError.InternalServerError)
            502 -> Result.Failure(KotlinRestError.BadGateway)
            503 -> Result.Failure(KotlinRestError.ServiceUnavailable)
            504 -> Result.Failure(KotlinRestError.Timeout)
            else -> Result.Failure(KotlinRestError.Unknown(response.statusCode(), String(response.body())))
        }
    }

    /**
     * This just shows a log in the console if the logging is enabled, not fancy stuff is done here, it's just to avoid typing the if everytime
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param message The message to log
     */
    private fun writeLog(message: String) {
        if (loggingEnabled) {
            println(message)
        }
    }
}