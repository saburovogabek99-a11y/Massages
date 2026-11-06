package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Message
import com.example.data.model.UiMode
import com.example.data.model.User
import com.example.data.repository.MessengerRepository
import com.example.ui.components.ChatBubble
import com.example.ui.components.DateDivider
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ErrorStateView
import com.example.ui.components.ShimmerBox
import com.example.ui.components.TypingIndicatorBubble
import com.example.ui.components.UserAvatar
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.BgDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
  user: User,
  repository: MessengerRepository,
  uiMode: UiMode,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val listState = rememberLazyListState()
  val scope = rememberCoroutineScope()

  val allMessagesMap by repository.messages.collectAsState()
  val typingMap by repository.typingStatus.collectAsState()
  val currentUser by repository.currentUser.collectAsState()

  val messages = allMessagesMap[user.id] ?: emptyList()
  val isTyping = typingMap[user.id] ?: false

  var textInput by remember { mutableStateOf("") }

  // Mark incoming messages as read when opening
  LaunchedEffect(user.id, messages.size) {
    repository.markMessagesRead(user.id)
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  // Handle Skeleton State
  if (uiMode == UiMode.SKELETON) {
    ChatSkeletonView(onBack = onBack, user = user, modifier = modifier)
    return
  }

  // Handle Error State
  if (uiMode == UiMode.ERROR) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
      ErrorStateView(
        title = "Xabarlar yuklanmadi",
        message = "Serverdan xabarlarni yuklab bo'lmadi (404/500).",
        onRetry = { repository.setUiMode(UiMode.NORMAL) },
        modifier = Modifier.align(Alignment.Center)
      )
    }
    return
  }

  val displayMessages = if (uiMode == UiMode.EMPTY) emptyList() else messages

  val isDark = MaterialTheme.colorScheme.background == BgDark
  val inputBg = if (isDark) MaterialTheme.colorScheme.surface else Color.White
  val inputBorder = if (isDark) BorderDark else BorderLight

  Scaffold(
    modifier = modifier.testTag("chat_screen"),
    topBar = {
      // Tepada: orqaga o'q, avatar, ism va holat ("online" yoki "yozmoqda...")
      Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onBack,
            modifier = Modifier
              .size(42.dp)
              .testTag("chat_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Orqaga",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }

          Spacer(modifier = Modifier.width(4.dp))

          UserAvatar(
            name = user.username,
            isOnline = user.online,
            size = 42.dp,
            showOnlineBadge = true
          )

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = user.username,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = when {
                isTyping -> "yozmoqda..."
                user.online -> "online"
                else -> "oflayn"
              },
              style = MaterialTheme.typography.labelSmall,
              color = when {
                isTyping -> PrimaryBlue
                user.online -> AccentGreen
                else -> MaterialTheme.colorScheme.onSurfaceVariant
              },
              fontWeight = if (isTyping || user.online) FontWeight.Medium else FontWeight.Normal
            )
          }
        }
      }
    },
    bottomBar = {
      // Pastda: yumaloq yozish maydoni va yumaloq ko'k yuborish tugmasi
      Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Yumaloq yozish maydoni
          Row(
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .clip(RoundedCornerShape(24.dp))
              .background(inputBg)
              .border(1.dp, inputBorder, RoundedCornerShape(24.dp))
              .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = { textInput += " 😊 " },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Outlined.Mood,
                contentDescription = "Emoji",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
              )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Box(modifier = Modifier.weight(1f)) {
              if (textInput.isEmpty()) {
                Text(
                  text = "Xabar yozing...",
                  style = MaterialTheme.typography.bodyLarge,
                  color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
              }
              BasicTextField(
                value = textInput,
                onValueChange = { textInput = it },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                  color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                  onSend = {
                    if (textInput.isNotBlank()) {
                      repository.sendMessage(user.id, textInput.trim())
                      textInput = ""
                    }
                  }
                ),
                cursorBrush = SolidColor(PrimaryBlue),
                modifier = Modifier.fillMaxWidth().testTag("chat_input_field")
              )
            }
          }

          Spacer(modifier = Modifier.width(10.dp))

          // Yumaloq ko'k yuborish tugmasi
          val canSend = textInput.isNotBlank()
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(if (canSend) PrimaryBlue else PrimaryBlue.copy(alpha = 0.5f))
              .testTag("chat_send_button"),
            contentAlignment = Alignment.Center
          ) {
            IconButton(
              onClick = {
                if (canSend) {
                  repository.sendMessage(user.id, textInput.trim())
                  textInput = ""
                  scope.launch {
                    val count = (repository.messages.value[user.id]?.size ?: 1)
                    listState.animateScrollToItem(count)
                  }
                }
              },
              enabled = canSend,
              modifier = Modifier.size(48.dp)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Yuborish",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }
    }
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(padding)
    ) {
      if (displayMessages.isEmpty()) {
        EmptyStateView(
          title = "Hali xabarlar yo'q",
          subtitle = "${user.username} bilan birinchi xabaringizni yozib, muloqotni boshlang!",
          actionText = "Salom deb yozish 👋",
          onActionClick = {
            repository.sendMessage(user.id, "Salom! Ishlar yaxshimi?")
          },
          modifier = Modifier.align(Alignment.Center)
        )
      } else {
        LazyColumn(
          state = listState,
          modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
        ) {
          // Kunlar orasida "Bugun", "Kecha" ajratgichi
          item {
            DateDivider(text = "Kecha")
          }

          // Render sample previous messages
          val sampleFirstTwo = displayMessages.take(2)
          items(sampleFirstTwo, key = { "m_${it.id}" }) { msg ->
            val isMine = msg.senderId == currentUser.id
            ChatBubble(
              message = msg,
              isMine = isMine,
              onDeleteClick = {
                repository.deleteMessage(user.id, msg.id)
                Toast.makeText(context, "Xabar o'chirildi", Toast.LENGTH_SHORT).show()
              },
              onCopyClick = {
                clipboardManager.setText(AnnotatedString(msg.text))
                Toast.makeText(context, "Nusxa olindi", Toast.LENGTH_SHORT).show()
              }
            )
          }

          item {
            DateDivider(text = "Bugun")
          }

          val remaining = displayMessages.drop(2)
          items(remaining, key = { "m_${it.id}" }) { msg ->
            val isMine = msg.senderId == currentUser.id
            ChatBubble(
              message = msg,
              isMine = isMine,
              onDeleteClick = {
                repository.deleteMessage(user.id, msg.id)
                Toast.makeText(context, "Xabar o'chirildi", Toast.LENGTH_SHORT).show()
              },
              onCopyClick = {
                clipboardManager.setText(AnnotatedString(msg.text))
                Toast.makeText(context, "Nusxa olindi", Toast.LENGTH_SHORT).show()
              }
            )
          }

          if (isTyping) {
            item {
              TypingIndicatorBubble(username = user.username)
            }
          }

          item {
            Spacer(modifier = Modifier.height(10.dp))
          }
        }
      }
    }
  }
}

// Skeleton view for Chat Screen
@Composable
fun ChatSkeletonView(
  onBack: () -> Unit,
  user: User,
  modifier: Modifier = Modifier
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
          }
          ShimmerBox(modifier = Modifier.size(42.dp), cornerRadius = 21.dp)
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            ShimmerBox(modifier = Modifier.width(110.dp).height(16.dp), cornerRadius = 4.dp)
            Spacer(modifier = Modifier.height(4.dp))
            ShimmerBox(modifier = Modifier.width(60.dp).height(12.dp), cornerRadius = 4.dp)
          }
        }
      }
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(padding)
        .padding(16.dp)
    ) {
      ShimmerBox(
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .width(80.dp)
          .height(24.dp),
        cornerRadius = 12.dp
      )
      Spacer(modifier = Modifier.height(20.dp))

      // Incoming bubble skeleton
      ShimmerBox(
        modifier = Modifier
          .align(Alignment.Start)
          .width(220.dp)
          .height(56.dp),
        cornerRadius = 18.dp
      )
      Spacer(modifier = Modifier.height(14.dp))

      // Outgoing bubble skeleton
      ShimmerBox(
        modifier = Modifier
          .align(Alignment.End)
          .width(180.dp)
          .height(48.dp),
        cornerRadius = 18.dp
      )
      Spacer(modifier = Modifier.height(14.dp))

      // Incoming bubble skeleton
      ShimmerBox(
        modifier = Modifier
          .align(Alignment.Start)
          .width(250.dp)
          .height(72.dp),
        cornerRadius = 18.dp
      )
      Spacer(modifier = Modifier.height(14.dp))

      // Outgoing bubble skeleton
      ShimmerBox(
        modifier = Modifier
          .align(Alignment.End)
          .width(210.dp)
          .height(52.dp),
        cornerRadius = 18.dp
      )
    }
  }
}
