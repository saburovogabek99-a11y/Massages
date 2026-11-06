package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Chat
import com.example.data.model.UiMode
import com.example.data.model.User
import com.example.data.repository.MessengerRepository
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ErrorStateView
import com.example.ui.components.ShimmerBox
import com.example.ui.components.UnreadBadge
import com.example.ui.components.UserAvatar
import com.example.ui.theme.PrimaryBlue

@Composable
fun ChatsListScreen(
  repository: MessengerRepository,
  uiMode: UiMode,
  onOpenChat: (User) -> Unit,
  onOpenSearch: () -> Unit,
  onOpenProfile: () -> Unit,
  modifier: Modifier = Modifier
) {
  val chats by repository.chats.collectAsState()
  val currentUser by repository.currentUser.collectAsState()

  // Handle Skeleton State
  if (uiMode == UiMode.SKELETON) {
    ChatsListSkeletonView(modifier = modifier)
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
        title = "Suhbatlar yuklanmadi",
        message = "Internet aloqasi yoki server bilan bog'lanishda xatolik yuz berdi.",
        onRetry = { repository.setUiMode(UiMode.NORMAL) },
        modifier = Modifier.align(Alignment.Center)
      )
    }
    return
  }

  // Handle Empty State
  val displayChats = if (uiMode == UiMode.EMPTY) emptyList() else chats

  Scaffold(
    modifier = modifier.testTag("chats_list_screen"),
    topBar = {
      // Tepada ilova nomi va qidiruv ikonkasi
      Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Messenger",
              style = MaterialTheme.typography.titleLarge,
              fontSize = 22.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = onOpenSearch,
              modifier = Modifier
                .size(42.dp)
                .testTag("search_icon_button")
            ) {
              Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Qidiruv",
                tint = MaterialTheme.colorScheme.onSurface
              )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // User profile shortcut avatar
            Box(
              modifier = Modifier
                .size(36.dp)
                .clickable { onOpenProfile() }
                .testTag("profile_avatar_button"),
              contentAlignment = Alignment.Center
            ) {
              UserAvatar(
                name = currentUser.username,
                isOnline = true,
                size = 36.dp,
                showOnlineBadge = false
              )
            }
          }
        }
      }
    },
    floatingActionButton = {
      // Pastki o'ng burchakda suzuvchi "+" tugmasi
      FloatingActionButton(
        onClick = onOpenSearch,
        containerColor = PrimaryBlue,
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
        shape = CircleShape,
        modifier = Modifier
          .padding(bottom = 12.dp, end = 4.dp)
          .testTag("new_chat_fab")
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "Yangi suhbat",
          modifier = Modifier.size(28.dp)
        )
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(paddingValues)
    ) {
      if (displayChats.isEmpty()) {
        // Bo'sh holat: rasm va "Hali suhbatlar yo'q" yozuvi
        EmptyStateView(
          title = "Hali suhbatlar yo'q",
          subtitle = "Do'stlaringizni qidirib toping va muloqotni boshlang",
          actionText = "Yangi suhbat boshlash",
          onActionClick = onOpenSearch,
          modifier = Modifier.align(Alignment.Center)
        )
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize()
        ) {
          items(displayChats, key = { it.userId }) { chat ->
            ChatItemRow(
              chat = chat,
              onClick = {
                onOpenChat(
                  User(
                    id = chat.userId,
                    username = chat.username,
                    online = chat.online
                  )
                )
              }
            )
            HorizontalDivider(
              modifier = Modifier.padding(start = 76.dp, end = 16.dp),
              color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
          }
          item {
            Spacer(modifier = Modifier.height(80.dp))
          }
        }
      }
    }
  }
}

// Chat Row: Har bir qator: yumaloq avatar (online bo'lsa yashil nuqta), ism qalin, oxirgi xabar kulrang bir qatorda, o'ngda vaqt va o'qilmagan xabarlar soni (ko'k doira badge)
@Composable
fun ChatItemRow(
  chat: Chat,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("chat_item_${chat.userId}"),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Yumaloq avatar (online bo'lsa yashil nuqta)
    UserAvatar(
      name = chat.username,
      isOnline = chat.online,
      size = 52.dp,
      showOnlineBadge = true
    )

    Spacer(modifier = Modifier.width(14.dp))

    // Ism va oxirgi xabar
    Column(
      modifier = Modifier.weight(1f)
    ) {
      Text(
        text = chat.username,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = chat.lastText ?: "Suhbat boshlanmagan",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }

    Spacer(modifier = Modifier.width(8.dp))

    // O'ngda vaqt va o'qilmagan xabarlar soni (ko'k doira badge)
    Column(
      horizontalAlignment = Alignment.End,
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = chat.lastAt ?: "",
        style = MaterialTheme.typography.labelSmall,
        color = if (chat.unread > 0) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = if (chat.unread > 0) FontWeight.SemiBold else FontWeight.Normal
      )

      Spacer(modifier = Modifier.height(4.dp))

      if (chat.unread > 0) {
        UnreadBadge(count = chat.unread)
      } else {
        Spacer(modifier = Modifier.size(24.dp))
      }
    }
  }
}

// Skeleton loading layout for Chats List
@Composable
fun ChatsListSkeletonView(modifier: Modifier = Modifier) {
  Scaffold(
    modifier = modifier,
    topBar = {
      Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          ShimmerBox(modifier = Modifier.width(120.dp).height(24.dp), cornerRadius = 6.dp)
          ShimmerBox(modifier = Modifier.size(36.dp), cornerRadius = 18.dp)
        }
      }
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(padding)
        .padding(vertical = 8.dp)
    ) {
      repeat(7) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          ShimmerBox(modifier = Modifier.size(52.dp), cornerRadius = 26.dp)
          Spacer(modifier = Modifier.width(14.dp))
          Column(modifier = Modifier.weight(1f)) {
            ShimmerBox(modifier = Modifier.width(140.dp).height(16.dp), cornerRadius = 4.dp)
            Spacer(modifier = Modifier.height(8.dp))
            ShimmerBox(modifier = Modifier.fillMaxWidth(0.8f).height(14.dp), cornerRadius = 4.dp)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column(horizontalAlignment = Alignment.End) {
            ShimmerBox(modifier = Modifier.width(36.dp).height(12.dp), cornerRadius = 4.dp)
            Spacer(modifier = Modifier.height(6.dp))
            ShimmerBox(modifier = Modifier.size(20.dp), cornerRadius = 10.dp)
          }
        }
        HorizontalDivider(
          modifier = Modifier.padding(start = 76.dp, end = 16.dp),
          color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
      }
    }
  }
}
