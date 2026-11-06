package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
  val id: Int,
  val username: String,
  val email: String? = null,
  val online: Boolean = false,
  @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class Chat(
  @Json(name = "user_id") val userId: Int,
  val username: String,
  @Json(name = "last_id") val lastId: Int? = null,
  @Json(name = "last_text") val lastText: String? = null,
  @Json(name = "last_at") val lastAt: String? = null,
  @Json(name = "last_sender_id") val lastSenderId: Int? = null,
  val unread: Int = 0,
  val online: Boolean = false
)

@JsonClass(generateAdapter = true)
data class Message(
  val id: Int,
  @Json(name = "sender_id") val senderId: Int,
  @Json(name = "receiver_id") val receiverId: Int,
  val text: String,
  @Json(name = "created_at") val createdAt: String,
  @Json(name = "read_at") val readAt: String? = null,
  val delivered: Boolean = true
)

@JsonClass(generateAdapter = true)
data class AuthRequest(
  val username: String,
  val password: String
)

@JsonClass(generateAdapter = true)
data class GoogleLoginRequest(
  val idToken: String
)

@JsonClass(generateAdapter = true)
data class AuthResponse(
  val token: String,
  val user: User
)

@JsonClass(generateAdapter = true)
data class SendMessageRequest(
  val to: Int,
  val text: String
)

@JsonClass(generateAdapter = true)
data class ReadResponse(
  val updated: Int
)

@JsonClass(generateAdapter = true)
data class DeleteResponse(
  val ok: Boolean
)

@JsonClass(generateAdapter = true)
data class HealthResponse(
  val ok: Boolean,
  val time: String? = null
)

data class InAppNotification(
  val senderId: Int,
  val senderName: String,
  val messageText: String,
  val timestamp: Long = System.currentTimeMillis()
)

enum class UiMode {
  NORMAL,
  SKELETON,
  ERROR,
  EMPTY
}

sealed class Screen {
  object Splash : Screen()
  object Login : Screen()
  object ChatsList : Screen()
  object Search : Screen()
  data class ChatDetail(val user: User) : Screen()
  object Profile : Screen()
  object DesignSystem : Screen()
}
