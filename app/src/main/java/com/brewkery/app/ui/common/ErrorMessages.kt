package com.brewkery.app.ui.common

import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

/** Maps technical failures to something a customer can act on. */
fun Throwable.toUserMessage(): String = when (this) {
    is HttpException -> "The server returned an error (${code()}). Please try again in a moment."
    is IOException -> "Can't reach Brewkery. Check your internet connection and try again."
    is SerializationException -> "We received unexpected data from the server. Please try again later."
    else -> "Something went wrong. Please try again."
}
