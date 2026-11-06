package com.example.data.remote

import com.example.data.model.AuthRequest
import com.example.data.model.AuthResponse
import com.example.data.model.Chat
import com.example.data.model.DeleteResponse
import com.example.data.model.GoogleLoginRequest
import com.example.data.model.HealthResponse
import com.example.data.model.Message
import com.example.data.model.ReadResponse
import com.example.data.model.SendMessageRequest
import com.example.data.model.User
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface MessengerApi {

  @POST("auth/register")
  suspend fun register(@Body body: AuthRequest): Response<AuthResponse>

  @POST("auth/login")
  suspend fun login(@Body body: AuthRequest): Response<AuthResponse>

  @POST("auth/google")
  suspend fun googleLogin(@Body body: GoogleLoginRequest): Response<AuthResponse>

  @GET("me")
  suspend fun getMe(@Header("Authorization") token: String): Response<User>

  @GET("users")
  suspend fun searchUsers(
    @Header("Authorization") token: String,
    @Query("q") query: String
  ): Response<List<User>>

  @GET("chats")
  suspend fun getChats(@Header("Authorization") token: String): Response<List<Chat>>

  @GET("messages/{userId}")
  suspend fun getMessages(
    @Path("userId") userId: Int,
    @Header("Authorization") token: String,
    @Query("limit") limit: Int = 50,
    @Query("before") before: Int? = null,
    @Query("after") after: Int? = null
  ): Response<List<Message>>

  @POST("messages")
  suspend fun sendMessage(
    @Header("Authorization") token: String,
    @Body body: SendMessageRequest
  ): Response<Message>

  @POST("messages/{userId}/read")
  suspend fun markAsRead(
    @Path("userId") userId: Int,
    @Header("Authorization") token: String
  ): Response<ReadResponse>

  @DELETE("messages/{id}")
  suspend fun deleteMessage(
    @Path("id") id: Int,
    @Header("Authorization") token: String
  ): Response<DeleteResponse>

  @GET("health")
  suspend fun checkHealth(): Response<HealthResponse>

  companion object {
    const val BASE_URL = "http://saburov.uz:20237/api/"
    const val WS_URL = "ws://saburov.uz:20237/ws"

    fun create(): MessengerApi {
      val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
      }

      val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .writeTimeout(8, TimeUnit.SECONDS)
        .build()

      return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(MessengerApi::class.java)
    }
  }
}
