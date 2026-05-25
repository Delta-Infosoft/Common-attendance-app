package com.i.common.attendance.network

import android.util.Log
import com.i.common.attendance.utils.EncryptedPrefHelper
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class BaseUrlInterceptor @Inject constructor(
    private val prefHelper: EncryptedPrefHelper
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        val baseUrl = prefHelper.getBaseUrl().toHttpUrl()

        val newUrl = baseUrl.newBuilder()
            .addEncodedPathSegments(originalUrl.encodedPath.removePrefix("/"))
            .encodedQuery(originalUrl.encodedQuery)
            .build()

        // 🔥 Logs
        Log.d("BaseUrlInterceptor", "Base URL: $baseUrl")
        Log.d("BaseUrlInterceptor", "Original URL: $originalUrl")
        Log.d("BaseUrlInterceptor", "Final URL: $newUrl")

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}