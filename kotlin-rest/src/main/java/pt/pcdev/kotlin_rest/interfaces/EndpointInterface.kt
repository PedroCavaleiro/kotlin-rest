package pt.pcdev.kotlin_rest.interfaces

interface EndpointInterface {

    val url: String

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
    fun withVersion(version: String): EndpointInterface

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
    fun <T : StringRepresentable> withVersion(version: T): EndpointInterface

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
    fun withController(controller: String): EndpointInterface

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
    fun <T : StringRepresentable> withController(controller: T): EndpointInterface

    /**
     * Adds a path to the endpoint
     * *NOTE: Do not append nor prepend the forward slash*
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param path The path to the endpoint
     */
    fun withPath(path: String): EndpointInterface

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
    fun withPath(path: String, parameters: Map<String, String>): EndpointInterface

    /**
     * Adds query parameters to the url
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param query The dictionary of query values to add
     */
    fun withQuery(query: Map<String, String>): EndpointInterface

    /**
     * Builds the URL
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @throws `KotlinRestError.InvalidURL` if the provided URL is not valid
     * @return The url as string
     */
    fun build(): String

}