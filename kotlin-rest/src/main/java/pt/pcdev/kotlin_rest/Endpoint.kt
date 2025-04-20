package pt.pcdev.kotlin_rest

import pt.pcdev.kotlin_rest.interfaces.EndpointInterface
import pt.pcdev.kotlin_rest.interfaces.StringRepresentable
import java.net.URI

enum class HTTPMethod(val method: String) {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE");
}

class Endpoint : EndpointInterface {

    override var url: String = ""

    constructor(url: String = "") {
        this.url = url
    }

    /**
     * Adds a version to the endpoint
     * This can be added using the `withPath(_ path: String)` method but it's better for readability
     * *NOTE: Do not append nor prepend the forward slash*
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param version The version of the endpoint as string
     */
    override fun withVersion(version: String): Endpoint {
        this.url += "/$version"
        return this
    }

    /**
     * Adds a version to the endpoint
     * This can be added using the `withPath(_ path: String)` method but it's better for readability
     * *NOTE: Do not append nor prepend the forward slash*
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param version The version of the endpoint as string
     * @param T The type of the enumerator containing the version
     */
    override fun <T : StringRepresentable> withVersion(version: T): Endpoint {
        this.withVersion(version.rawValue)
        return this
    }

    /**
     * Adds a controller to the endpoint
     * This can be added using the `withPath(_ path: String)` method but it's better for readability
     * *NOTE: Do not append nor prepend the forward slash*
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param controller The controller of the endpoint, usually a controller contains multiple endpoints
     */
    override fun withController(controller: String): Endpoint {
        this.url += "/$controller"
        return this
    }

    /**
     * Adds a controller to the endpoint
     * This can be added using the `withPath(_ path: String)` method but it's better for readability
     * *NOTE: Do not append nor prepend the forward slash*
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param controller The controller of the endpoint, usually a controller contains multiple endpoints
     * @param T The type of the enumerator containing the controller
     */
    override fun <T : StringRepresentable> withController(controller: T): Endpoint {
        this.url += "/${controller.rawValue}"
        return this
    }

    /**
     * Adds a path to the endpoint
     * *NOTE: Do not append nor prepend the forward slash*
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param path The path to the endpoint
     */
    override fun withPath(path: String): Endpoint {
        this.url += "/$path"
        return this
    }

    /**
     * Adds a path to the endpoint
     * *NOTE: Do not append nor prepend the forward slash*
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param path The path to the endpoint
     * @param parameters The dictionary with the parameters to replace
     */
    override fun withPath(path: String, parameters: Map<String, String>): Endpoint {
        this.withPath(path)
        parameters.forEach { (key, value) ->
            this.url = this.url.replace("{$key}", value)
        }
        return this
    }

    /**
     * Adds query parameters to the url
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param query The dictionary of query values to add
     */
    override fun withQuery(query: Map<String, String>): Endpoint {
        this.url += "?" + query.map { (key, value) -> "$key=$value" }.joinToString("&")
        return this
    }

    /**
     * Builds the URL
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @throws `KotlinRestError.InvalidURL` if the provided URL is not valid
     * @return The url as string
     */
    override fun build(): String {
        try {
            val url = URI(this.url)
            return this.url
        } catch (_: Exception) {
            throw KotlinRestError.InvalidURL
        }

    }
}