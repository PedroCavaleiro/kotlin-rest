sealed class KotlinRestError(message: String? = null) : Throwable(message), java.io.Serializable {
    object InvalidURL : KotlinRestError("Invalid URL") {
        @JvmStatic
        private fun readResolve(): Any = InvalidURL
    }
    object NoResponse : KotlinRestError("No response from server") {
        @JvmStatic
        private fun readResolve(): Any = NoResponse
    }
    data class BadRequest(val errorMessage: String) : KotlinRestError(errorMessage)
    object Unauthorized : KotlinRestError("Unauthorized access") {
        @JvmStatic
        private fun readResolve(): Any = Unauthorized
    }
    object Forbidden : KotlinRestError("Forbidden access") {
        @JvmStatic
        private fun readResolve(): Any = Forbidden
    }
    object NotFound : KotlinRestError("Resource not found") {
        @JvmStatic
        private fun readResolve(): Any = NotFound
    }
    object InternalServerError : KotlinRestError("Internal server error") {
        @JvmStatic
        private fun readResolve(): Any = InternalServerError
    }
    object BadGateway : KotlinRestError("Bad gateway") {
        @JvmStatic
        private fun readResolve(): Any = BadGateway
    }
    object ServiceUnavailable : KotlinRestError("Service unavailable") {
        @JvmStatic
        private fun readResolve(): Any = ServiceUnavailable
    }
    object Timeout : KotlinRestError("Request timed out") {
        @JvmStatic
        private fun readResolve(): Any = Timeout
    }
    data class Unknown(val code: Int, val errorMessage: String) : KotlinRestError("Unknown error: Code $code, Message: $errorMessage")
}

// Example usage
fun handleError(error: KotlinRestError) {
    when (error) {
        is KotlinRestError.InvalidURL -> println("Invalid URL")
        is KotlinRestError.NoResponse -> println("No response from server")
        is KotlinRestError.BadRequest -> println("Bad request: ${error.errorMessage}")
        is KotlinRestError.Unauthorized -> println("Unauthorized access")
        is KotlinRestError.Forbidden -> println("Forbidden access")
        is KotlinRestError.NotFound -> println("Resource not found")
        is KotlinRestError.InternalServerError -> println("Internal server error")
        is KotlinRestError.BadGateway -> println("Bad gateway")
        is KotlinRestError.ServiceUnavailable -> println("Service unavailable")
        is KotlinRestError.Timeout -> println("Request timed out")
        is KotlinRestError.Unknown -> println("Unknown error: Code ${error.code}, Message: ${error.errorMessage}")
    }
}


