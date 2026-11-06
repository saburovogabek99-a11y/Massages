package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UiMode
import com.example.data.model.User
import com.example.data.repository.MessengerRepository
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ErrorStateView
import com.example.ui.components.MessengerTextField
import com.example.ui.components.ShimmerBox
import com.example.ui.components.UserAvatar
import com.example.ui.theme.PrimaryBlue

@Composable
fun SearchScreen(
  repository: MessengerRepository,
  uiMode: UiMode,
  onBack: () -> Unit,
  onOpenChat: (User) -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  val searchResults = remember(searchQuery) {
    repository.searchUsers(searchQuery)
  }

  // Handle Skeleton State
  if (uiMode == UiMode.SKELETON) {
    SearchSkeletonView(onBack = onBack, modifier = modifier)
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
        title = "Qidiruvda xatolik",
        message = "Foydalanuvchilarni qidirishda xatolik yuz berdi. Qayta urinib ko'ring.",
        onRetry = { repository.setUiMode(UiMode.NORMAL) },
        modifier = Modifier.align(Alignment.Center)
      )
    }
    return
  }

  Scaffold(
    modifier = modifier.testTag("search_screen"),
    topBar = {
      Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = onBack,
              modifier = Modifier
                .size(40.dp)
                .testTag("search_back_button")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Orqaga",
                tint = MaterialTheme.colorScheme.onSurface
              )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Tepada katta qidiruv maydoni
            MessengerTextField(
              value = searchQuery,
              onValueChange = { searchQuery = it },
              placeholder = "Foydalanuvchini qidiring...",
              leadingIcon = Icons.Outlined.Search,
              modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp),
              testTag = "search_input_field"
            )

            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(
                  imageVector = Icons.Default.Clear,
                  contentDescription = "Tozalash",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
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
      if (uiMode == UiMode.EMPTY || searchResults.isEmpty()) {
        EmptyStateView(
          title = "Foydalanuvchi topilmadi",
          subtitle = "Kiritilgan nom bo'yicha hech qanday natija mavjud emas",
          modifier = Modifier.align(Alignment.Center)
        )
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize()
        ) {
          item {
            Text(
              text = if (searchQuery.isBlank()) "Tavsiya etilgan foydalanuvchilar" else "Qidiruv natijalari",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
          }

          items(searchResults, key = { it.id }) { user ->
            SearchResultItem(
              user = user,
              onWriteClick = { onOpenChat(user) }
            )
            HorizontalDivider(
              modifier = Modifier.padding(start = 76.dp, end = 16.dp),
              color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
          }
        }
      }
    }
  }
}

// Natijalar avatar va username bilan, har birining yonida "Yozish" tugmasi
@Composable
fun SearchResultItem(
  user: User,
  onWriteClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("search_result_item_${user.id}"),
    verticalAlignment = Alignment.CenterVertically
  ) {
    UserAvatar(
      name = user.username,
      isOnline = user.online,
      size = 48.dp,
      showOnlineBadge = true
    )

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = user.username,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = if (user.online) "Online" else "Oflayn",
        style = MaterialTheme.typography.labelSmall,
        color = if (user.online) Color(0xFF22C55E) else MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    // Har birining yonida "Yozish" tugmasi
    Button(
      onClick = onWriteClick,
      shape = RoundedCornerShape(14.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = PrimaryBlue,
        contentColor = Color.White
      ),
      contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
      modifier = Modifier.testTag("write_button_${user.id}")
    ) {
      Text(
        text = "Yozish",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}

// Skeleton loading layout for Search Screen
@Composable
fun SearchSkeletonView(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
          }
          ShimmerBox(modifier = Modifier.weight(1f).height(46.dp), cornerRadius = 14.dp)
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
      repeat(5) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          ShimmerBox(modifier = Modifier.size(48.dp), cornerRadius = 24.dp)
          Spacer(modifier = Modifier.width(14.dp))
          Column(modifier = Modifier.weight(1f)) {
            ShimmerBox(modifier = Modifier.width(120.dp).height(16.dp), cornerRadius = 4.dp)
            Spacer(modifier = Modifier.height(6.dp))
            ShimmerBox(modifier = Modifier.width(80.dp).height(12.dp), cornerRadius = 4.dp)
          }
          ShimmerBox(modifier = Modifier.width(76.dp).height(34.dp), cornerRadius = 14.dp)
        }
        HorizontalDivider(
          modifier = Modifier.padding(start = 62.dp),
          color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
      }
    }
  }
}
