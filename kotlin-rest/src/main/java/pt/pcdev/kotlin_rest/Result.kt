package pt.pcdev.kotlin_rest


sealed class Result<out T> {
    data class Success<out T>(val value: T) : Result<T>()
    data class Failure(val error: KotlinRestError) : Result<Nothing>()
}
