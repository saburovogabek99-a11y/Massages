package com.example

import com.example.data.model.Chat
import com.example.data.model.Message
import com.example.data.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MessengerUnitTest {

  @Test
  fun testUserModel() {
    val user = User(
      id = 1,
      username = "ali_vali",
      email = "ali@gmail.com",
      online = true
    )
    assertEquals(1, user.id)
    assertEquals("ali_vali", user.username)
    assertTrue(user.online)
  }

  @Test
  fun testMessageDelivery() {
    val message = Message(
      id = 10,
      senderId = 1,
      receiverId = 2,
      text = "Salom, loyiha tayyormi?",
      createdAt = "10:30",
      readAt = "10:32",
      delivered = true
    )
    assertEquals(10, message.id)
    assertEquals("Salom, loyiha tayyormi?", message.text)
    assertEquals("10:32", message.readAt)
    assertTrue(message.delivered)
  }

  @Test
  fun testChatUnreadCount() {
    val chat = Chat(
      userId = 2,
      username = "Alisher",
      lastText = "Salom!",
      unread = 3,
      online = true
    )
    assertEquals(3, chat.unread)
    assertTrue(chat.online)
  }
}
