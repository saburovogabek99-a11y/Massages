package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UiMode
import com.example.data.repository.MessengerRepository
import com.example.ui.components.ErrorStateView
import com.example.ui.components.GoogleSignInButton
import com.example.ui.components.ShimmerBox
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.BgDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
  repository: MessengerRepository,
  uiMode: UiMode,
  onLoginSuccess: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  // Check skeleton/loading state
  if (uiMode == UiMode.SKELETON) {
    LoginSkeletonView(modifier = modifier)
    return
  }

  // Check error state preview
  if (uiMode == UiMode.ERROR) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
      ErrorStateView(
        title = "Google orqali kirishda xatolik",
        message = "Google autentifikatsiyasida kutilmagan xatolik yuz berdi. Iltimos, qayta urinib ko'ring.",
        onRetry = { repository.setUiMode(UiMode.NORMAL) },
        modifier = Modifier.align(Alignment.Center)
      )
    }
    return
  }

  val isDark = MaterialTheme.colorScheme.background == BgDark
  val cardBg = if (isDark) MaterialTheme.colorScheme.surface else Color.White
  val cardBorder = if (isDark) BorderDark else BorderLight

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("login_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Spacer(modifier = Modifier.height(24.dp))

      // Markazda katta logo (xabar pufakchasi ichida kulgich)
      Box(
        modifier = Modifier
          .size(96.dp)
          .clip(CircleShape)
          .background(PrimaryBlue.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.app_logo_icon_1793955155524),
          contentDescription = "Messenger Logo",
          modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .shadow(8.dp, CircleShape)
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Xush kelibsiz!",
        style = MaterialTheme.typography.titleLarge,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Tezkor, qulay va xavfsiz muloqot qilish uchun Google hisobingiz orqali tizimga kiring",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        lineHeight = 22.sp,
        modifier = Modifier.padding(horizontal = 12.dp)
      )

      Spacer(modifier = Modifier.height(36.dp))

      // Afzalliklar / Xavfsizlik kartasi (Trust & Security highlights)
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = cardBg,
        shadowElevation = 0.5.dp,
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, cardBorder, RoundedCornerShape(20.dp))
      ) {
        Column(
          modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          FeatureHighlightRow(
            icon = Icons.Outlined.Security,
            iconTint = PrimaryBlue,
            title = "Xavfsiz Autentifikatsiya",
            description = "Google Identity xavfsizlik standartlari asosida himoyalangan"
          )
          FeatureHighlightRow(
            icon = Icons.Outlined.FlashOn,
            iconTint = Color(0xFFF59E0B),
            title = "Bitta bosishda bir zumda kirish",
            description = "Parolni eslab qolish shart emas, tezkor va oson"
          )
          FeatureHighlightRow(
            icon = Icons.Outlined.CloudDone,
            iconTint = AccentGreen,
            title = "Doimiy Sinxronizatsiya",
            description = "Suhbatlar va yangi xabarlaringiz doim yoningizda saqlanadi"
          )
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      if (errorMessage != null) {
        Text(
          text = errorMessage!!,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.error,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(bottom = 12.dp)
        )
      }

      // Faqat Google bilan davom etish tugmasi
      GoogleSignInButton(
        onClick = {
          isLoading = true
          errorMessage = null
          scope.launch {
            val res = repository.googleLogin()
            isLoading = false
            if (res.isSuccess) {
              onLoginSuccess()
            } else {
              errorMessage = res.exceptionOrNull()?.message ?: "Kirishda xatolik yuz berdi"
            }
          }
        },
        isLoading = isLoading,
        modifier = Modifier.fillMaxWidth(),
        testTag = "google_login_button"
      )

      Spacer(modifier = Modifier.height(28.dp))

      // Foydalanish shartlari va Maxfiylik eslatmasi
      Text(
        text = "Davom etish orqali siz Messenger xizmatining Foydalanish qoidalari va Maxfiylik siyosatiga rozilik bildirasiz.",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
        textAlign = TextAlign.Center,
        lineHeight = 16.sp,
        modifier = Modifier.padding(horizontal = 16.dp)
      )

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

@Composable
fun FeatureHighlightRow(
  icon: ImageVector,
  iconTint: Color,
  title: String,
  description: String
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth()
  ) {
    Box(
      modifier = Modifier
        .size(38.dp)
        .clip(CircleShape)
        .background(iconTint.copy(alpha = 0.12f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = iconTint,
        modifier = Modifier.size(20.dp)
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = description,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 15.sp
      )
    }
  }
}

// Skeleton view for Google-only Login screen
@Composable
fun LoginSkeletonView(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(24.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      ShimmerBox(modifier = Modifier.size(96.dp), cornerRadius = 48.dp)
      Spacer(modifier = Modifier.height(24.dp))
      ShimmerBox(modifier = Modifier.width(200.dp).height(30.dp), cornerRadius = 8.dp)
      Spacer(modifier = Modifier.height(12.dp))
      ShimmerBox(modifier = Modifier.width(280.dp).height(18.dp), cornerRadius = 6.dp)
      Spacer(modifier = Modifier.height(8.dp))
      ShimmerBox(modifier = Modifier.width(220.dp).height(18.dp), cornerRadius = 6.dp)
      Spacer(modifier = Modifier.height(36.dp))
      ShimmerBox(modifier = Modifier.fillMaxWidth().height(140.dp), cornerRadius = 20.dp)
      Spacer(modifier = Modifier.height(32.dp))
      ShimmerBox(modifier = Modifier.fillMaxWidth().height(52.dp), cornerRadius = 18.dp)
      Spacer(modifier = Modifier.height(20.dp))
      ShimmerBox(modifier = Modifier.width(240.dp).height(14.dp), cornerRadius = 4.dp)
    }
  }
}
