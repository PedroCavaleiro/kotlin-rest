package pt.pcdev.kotlin_rest.interfaces

import kotlinx.serialization.Serializable
import pt.pcdev.kotlin_rest.HTTPMethod

interface ApiAuthenticationInterface {

    val headers: Map<String, String>

    /**
     * Generates the headers to authenticate with the API
     * This authenticates both the client and the user
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param method The `HTTPMethod` of the request
     * @param body The body of the request, null if none, must be `codable`
     * @param jwt The JWT token for the user authentication, null for no authentication
     * @return A map of headers for the authentication
     */
    fun generateHeaders(
        method: HTTPMethod,
        body: Serializable?,
        jwt: String?
    ): Map<String, String>

}