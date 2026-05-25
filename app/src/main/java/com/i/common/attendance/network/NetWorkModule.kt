package com.i.common.attendance.network

import android.content.Context
import com.i.common.attendance.network.service.ApiService
import com.i.common.attendance.utils.EncryptedPrefHelper
import com.i.common.attendance.utils.URLFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetWorkModule {

    /* -------------------------------- */
    /* INTERCEPTORS                     */
    /* -------------------------------- */

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun provideNetworkConnectionInterceptor(
        @ApplicationContext context: Context
    ): NetworkConnectionInterceptor =
        NetworkConnectionInterceptor(context)

    /* -------------------------------- */
    /* OKHTTP                           */
    /* -------------------------------- */

    @Provides
    @Singleton
    fun provideBaseUrlInterceptor(
        prefHelper: EncryptedPrefHelper
    ): BaseUrlInterceptor {
        return BaseUrlInterceptor(prefHelper)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        networkConnectionInterceptor: NetworkConnectionInterceptor,
        baseUrlInterceptor: BaseUrlInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(networkConnectionInterceptor)
            .addInterceptor(baseUrlInterceptor) // ⭐ ADD HERE
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(HtmlStripInterceptor()) // ⭐ ADD HERE
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

    @Singleton
    @Provides
    @Named("DUKE_CLIENT")
    fun provideDukeOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        networkConnectionInterceptor: NetworkConnectionInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(networkConnectionInterceptor)
            // ❌ NO BaseUrlInterceptor here
            .addInterceptor {
                val request = it.request().newBuilder()
                    .header("Accept", "application/json")
                    .build()
                it.proceed(request)
            }
            .addInterceptor(HtmlStripInterceptor())
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

    @Singleton
    @Provides
    @Named("FLOTECH_CLIENT")
    fun provideFlotechOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        networkConnectionInterceptor: NetworkConnectionInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(networkConnectionInterceptor)
            // ❌ NO BaseUrlInterceptor here
            .addInterceptor {
                val request = it.request().newBuilder()
                    .header("Accept", "application/json")
                    .build()
                it.proceed(request)
            }
            .addInterceptor(HtmlStripInterceptor())
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

    @Singleton
    @Provides
    @Named("UNNATI_LOCALHOST_CLIENT")
    fun provideUnnatiLocalhostOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        networkConnectionInterceptor: NetworkConnectionInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(networkConnectionInterceptor)
            // ❌ NO BaseUrlInterceptor here
            .addInterceptor {
                val request = it.request().newBuilder()
                    .header("Accept", "application/json")
                    .build()
                it.proceed(request)
            }
            .addInterceptor(HtmlStripInterceptor())
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

    @Singleton
    @Provides
    @Named("MASCOT_CLIENT")
    fun provideMascotOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        networkConnectionInterceptor: NetworkConnectionInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(networkConnectionInterceptor)
            // ❌ NO BaseUrlInterceptor here
            .addInterceptor {
                val request = it.request().newBuilder()
                    .header("Accept", "application/json")
                    .build()
                it.proceed(request)
            }
            .addInterceptor(HtmlStripInterceptor())
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    /* -------------------------------- */
    /* RETROFIT (DEFAULT BASE URL)      */
    /* -------------------------------- */

    @Singleton
    @Provides
    @Named("DEFAULT")
    fun provideDefaultRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("http://localhost/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    /* -------------------------------- */
    /* RETROFIT (DUKE BASE URL)     */
    /* -------------------------------- */

    @Singleton
    @Provides
    @Named("DUKE")
    fun provideDukeRetrofit(
        @Named("DUKE_CLIENT") okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(URLFactory.Url.baseUrlDuke)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    @Named("FLOTECH")
    fun provideFlotechRetrofit(
        @Named("FLOTECH_CLIENT") okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(URLFactory.Url.baseUrlFlotech)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    @Named("MASCOT")
    fun provideMascotRetrofit(
        @Named("MASCOT_CLIENT") okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(URLFactory.Url.BASE_URL_MASCOT_DELTA_ACCOUNT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    @Named("UNNATI_LOCALHOST")
    fun provideUnnatiLocalHostRetrofit(
        @Named("UNNATI_LOCALHOST_CLIENT") okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(URLFactory.Url.BASE_URL_UNNATI_LOCAL_HOST)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    /* -------------------------------- */
    /* API SERVICES                     */
    /* -------------------------------- */

    @Singleton
    @Provides
    @Named("DEFAULT")
    fun provideDefaultApiService(
        @Named("DEFAULT") retrofit: Retrofit
    ): ApiService = retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    @Named("DUKE")
    fun provideDukeApiService(
        @Named("DUKE") retrofit: Retrofit
    ): ApiService = retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    @Named("FLOTECH")
    fun provideFlotechApiService(
        @Named("FLOTECH") retrofit: Retrofit
    ): ApiService = retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    @Named("MASCOT")
    fun provideMascotApiService(
        @Named("MASCOT") retrofit: Retrofit
    ): ApiService = retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    @Named("UNNATI_LOCALHOST")
    fun provideUnnatiLocalHostApiService(
        @Named("UNNATI_LOCALHOST") retrofit: Retrofit
    ): ApiService = retrofit.create(ApiService::class.java)

}