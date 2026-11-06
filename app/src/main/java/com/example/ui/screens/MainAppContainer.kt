package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.model.Screen
import com.example.data.model.User
import com.example.data.repository.MessengerRepository
import com.example.ui.components.InAppNotificationBanner
import com.example.ui.theme.MessengerTheme
import kotlinx.coroutines.delay

@Composable
fun MainAppContainer(
  repository: MessengerRepository,
  initialChatUserId: Int? = null,
  initialChatUsername: String? = null
) {
  val isDarkTheme by repository.isDarkTheme.collectAsState()
  val uiMode by repository.uiMode.collectAsState()
  val token by repository.token.collectAsState()
  val latestNotification by repository.latestNotification.collectAsState()

  var currentScreen by remember {
    mutableStateOf<Screen>(
      if (initialChatUserId != null) {
        Screen.ChatDetail(
          User(
            id = initialChatUserId,
            username = initialChatUsername ?: "Suhbatdosh",
            online = true
          )
        )
      } else {
        Screen.Splash
      }
    )
  }

  // Auto-dismiss in-app notification banner after 4.5 seconds
  LaunchedEffect(latestNotification) {
    if (latestNotification != null) {
      delay(4500)
      repository.clearLatestNotification()
    }
  }

  MessengerTheme(darkTheme = isDarkTheme) {
    Box(modifier = Modifier.fillMaxSize()) {
      // Main screen content with smooth Crossfade
      Crossfade(
        targetState = currentScreen,
        label = "screen_crossfade",
        modifier = Modifier.fillMaxSize()
      ) { screen ->
        when (screen) {
          is Screen.Splash -> {
            SplashScreen(
              onNavigateNext = {
                currentScreen = if (token != null) {
                  Screen.ChatsList
                } else {
                  Screen.Login
                }
              }
            )
          }

          is Screen.Login -> {
            LoginScreen(
              repository = repository,
              uiMode = uiMode,
              onLoginSuccess = {
                currentScreen = Screen.ChatsList
              }
            )
          }

          is Screen.ChatsList -> {
            ChatsListScreen(
              repository = repository,
              uiMode = uiMode,
              onOpenChat = { user ->
                currentScreen = Screen.ChatDetail(user)
              },
              onOpenSearch = {
                currentScreen = Screen.Search
              },
              onOpenProfile = {
                currentScreen = Screen.Profile
              }
            )
          }

          is Screen.Search -> {
            SearchScreen(
              repository = repository,
              uiMode = uiMode,
              onBack = {
                currentScreen = Screen.ChatsList
              },
              onOpenChat = { user ->
                currentScreen = Screen.ChatDetail(user)
              }
            )
          }

          is Screen.ChatDetail -> {
            ChatScreen(
              user = screen.user,
              repository = repository,
              uiMode = uiMode,
              onBack = {
                currentScreen = Screen.ChatsList
              }
            )
          }

          is Screen.Profile -> {
            ProfileScreen(
              repository = repository,
              uiMode = uiMode,
              onBack = {
                currentScreen = Screen.ChatsList
              },
              onLogout = {
                currentScreen = Screen.Login
              }
            )
          }

          is Screen.DesignSystem -> {
            ChatsListScreen(
              repository = repository,
              uiMode = uiMode,
              onOpenChat = { user -> currentScreen = Screen.ChatDetail(user) },
              onOpenSearch = { currentScreen = Screen.Search },
              onOpenProfile = { currentScreen = Screen.Profile }
            )
          }
        }
      }

      // Elegant In-App Top Notification Banner (Telegram/Signal style)
      AnimatedVisibility(
        visible = latestNotification != null,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
        modifier = Modifier
          .align(Alignment.TopCenter)
          .statusBarsPadding()
          .padding(top = 4.dp)
      ) {
        latestNotification?.let { notif ->
          InAppNotificationBanner(
            notification = notif,
            onClick = {
              currentScreen = Screen.ChatDetail(
                User(id = notif.senderId, username = notif.senderName, online = true)
              )
              repository.clearLatestNotification()
            },
            onDismiss = {
              repository.clearLatestNotification()
            }
          )
        }
      }
    }
  }
}
