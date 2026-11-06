package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UiMode
import com.example.data.repository.MessengerRepository
import com.example.ui.components.ButtonVariant
import com.example.ui.components.ErrorStateView
import com.example.ui.components.MessengerButton
import com.example.ui.components.ShimmerBox
import com.example.ui.components.UserAvatar
import com.example.ui.theme.BgDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryBlue

@Composable
fun ProfileScreen(
  repository: MessengerRepository,
  uiMode: UiMode,
  onBack: () -> Unit,
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentUser by repository.currentUser.collectAsState()
  val isDarkTheme by repository.isDarkTheme.collectAsState()
  val notificationsEnabled by repository.notificationsEnabled.collectAsState()

  // Handle Skeleton State
  if (uiMode == UiMode.SKELETON) {
    ProfileSkeletonView(onBack = onBack, modifier = modifier)
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
        title = "Profil yuklanmadi",
        message = "Foydalanuvchi ma'lumotlarini olishda xatolik yuz berdi.",
        onRetry = { repository.setUiMode(UiMode.NORMAL) },
        modifier = Modifier.align(Alignment.Center)
      )
    }
    return
  }

  val isDark = MaterialTheme.colorScheme.background == BgDark
  val cardBg = if (isDark) MaterialTheme.colorScheme.surface else Color.White
  val cardBorder = if (isDark) BorderDark else BorderLight

  Scaffold(
    modifier = modifier.testTag("profile_screen"),
    topBar = {
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
            modifier = Modifier.size(42.dp)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Orqaga",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }

          Text(
            text = "Profil va Sozlamalar",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp)
          )
        }
      }
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(12.dp))

      // Katta avatar, ism, email
      Box(
        modifier = Modifier.size(100.dp),
        contentAlignment = Alignment.Center
      ) {
        UserAvatar(
          name = currentUser.username,
          isOnline = currentUser.online,
          size = 100.dp,
          showOnlineBadge = true
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = currentUser.username,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = currentUser.email ?: "ali.vali@example.com",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(28.dp))

      // Ostida kartalar: mavzu (Light/Dark o'tkazgich), bildirishnomalar
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = cardBg,
        shadowElevation = 0.5.dp,
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, cardBorder, RoundedCornerShape(20.dp))
      ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
          // Mavzu (Light/Dark o'tkazgich)
          SettingItemWithSwitch(
            icon = Icons.Outlined.DarkMode,
            title = "Tungi mavzu (Dark mode)",
            subtitle = if (isDarkTheme) "Tungi rejim yoqilgan" else "Kunduzgi rejim yoqilgan",
            checked = isDarkTheme,
            onCheckedChange = { repository.setDarkTheme(it) },
            testTag = "dark_theme_switch"
          )

          HorizontalDivider(
            modifier = Modifier.padding(start = 56.dp, end = 16.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
          )

          // Bildirishnomalar
          SettingItemWithSwitch(
            icon = Icons.Outlined.Notifications,
            title = "Bildirishnomalar",
            subtitle = if (notificationsEnabled) "Yangi xabarlar ko'rsatiladi" else "Ovoz va bildirishnomalar o'chirilgan",
            checked = notificationsEnabled,
            onCheckedChange = { repository.setNotificationsEnabled(it) },
            testTag = "notifications_switch"
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Security / Privacy Card
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = cardBg,
        shadowElevation = 0.5.dp,
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, cardBorder, RoundedCornerShape(20.dp))
      ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
          SettingItemNavigation(
            icon = Icons.Outlined.Security,
            title = "Xavfsizlik va Maxfiylik",
            subtitle = "Shifrlash va aktiv seanslar",
            onClick = {}
          )
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      // "Chiqish" qizil tugma
      MessengerButton(
        text = "Chiqish",
        icon = Icons.AutoMirrored.Filled.ExitToApp,
        variant = ButtonVariant.DANGER,
        onClick = {
          repository.logout()
          onLogout()
        },
        modifier = Modifier.fillMaxWidth(),
        testTag = "profile_logout_button"
      )

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun SettingItemWithSwitch(
  icon: ImageVector,
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  testTag: String = "setting_switch"
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(40.dp)
        .clip(CircleShape)
        .background(PrimaryBlue.copy(alpha = 0.1f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = PrimaryBlue,
        modifier = Modifier.size(22.dp)
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = PrimaryBlue
      ),
      modifier = Modifier.testTag(testTag)
    )
  }
}

@Composable
fun SettingItemNavigation(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(40.dp)
        .clip(CircleShape)
        .background(PrimaryBlue.copy(alpha = 0.1f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = PrimaryBlue,
        modifier = Modifier.size(22.dp)
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Icon(
      imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
      modifier = Modifier.size(20.dp)
    )
  }
}

// Skeleton view for Profile Screen
@Composable
fun ProfileSkeletonView(
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
            .padding(horizontal = 8.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
          }
          ShimmerBox(modifier = Modifier.width(140.dp).height(20.dp), cornerRadius = 6.dp)
        }
      }
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(padding)
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(16.dp))
      ShimmerBox(modifier = Modifier.size(100.dp), cornerRadius = 50.dp)
      Spacer(modifier = Modifier.height(18.dp))
      ShimmerBox(modifier = Modifier.width(160.dp).height(22.dp), cornerRadius = 6.dp)
      Spacer(modifier = Modifier.height(8.dp))
      ShimmerBox(modifier = Modifier.width(200.dp).height(16.dp), cornerRadius = 4.dp)
      Spacer(modifier = Modifier.height(32.dp))
      ShimmerBox(modifier = Modifier.fillMaxWidth().height(160.dp), cornerRadius = 20.dp)
      Spacer(modifier = Modifier.height(24.dp))
      ShimmerBox(modifier = Modifier.fillMaxWidth().height(52.dp), cornerRadius = 18.dp)
    }
  }
}
