package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InAppNotification
import com.example.data.model.Message
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.BgDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BubbleMine
import com.example.ui.theme.BubbleMineText
import com.example.ui.theme.BubbleOtherDark
import com.example.ui.theme.BubbleOtherLight
import com.example.ui.theme.BubbleOtherTextDark
import com.example.ui.theme.BubbleOtherTextLight
import com.example.ui.theme.CardDark
import com.example.ui.theme.CardLight
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.ReadCheckBlue
import kotlin.math.absoluteValue

// Shimmer gradient modifier
@Composable
fun shimmerBrush(targetValue: Float = 1000f): Brush {
  val shimmerColors = listOf(
    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
  )

  val transition = rememberInfiniteTransition(label = "shimmer_transition")
  val translateAnimation = transition.animateFloat(
    initialValue = 0f,
    targetValue = targetValue,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "shimmer_float"
  )

  return Brush.linearGradient(
    colors = shimmerColors,
    start = Offset.Zero,
    end = Offset(x = translateAnimation.value, y = translateAnimation.value)
  )
}

@Composable
fun ShimmerBox(
  modifier: Modifier = Modifier,
  cornerRadius: Dp = 12.dp
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(cornerRadius))
      .background(shimmerBrush())
  )
}

// User Avatar with distinctive pastel background + online green indicator
@Composable
fun UserAvatar(
  name: String,
  modifier: Modifier = Modifier,
  isOnline: Boolean = false,
  size: Dp = 48.dp,
  showOnlineBadge: Boolean = true
) {
  val initial = name.trim().take(1).uppercase().ifEmpty { "?" }
  val avatarColors = listOf(
    Color(0xFF2563EB),
    Color(0xFF7C3AED),
    Color(0xFF0D9488),
    Color(0xFF059669),
    Color(0xFFD97706),
    Color(0xFFDC2626),
    Color(0xFF4F46E5)
  )
  val bgColor = avatarColors[(name.hashCode().absoluteValue) % avatarColors.size]

  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .size(size)
        .clip(CircleShape)
        .background(bgColor),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = initial,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = (size.value * 0.42f).sp
      )
    }

    if (showOnlineBadge && isOnline) {
      Box(
        modifier = Modifier
          .size(size * 0.28f)
          .align(Alignment.BottomEnd)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surface)
          .padding((size * 0.04f).coerceAtLeast(1.5.dp))
          .clip(CircleShape)
          .background(AccentGreen)
          .testTag("avatar_online_badge")
      )
    }
  }
}

// Unread Messages Badge
@Composable
fun UnreadBadge(
  count: Int,
  modifier: Modifier = Modifier
) {
  if (count <= 0) return
  val text = if (count > 99) "99+" else count.toString()
  Box(
    modifier = modifier
      .size(24.dp)
      .clip(CircleShape)
      .background(PrimaryBlue)
      .testTag("unread_badge"),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      color = Color.White,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center
    )
  }
}

// Date Divider (e.g., "Bugun", "Kecha")
@Composable
fun DateDivider(
  text: String,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 12.dp),
    contentAlignment = Alignment.Center
  ) {
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
      shadowElevation = 0.5.dp
    ) {
      Text(
        text = text,
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

// Button Variants
enum class ButtonVariant {
  PRIMARY,
  SECONDARY,
  OUTLINED,
  DANGER
}

@Composable
fun MessengerButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  variant: ButtonVariant = ButtonVariant.PRIMARY,
  icon: ImageVector? = null,
  enabled: Boolean = true,
  isLoading: Boolean = false,
  testTag: String = "messenger_button"
) {
  when (variant) {
    ButtonVariant.PRIMARY -> {
      Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = PrimaryBlue,
          contentColor = Color.White,
          disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f)
        ),
        modifier = modifier
          .height(52.dp)
          .testTag(testTag)
      ) {
        if (isLoading) {
          CircularProgressIndicator(
            modifier = Modifier.size(22.dp),
            color = Color.White,
            strokeWidth = 2.5.dp
          )
        } else {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            if (icon != null) {
              Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
              text = text,
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }
    ButtonVariant.DANGER -> {
      Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = ErrorRed,
          contentColor = Color.White
        ),
        modifier = modifier
          .height(52.dp)
          .testTag(testTag)
      ) {
        if (isLoading) {
          CircularProgressIndicator(
            modifier = Modifier.size(22.dp),
            color = Color.White,
            strokeWidth = 2.5.dp
          )
        } else {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            if (icon != null) {
              Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }
    ButtonVariant.OUTLINED -> {
      OutlinedButton(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
          .height(52.dp)
          .testTag(testTag)
      ) {
        if (icon != null) {
          Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
      }
    }
    ButtonVariant.SECONDARY -> {
      Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant,
          contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier
          .height(52.dp)
          .testTag(testTag)
      ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
      }
    }
  }
}

// Google Sign In Button (white with border and Google "G" icon)
@Composable
fun GoogleSignInButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isLoading: Boolean = false,
  testTag: String = "google_sign_in_button"
) {
  val isDark = MaterialTheme.colorScheme.background == BgDark
  val containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White
  val borderColor = if (isDark) BorderDark else BorderLight

  Surface(
    onClick = onClick,
    enabled = !isLoading,
    shape = RoundedCornerShape(18.dp),
    color = containerColor,
    shadowElevation = 1.dp,
    modifier = modifier
      .fillMaxWidth()
      .height(52.dp)
      .border(1.dp, borderColor, RoundedCornerShape(18.dp))
      .testTag(testTag)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (isLoading) {
        CircularProgressIndicator(
          modifier = Modifier.size(20.dp),
          color = PrimaryBlue,
          strokeWidth = 2.dp
        )
      } else {
        // Draw Google G
        GoogleLogo(modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = "Google bilan davom etish",
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    }
  }
}

// Crisp Google Logo icon drawn on Canvas
@Composable
fun GoogleLogo(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height
    // Outer circle arcs with 4 standard Google colors: Red, Yellow, Green, Blue
    val center = Offset(w / 2f, h / 2f)
    val radius = w * 0.42f
    val strokeW = w * 0.18f

    // Blue horizontal bar
    drawLine(
      color = Color(0xFF4285F4),
      start = Offset(center.x, center.y),
      end = Offset(w * 0.88f, center.y),
      strokeWidth = strokeW
    )
    // Blue arc
    drawArc(
      color = Color(0xFF4285F4),
      startAngle = -45f,
      sweepAngle = 90f,
      useCenter = false,
      style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeW)
    )
    // Green arc
    drawArc(
      color = Color(0xFF34A853),
      startAngle = 45f,
      sweepAngle = 90f,
      useCenter = false,
      style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeW)
    )
    // Yellow arc
    drawArc(
      color = Color(0xFFFBBC05),
      startAngle = 135f,
      sweepAngle = 90f,
      useCenter = false,
      style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeW)
    )
    // Red arc
    drawArc(
      color = Color(0xFFEA4335),
      startAngle = 225f,
      sweepAngle = 90f,
      useCenter = false,
      style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeW)
    )
  }
}

// Input Field Component
@Composable
fun MessengerTextField(
  value: String,
  onValueChange: (String) -> Unit,
  placeholder: String,
  modifier: Modifier = Modifier,
  leadingIcon: ImageVector? = null,
  isPassword: Boolean = false,
  isPasswordVisible: Boolean = false,
  onPasswordToggle: (() -> Unit)? = null,
  isError: Boolean = false,
  errorMessage: String? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  testTag: String = "messenger_text_field"
) {
  val isDark = MaterialTheme.colorScheme.background == BgDark
  val bgColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White
  val borderColor = when {
    isError -> ErrorRed
    else -> if (isDark) BorderDark else BorderLight
  }

  Column(modifier = modifier) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = bgColor,
      shadowElevation = 0.5.dp,
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .border(1.dp, borderColor, RoundedCornerShape(16.dp))
        .testTag(testTag)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (leadingIcon != null) {
          Icon(
            imageVector = leadingIcon,
            contentDescription = null,
            tint = if (isError) ErrorRed else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
        }

        Box(modifier = Modifier.weight(1f)) {
          if (value.isEmpty()) {
            Text(
              text = placeholder,
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
          }
          BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
              color = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = true,
            visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            cursorBrush = SolidColor(PrimaryBlue),
            modifier = Modifier.fillMaxWidth()
          )
        }

        if (isPassword && onPasswordToggle != null) {
          IconButton(
            onClick = onPasswordToggle,
            modifier = Modifier.size(36.dp)
          ) {
            Icon(
              imageVector = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
              contentDescription = if (isPasswordVisible) "Parolni berkitish" else "Parolni ko'rsatish",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }

    if (isError && !errorMessage.isNullOrEmpty()) {
      Text(
        text = errorMessage,
        color = ErrorRed,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
      )
    }
  }
}

// Chat Bubble Component with long-press context menu
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ChatBubble(
  message: Message,
  isMine: Boolean,
  modifier: Modifier = Modifier,
  onDeleteClick: (() -> Unit)? = null,
  onCopyClick: (() -> Unit)? = null
) {
  var showMenu by remember { mutableStateOf(false) }

  val isDark = MaterialTheme.colorScheme.background == BgDark
  val bubbleBg = if (isMine) PrimaryBlue else if (isDark) BubbleOtherDark else BubbleOtherLight
  val textColor = if (isMine) BubbleMineText else if (isDark) BubbleOtherTextDark else BubbleOtherTextLight
  val timeColor = if (isMine) Color.White.copy(alpha = 0.75f) else MaterialTheme.colorScheme.onSurfaceVariant

  val bubbleShape = if (isMine) {
    RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp)
  } else {
    RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp)
  }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 3.dp),
    horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
  ) {
    Box {
      Surface(
        shape = bubbleShape,
        color = bubbleBg,
        shadowElevation = 0.5.dp,
        modifier = Modifier
          .widthIn(min = 70.dp, max = 280.dp)
          .combinedClickable(
            onClick = { /* normal click */ },
            onLongClick = { showMenu = true }
          )
          .testTag(if (isMine) "chat_bubble_mine" else "chat_bubble_other")
      ) {
        Column(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
          Text(
            text = message.text,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            lineHeight = 22.sp
          )

          Spacer(modifier = Modifier.height(3.dp))

          Row(
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = message.createdAt,
              style = MaterialTheme.typography.labelSmall,
              color = timeColor,
              fontSize = 11.sp
            )

            if (isMine) {
              Spacer(modifier = Modifier.width(4.dp))
              if (message.readAt != null) {
                // Read: double checkmark
                Icon(
                  imageVector = Icons.Default.DoneAll,
                  contentDescription = "O'qildi",
                  tint = ReadCheckBlue,
                  modifier = Modifier.size(14.dp)
                )
              } else {
                // Delivered: single checkmark
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Yuborildi",
                  tint = Color.White.copy(alpha = 0.8f),
                  modifier = Modifier.size(13.dp)
                )
              }
            }
          }
        }
      }

      // Context Dropdown Menu on long press
      DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
      ) {
        DropdownMenuItem(
          text = { Text("Nusxa olish") },
          onClick = {
            showMenu = false
            onCopyClick?.invoke()
          }
        )
        if (isMine && onDeleteClick != null) {
          DropdownMenuItem(
            text = { Text("Xabarni o'chirish", color = ErrorRed) },
            onClick = {
              showMenu = false
              onDeleteClick()
            }
          )
        }
      }
    }
  }
}

// Typing Indicator Bubble
@Composable
fun TypingIndicatorBubble(username: String) {
  val transition = rememberInfiniteTransition(label = "typing_dots")
  val dot1 by transition.animateFloat(
    initialValue = 0f,
    targetValue = -6f,
    animationSpec = infiniteRepeatable(
      animation = tween(400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "dot1"
  )
  val dot2 by transition.animateFloat(
    initialValue = 0f,
    targetValue = -6f,
    animationSpec = infiniteRepeatable(
      animation = tween(400, delayMillis = 150, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "dot2"
  )
  val dot3 by transition.animateFloat(
    initialValue = 0f,
    targetValue = -6f,
    animationSpec = infiniteRepeatable(
      animation = tween(400, delayMillis = 300, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "dot3"
  )

  val isDark = MaterialTheme.colorScheme.background == BgDark
  val bubbleBg = if (isDark) BubbleOtherDark else BubbleOtherLight

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 4.dp),
    horizontalArrangement = Arrangement.Start
  ) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = bubbleBg,
      modifier = Modifier.padding(top = 2.dp)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "$username yozmoqda",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
          Box(
            modifier = Modifier
              .offset(y = dot1.dp)
              .size(6.dp)
              .clip(CircleShape)
              .background(PrimaryBlue)
          )
          Box(
            modifier = Modifier
              .offset(y = dot2.dp)
              .size(6.dp)
              .clip(CircleShape)
              .background(PrimaryBlue)
          )
          Box(
            modifier = Modifier
              .offset(y = dot3.dp)
              .size(6.dp)
              .clip(CircleShape)
              .background(PrimaryBlue)
          )
        }
      }
    }
  }
}

// Empty State View
@Composable
fun EmptyStateView(
  title: String,
  subtitle: String,
  modifier: Modifier = Modifier,
  actionText: String? = null,
  onActionClick: (() -> Unit)? = null
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    // Friendly Empty Illustration Bubble
    Surface(
      shape = CircleShape,
      color = PrimaryBlue.copy(alpha = 0.1f),
      modifier = Modifier.size(90.dp)
    ) {
      Box(contentAlignment = Alignment.Center) {
        Text(text = "💬", fontSize = 38.sp)
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    Text(
      text = title,
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colorScheme.onBackground,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = subtitle,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
      lineHeight = 20.sp
    )

    if (actionText != null && onActionClick != null) {
      Spacer(modifier = Modifier.height(20.dp))
      MessengerButton(
        text = actionText,
        onClick = onActionClick,
        variant = ButtonVariant.PRIMARY
      )
    }
  }
}

// Error State View
@Composable
fun ErrorStateView(
  title: String = "Xatolik yuz berdi",
  message: String = "Server bilan bog'lanishda muammo bo'ldi. Qaytadan urinib ko'ring.",
  onRetry: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Surface(
      shape = CircleShape,
      color = ErrorRed.copy(alpha = 0.12f),
      modifier = Modifier.size(80.dp)
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = Icons.Default.ErrorOutline,
          contentDescription = null,
          tint = ErrorRed,
          modifier = Modifier.size(38.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    Text(
      text = title,
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colorScheme.onBackground,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = message,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(22.dp))

    MessengerButton(
      text = "Qayta urinish",
      icon = Icons.Outlined.Refresh,
      onClick = onRetry,
      variant = ButtonVariant.PRIMARY
    )
  }
}

// In-App Notification Banner (Telegram/Signal style top drop-down banner)
@Composable
fun InAppNotificationBanner(
  notification: InAppNotification,
  onClick: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = MaterialTheme.colorScheme.background == BgDark
  val bannerBg = if (isDark) CardDark else CardLight
  val borderColor = if (isDark) BorderDark else BorderLight

  Surface(
    shape = RoundedCornerShape(20.dp),
    color = bannerBg,
    shadowElevation = 8.dp,
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp)
      .border(1.dp, borderColor, RoundedCornerShape(20.dp))
      .clickable { onClick() }
      .testTag("in_app_notification_banner")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      UserAvatar(
        name = notification.senderName,
        isOnline = true,
        size = 44.dp,
        showOnlineBadge = true
      )

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = notification.senderName,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "Hozirgina",
            style = MaterialTheme.typography.labelSmall,
            color = PrimaryBlue,
            fontSize = 11.sp
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = notification.messageText,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      Spacer(modifier = Modifier.width(6.dp))

      IconButton(
        onClick = onDismiss,
        modifier = Modifier.size(32.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Yopish",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}

