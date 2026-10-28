package com.udv.notasapp

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface ApiService {

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/notas")
    suspend fun crearNota(@Body request: CrearNotaRequest): Response<Nota>

    @GET("api/notas")
    suspend fun obtenerNotas(): Response<List<Nota>>

    @DELETE("api/notas/{id}")
    suspend fun eliminarNota(@Path("id") id: Int): Response<Void>
}

object RetrofitClient {
    private const val BASE_URL = "http://192.168.0.22:3000/"
    private val authInterceptor = Interceptor { chain ->
        val builder = chain.request().newBuilder()
        val token = SessionManager.obtenerToken()
        if (!token.isNullOrEmpty()) {
            builder.addHeader("Authorization", "Bearer $token")
        }
        chain.proceed(builder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}