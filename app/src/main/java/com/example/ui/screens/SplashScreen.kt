package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
  onNavigateNext: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scale = remember { Animatable(0.6f) }

  LaunchedEffect(Unit) {
    scale.animateTo(
      targetValue = 1f,
      animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
      )
    )
    delay(1800)
    onNavigateNext()
  }

  // Gradient fon: ko'kdan to'q ko'kka (#2563EB dan #1E3A8A gacha)
  val gradientBrush = Brush.verticalGradient(
    colors = listOf(
      Color(0xFF3B82F6),
      Color(0xFF2563EB),
      Color(0xFF1E3A8A),
      Color(0xFF0F172A)
    )
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(gradientBrush)
      .testTag("splash_screen"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.scale(scale.value)
    ) {
      // Markazda ilova logosi (xabar pufakchasi ichida kulgich belgisi)
      Box(
        modifier = Modifier
          .size(120.dp)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.app_logo_icon_1793955155524),
          contentDescription = "Messenger Logo",
          modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .shadow(12.dp, CircleShape)
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Messenger",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        letterSpacing = 0.5.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Tezkor, qulay va xavfsiz muloqot",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White.copy(alpha = 0.8f)
      )
    }

    // Bottom brand tag
    Text(
      text = "Material 3 • Modern UX",
      style = MaterialTheme.typography.labelSmall,
      color = Color.White.copy(alpha = 0.6f),
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 36.dp)
    )
  }
}
