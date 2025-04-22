package interfaces

import HTTPMethod
import kotlinx.serialization.Serializable

interface ApiAuthenticationInterface {

    val headers: MutableMap<String, String>

    /**
     * Generates the headers to authenticate with the API.
     * This authenticates both the client and the user.
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param method The HTTP method of the request.
     * @param body The body of the request, null if none, must be serializable.
     * @param jwt The JWT token for user authentication, null for no authentication.
     * @return A map of headers for the request.
     */
    fun generateHeaders(
        method: HTTPMethod,
        body: Serializable?,
        jwt: String?
    ): Map<String, String>

    /**
     * Generates a SHA-256 hash of the input string.
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param input The input string to hash.
     * @return The SHA-256 hash of the input string.
     */
    fun sha256Hash(input: String): String

    /**
     * Generates an HMAC SHA-256 hash of the data using the provided key.
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param data The data to hash.
     * @param key The key to use for hashing.
     * @return The HMAC SHA-256 hash of the data.
     */
    fun hmacSha256(data: String, key: String): String

    /**
     * Serializes the given data to a JSON string.
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @param data The data to serialize.
     * @return The JSON string representation of the data.
     */
    fun serializeToJson(data: Serializable): String

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @author Pedro Cavaleiro
     * @since 1.0.0
     *
     * @return The hexadecimal string representation of the byte array.
     */
    fun ByteArray.toHexString(): String
}