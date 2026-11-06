package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.AuthRequest
import com.example.data.model.Chat
import com.example.data.model.InAppNotification
import com.example.data.model.Message
import com.example.data.model.UiMode
import com.example.data.model.User
import com.example.data.remote.MessengerApi
import com.example.notification.MessengerNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessengerRepository private constructor(private val context: Context) {

  private val prefs = context.getSharedPreferences("messenger_prefs", Context.MODE_PRIVATE)
  private val api: MessengerApi = MessengerApi.create()
  private val scope = CoroutineScope(Dispatchers.IO)

  // Token and Current User
  private val _token = MutableStateFlow<String?>(prefs.getString("auth_token", "demo_jwt_token"))
  val token: StateFlow<String?> = _token.asStateFlow()

  private val _currentUser = MutableStateFlow<User>(
    User(
      id = 1,
      username = "ali_vali",
      email = "ali.vali@example.com",
      online = true,
      createdAt = "2024-11-06T08:00:00Z"
    )
  )
  val currentUser: StateFlow<User> = _currentUser.asStateFlow()

  // UI Theme & Mode toggles
  private val _isDarkTheme = MutableStateFlow(prefs.getBoolean("is_dark_theme", false))
  val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

  private val _uiMode = MutableStateFlow(UiMode.NORMAL)
  val uiMode: StateFlow<UiMode> = _uiMode.asStateFlow()

  private val _serverOnline = MutableStateFlow(false)
  val serverOnline: StateFlow<Boolean> = _serverOnline.asStateFlow()

  // Live Chats List
  private val _chats = MutableStateFlow<List<Chat>>(emptyList())
  val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

  // Messages map by UserId
  private val _messages = MutableStateFlow<Map<Int, List<Message>>>(emptyMap())
  val messages: StateFlow<Map<Int, List<Message>>> = _messages.asStateFlow()

  // Typing status map
  private val _typingStatus = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
  val typingStatus: StateFlow<Map<Int, Boolean>> = _typingStatus.asStateFlow()

  // Notifications StateFlows
  private val _notificationsEnabled = MutableStateFlow(prefs.getBoolean("notifications_enabled", true))
  val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

  private val _latestNotification = MutableStateFlow<InAppNotification?>(null)
  val latestNotification: StateFlow<InAppNotification?> = _latestNotification.asStateFlow()

  // WebSocket reference
  private var webSocket: WebSocket? = null
  private val okHttpClient = OkHttpClient()

  init {
    initDefaultData()
    checkServerHealth()
    startContinuousIncomingMessages()
  }

  fun setNotificationsEnabled(enabled: Boolean) {
    _notificationsEnabled.value = enabled
    prefs.edit().putBoolean("notifications_enabled", enabled).apply()
  }

  fun clearLatestNotification() {
    _latestNotification.value = null
  }

  fun setDarkTheme(dark: Boolean) {
    _isDarkTheme.value = dark
    prefs.edit().putBoolean("is_dark_theme", dark).apply()
  }

  fun setUiMode(mode: UiMode) {
    _uiMode.value = mode
  }

  fun checkServerHealth() {
    scope.launch {
      try {
        val res = api.checkHealth()
        _serverOnline.value = res.isSuccessful && res.body()?.ok == true
        if (_serverOnline.value) {
          connectWebSocket()
        }
      } catch (e: Exception) {
        _serverOnline.value = false
        Log.d("MessengerRepo", "Server health check offline: ${e.message}")
      }
    }
  }

  private fun connectWebSocket() {
    val jwt = _token.value ?: return
    val request = Request.Builder()
      .url("${MessengerApi.WS_URL}?token=$jwt")
      .build()

    webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
      override fun onOpen(ws: WebSocket, response: Response) {
        Log.d("MessengerWS", "WebSocket connected")
      }

      override fun onMessage(ws: WebSocket, text: String) {
        handleWsMessage(text)
      }

      override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
        Log.d("MessengerWS", "WebSocket failure: ${t.message}")
      }
    })
  }

  private fun handleWsMessage(text: String) {
    try {
      val json = JSONObject(text)
      when (json.optString("type")) {
        "message" -> {
          val data = json.getJSONObject("data")
          val msg = Message(
            id = data.getInt("id"),
            senderId = data.getInt("sender_id"),
            receiverId = data.getInt("receiver_id"),
            text = data.getString("text"),
            createdAt = data.getString("created_at"),
            readAt = if (data.has("read_at")) data.getString("read_at") else null,
            delivered = true
          )
          addIncomingMessage(msg)
        }
        "presence" -> {
          val data = json.getJSONObject("data")
          val userId = data.getInt("user_id")
          val online = data.getBoolean("online")
          updateUserOnline(userId, online)
        }
        "typing" -> {
          val data = json.getJSONObject("data")
          val from = data.getInt("from")
          setTyping(from, true)
          scope.launch {
            delay(2500)
            setTyping(from, false)
          }
        }
      }
    } catch (e: Exception) {
      Log.e("MessengerWS", "Parse error: ${e.message}")
    }
  }

  private fun updateUserOnline(userId: Int, online: Boolean) {
    _chats.value = _chats.value.map {
      if (it.userId == userId) it.copy(online = online) else it
    }
  }

  private fun setTyping(userId: Int, isTyping: Boolean) {
    val current = _typingStatus.value.toMutableMap()
    current[userId] = isTyping
    _typingStatus.value = current
  }

  suspend fun login(username: String, pass: String): Result<User> {
    if (_serverOnline.value) {
      return try {
        val res = api.login(AuthRequest(username, pass))
        if (res.isSuccessful && res.body() != null) {
          val body = res.body()!!
          _token.value = body.token
          _currentUser.value = body.user
          prefs.edit().putString("auth_token", body.token).apply()
          connectWebSocket()
          Result.success(body.user)
        } else {
          Result.failure(Exception("Kirish xatosi: login yoki parol noto'g'ri"))
        }
      } catch (e: Exception) {
        // Fallback to local success for interactive demo
        simulateLocalLogin(username)
      }
    } else {
      delay(600)
      return simulateLocalLogin(username)
    }
  }

  suspend fun register(username: String, pass: String): Result<User> {
    if (_serverOnline.value) {
      return try {
        val res = api.register(AuthRequest(username, pass))
        if (res.isSuccessful && res.body() != null) {
          val body = res.body()!!
          _token.value = body.token
          _currentUser.value = body.user
          prefs.edit().putString("auth_token", body.token).apply()
          connectWebSocket()
          Result.success(body.user)
        } else {
          Result.failure(Exception("Ro'yxatdan o'tishda xatolik yuz berdi"))
        }
      } catch (e: Exception) {
        simulateLocalLogin(username)
      }
    } else {
      delay(600)
      return simulateLocalLogin(username)
    }
  }

  suspend fun googleLogin(): Result<User> {
    if (_serverOnline.value) {
      try {
        val res = api.googleLogin(com.example.data.model.GoogleLoginRequest(idToken = "google_id_token_mock"))
        if (res.isSuccessful && res.body() != null) {
          val body = res.body()!!
          _token.value = body.token
          _currentUser.value = body.user
          prefs.edit().putString("auth_token", body.token).apply()
          connectWebSocket()
          return Result.success(body.user)
        }
      } catch (e: Exception) {
        Log.d("MessengerRepo", "Google login fallback: ${e.message}")
      }
    }
    delay(600)
    val user = User(
      id = 5,
      username = "sanobar_n",
      email = "sanobarnapesova@gmail.com",
      online = true,
      createdAt = "2024-11-06T08:00:00Z"
    )
    _currentUser.value = user
    _token.value = "google_mock_jwt_token"
    return Result.success(user)
  }

  private fun simulateLocalLogin(username: String): Result<User> {
    val user = User(
      id = 1,
      username = username.ifEmpty { "ali_vali" },
      email = "${username.ifEmpty { "ali_vali" }}@gmail.com",
      online = true
    )
    _currentUser.value = user
    _token.value = "demo_jwt_token"
    return Result.success(user)
  }

  fun logout() {
    _token.value = null
    prefs.edit().remove("auth_token").apply()
  }

  fun getMessagesForUser(userId: Int): List<Message> {
    return _messages.value[userId] ?: emptyList()
  }

  fun sendMessage(receiverId: Int, text: String) {
    val myId = _currentUser.value.id
    val now = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    val newId = ((_messages.value[receiverId]?.maxOfOrNull { it.id } ?: 100) + 1)
    val newMsg = Message(
      id = newId,
      senderId = myId,
      receiverId = receiverId,
      text = text,
      createdAt = now,
      readAt = null,
      delivered = true
    )

    // Update messages map
    val currentMap = _messages.value.toMutableMap()
    val list = (currentMap[receiverId] ?: emptyList()).toMutableList()
    list.add(newMsg)
    currentMap[receiverId] = list
    _messages.value = currentMap

    // Update chats list
    _chats.value = _chats.value.map { chat ->
      if (chat.userId == receiverId) {
        chat.copy(
          lastText = text,
          lastAt = now,
          lastSenderId = myId
        )
      } else chat
    }

    // Try server call if online
    val t = _token.value
    if (_serverOnline.value && t != null) {
      scope.launch {
        try {
          api.sendMessage("Bearer $t", com.example.data.model.SendMessageRequest(to = receiverId, text = text))
        } catch (e: Exception) {
          Log.e("MessengerRepo", "Live send failed: ${e.message}")
        }
      }
    }

    // Interactive realistic bot response simulation after 1.5s
    scope.launch {
      delay(1000)
      setTyping(receiverId, true)
      delay(1500)
      setTyping(receiverId, false)

      val replies = listOf(
        "Ajoyib! Barchasi tushunarli bo'ldi 👍",
        "Rahmat, tez orada ko'rib chiqaman!",
        "Xabaringizni oldim, ozgina kutib tursangiz javob beraman 😊",
        "Zo'r! Loyihaning dizayni juda yoqdi!",
        "Kelishdik, bugun kechqurun yana gaplashamiz."
      )
      val replyText = replies.random()
      val replyTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
      val replyId = newId + 1
      val replyMsg = Message(
        id = replyId,
        senderId = receiverId,
        receiverId = myId,
        text = replyText,
        createdAt = replyTime,
        readAt = null,
        delivered = true
      )
      addIncomingMessage(replyMsg)
    }
  }

  private fun addIncomingMessage(msg: Message) {
    val partnerId = if (msg.senderId == _currentUser.value.id) msg.receiverId else msg.senderId
    val currentMap = _messages.value.toMutableMap()
    val list = (currentMap[partnerId] ?: emptyList()).toMutableList()
    list.add(msg)
    currentMap[partnerId] = list
    _messages.value = currentMap

    var partnerName = "Suhbatdosh"
    _chats.value = _chats.value.map { chat ->
      if (chat.userId == partnerId) {
        partnerName = chat.username
        chat.copy(
          lastText = msg.text,
          lastAt = msg.createdAt,
          lastSenderId = msg.senderId,
          unread = chat.unread + 1
        )
      } else chat
    }

    // Trigger system notification and in-app banner if message is from a partner
    if (msg.senderId != _currentUser.value.id && _notificationsEnabled.value) {
      MessengerNotificationManager.showMessageNotification(
        context = context,
        senderId = partnerId,
        senderName = partnerName,
        messageText = msg.text
      )
      _latestNotification.value = InAppNotification(
        senderId = partnerId,
        senderName = partnerName,
        messageText = msg.text
      )
    }
  }

  private fun startContinuousIncomingMessages() {
    scope.launch {
      delay(8000) // start shortly after app launch
      val mockSenders = listOf(
        Pair(2, "Alisher Navoiy"),
        Pair(3, "Dilnoza UI/UX"),
        Pair(4, "Jasur Backend"),
        Pair(5, "Madina PM"),
        Pair(6, "Nodir Android")
      )
      val sampleTexts = listOf(
        "Assalomu alaykum! Loyiha qanday ketmoqda?",
        "Yangi bildirishnomalar tizimi muvaffaqiyatli ishlayapti 🔔",
        "Bugungi topshiriqlar bo'yicha yangi ma'lumot bormi?",
        "Figma maketidagi dizayn juda toza va qulay chiqibdi 👍",
        "Ertaga soat 10:00 da sprint rejalashtirish uchrashuvi bo'ladi.",
        "Xabarni oldim, tez orada to'liq javob qaytaraman 😊",
        "Suhbatlar ro'yxatida yangi xabar ko'rindi!"
      )

      while (true) {
        delay(22000) // every 22 seconds a message arrives continuously
        if (_token.value != null && _notificationsEnabled.value) {
          val (senderId, senderName) = mockSenders.random()
          val text = sampleTexts.random()
          val now = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
          val newId = ((_messages.value[senderId]?.maxOfOrNull { it.id } ?: 200) + 1)
          val msg = Message(
            id = newId,
            senderId = senderId,
            receiverId = _currentUser.value.id,
            text = text,
            createdAt = now,
            readAt = null,
            delivered = true
          )
          addIncomingMessage(msg)
        }
      }
    }
  }

  fun markMessagesRead(partnerId: Int) {
    val currentMap = _messages.value.toMutableMap()
    val list = currentMap[partnerId]?.map { msg ->
      if (msg.senderId == partnerId && msg.readAt == null) {
        msg.copy(readAt = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
      } else msg
    } ?: emptyList()
    currentMap[partnerId] = list
    _messages.value = currentMap

    _chats.value = _chats.value.map {
      if (it.userId == partnerId) it.copy(unread = 0) else it
    }
  }

  fun deleteMessage(partnerId: Int, messageId: Int) {
    val currentMap = _messages.value.toMutableMap()
    val list = currentMap[partnerId]?.filter { it.id != messageId } ?: emptyList()
    currentMap[partnerId] = list
    _messages.value = currentMap

    val last = list.lastOrNull()
    _chats.value = _chats.value.map { chat ->
      if (chat.userId == partnerId) {
        chat.copy(
          lastText = last?.text ?: "Xabar o'chirildi",
          lastAt = last?.createdAt ?: chat.lastAt
        )
      } else chat
    }
  }

  fun searchUsers(query: String): List<User> {
    val allUsers = listOf(
      User(id = 2, username = "alisher_dev", email = "alisher@gmail.com", online = true),
      User(id = 3, username = "dilnoza_ui", email = "dilnoza@design.uz", online = true),
      User(id = 4, username = "jasur_backend", email = "jasur@gmail.com", online = false),
      User(id = 5, username = "madina_pm", email = "madina@tech.uz", online = true),
      User(id = 6, username = "nodir_android", email = "nodir@android.uz", online = false),
      User(id = 7, username = "kamola_qa", email = "kamola@tester.uz", online = true),
      User(id = 8, username = "sardor_lead", email = "sardor@dev.uz", online = false)
    )
    if (query.isBlank()) return allUsers
    return allUsers.filter {
      it.username.contains(query, ignoreCase = true) || (it.email?.contains(query, ignoreCase = true) == true)
    }
  }

  private fun initDefaultData() {
    val initialChats = listOf(
      Chat(
        userId = 2,
        username = "Alisher Navoiy",
        lastId = 12,
        lastText = "Salom! Figma maketini ko'rib chiqdingizmi?",
        lastAt = "10:45",
        lastSenderId = 2,
        unread = 2,
        online = true
      ),
      Chat(
        userId = 3,
        username = "Dilnoza UI/UX",
        lastId = 15,
        lastText = "Yangi dizayn komponentlari tayyor bo'ldi ✨",
        lastAt = "09:30",
        lastSenderId = 3,
        unread = 1,
        online = true
      ),
      Chat(
        userId = 4,
        username = "Jasur Backend",
        lastId = 20,
        lastText = "WebSocket ulanishini tekshirib ko'ring (port 20237)",
        lastAt = "Kecha",
        lastSenderId = 1,
        unread = 0,
        online = false
      ),
      Chat(
        userId = 5,
        username = "Madina PM",
        lastId = 25,
        lastText = "Ertaga soat 10:00 da sprint rejalashtirish",
        lastAt = "Kecha",
        lastSenderId = 5,
        unread = 0,
        online = true
      ),
      Chat(
        userId = 6,
        username = "Nodir Android",
        lastId = 30,
        lastText = "Material 3 silliq animatsiyalari zo'r chiqibdi!",
        lastAt = "03 Noy",
        lastSenderId = 6,
        unread = 0,
        online = false
      )
    )
    _chats.value = initialChats

    // Initial messages for Alisher
    val alisherMessages = listOf(
      Message(
        id = 1,
        senderId = 1,
        receiverId = 2,
        text = "Assalomu alaykum! Yangi Messenger UI qanday ketmoqda?",
        createdAt = "10:30",
        readAt = "10:32",
        delivered = true
      ),
      Message(
        id = 2,
        senderId = 2,
        receiverId = 1,
        text = "Vaalaykum assalom! Juda yaxshi, Telegram va Signal uslubida toza minimalizm yaratdik.",
        createdAt = "10:35",
        readAt = "10:36",
        delivered = true
      ),
      Message(
        id = 3,
        senderId = 1,
        receiverId = 2,
        text = "Ajoyib, ranglar palitrasi to'q ko'k (#2563EB) va yashil (#22C55E) bo'ldimi?",
        createdAt = "10:40",
        readAt = "10:41",
        delivered = true
      ),
      Message(
        id = 4,
        senderId = 2,
        receiverId = 1,
        text = "Ha, aynan shunday! Dark va Light mavzulari ham to'liq sozlangan.",
        createdAt = "10:44",
        readAt = null,
        delivered = true
      ),
      Message(
        id = 5,
        senderId = 2,
        receiverId = 1,
        text = "Salom! Figma maketini ko'rib chiqdingizmi?",
        createdAt = "10:45",
        readAt = null,
        delivered = true
      )
    )

    // Initial messages for Dilnoza
    val dilnozaMessages = listOf(
      Message(
        id = 101,
        senderId = 3,
        receiverId = 1,
        text = "Assalomu alaykum! Komponentlar tizimini tayyorladim.",
        createdAt = "09:25",
        readAt = "09:28",
        delivered = true
      ),
      Message(
        id = 102,
        senderId = 3,
        receiverId = 1,
        text = "Yangi dizayn komponentlari tayyor bo'ldi ✨",
        createdAt = "09:30",
        readAt = null,
        delivered = true
      )
    )

    _messages.value = mapOf(
      2 to alisherMessages,
      3 to dilnozaMessages
    )
  }

  companion object {
    @Volatile
    private var instance: MessengerRepository? = null

    fun getInstance(context: Context): MessengerRepository {
      return instance ?: synchronized(this) {
        instance ?: MessengerRepository(context.applicationContext).also { instance = it }
      }
    }
  }
}
