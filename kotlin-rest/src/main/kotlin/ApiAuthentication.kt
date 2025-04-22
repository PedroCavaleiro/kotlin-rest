import interfaces.ApiAuthenticationInterface
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

public class ApiAuthentication(
    deviceUuid: String,
    appId: String,
    private val appKey: String,
    userAgent: String = "KotlinRest"
): ApiAuthenticationInterface {

    private enum class AuthenticationHeader(val header: String) {
        AppId("x-app-id"),
        Browser("x-browser"),
        BrowserSignature("x-browser-sig"),
        Client("x-client"),
        RequestSignature("x-req-sig"),
        RequestNonce("x-req-nonce"),
        RequestTimestamp("x-req-timestamp")
    }

    override var headers: MutableMap<String, String> = mutableMapOf()

    init {
        val fingerprint = sha256Hash("$deviceUuid | $userAgent")

        headers[AuthenticationHeader.AppId.header] = appId
        headers[AuthenticationHeader.Browser.header] = fingerprint
        headers[AuthenticationHeader.BrowserSignature.header] = sha256Hash(fingerprint)
        headers[AuthenticationHeader.Client.header] = userAgent
    }

    /**
     * Generates the headers to authenticate with the API
     * This authenticates both the client and the user
     *
     * @param method The `HTTPMethod` of the request
     * @param body The body of the request, null `if` none, must be `serializable`
     * @param jwt The JWT token for the user authentication, `null` for no authentication
     */
    override fun generateHeaders(
        method: HTTPMethod,
        body: Serializable?,
        jwt: String?
    ): Map<String, String> {
        val headersCopy = headers.toMutableMap()

        val requestNonce = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis().toString()
        val appIdValue = headers[AuthenticationHeader.AppId.header] ?: ""

        val bodyHash = body?.let { sha256Hash(serializeToJson(it)) } ?: ""

        val plainSignature = if (body == null) {
            "$appIdValue$method$timestamp$requestNonce"
        } else {
            "$appIdValue$method$timestamp$requestNonce$bodyHash"
        }

        headersCopy[AuthenticationHeader.RequestSignature.header] = hmacSha256(plainSignature, appKey)
        headersCopy[AuthenticationHeader.RequestNonce.header] = requestNonce
        headersCopy[AuthenticationHeader.RequestTimestamp.header] = timestamp

        if (jwt != null) {
            headersCopy["Authorization"] = "Bearer $jwt"
        }

        return headersCopy
    }

    override fun sha256Hash(input: String): String {
        val bytes = input.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.toHexString()
    }

    override fun hmacSha256(data: String, key: String): String {
        val hmacSha256 = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(key.toByteArray(), "HmacSHA256")
        hmacSha256.init(secretKey)
        val hash = hmacSha256.doFinal(data.toByteArray())
        return hash.toHexString()
    }

    override fun serializeToJson(data: Serializable): String {
        return Json.Default.encodeToString(data)
    }

    override fun ByteArray.toHexString(): String {
        return joinToString("") { "%02x".format(it) }
    }
}