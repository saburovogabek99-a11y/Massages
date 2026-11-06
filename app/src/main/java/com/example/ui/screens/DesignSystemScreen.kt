package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Message
import com.example.ui.components.ButtonVariant
import com.example.ui.components.ChatBubble
import com.example.ui.components.DateDivider
import com.example.ui.components.GoogleSignInButton
import com.example.ui.components.MessengerButton
import com.example.ui.components.MessengerTextField
import com.example.ui.components.ShimmerBox
import com.example.ui.components.TypingIndicatorBubble
import com.example.ui.components.UnreadBadge
import com.example.ui.components.UserAvatar
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.BgDark
import com.example.ui.theme.BgLight
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BubbleMine
import com.example.ui.theme.BubbleOtherDark
import com.example.ui.theme.BubbleOtherLight
import com.example.ui.theme.CardDark
import com.example.ui.theme.CardLight
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueDark
import com.example.ui.theme.ReadCheckBlue

@Composable
fun DesignSystemScreen(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf(
    "Ranglar",
    "Shriftlar",
    "Tugmalar",
    "Input Maydonlari",
    "Pufakchalar",
    "Avatarlar & Badges"
  )

  Scaffold(
    modifier = modifier.testTag("design_system_screen"),
    topBar = {
      Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
      ) {
        Column {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(onClick = onBack) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Orqaga",
                tint = MaterialTheme.colorScheme.onSurface
              )
            }
            Column(modifier = Modifier.padding(start = 6.dp)) {
              Text(
                text = "Figma Dizayn Tizimi (Design System)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Material 3 • Telegram & Signal uslubi",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = PrimaryBlue
          ) {
            tabs.forEachIndexed { index, title ->
              Tab(
                selected = selectedTab == index,
                onClick = { selectedTab = index },
                text = {
                  Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                  )
                }
              )
            }
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
        .verticalScroll(rememberScrollState())
        .padding(20.dp)
    ) {
      when (selectedTab) {
        0 -> ColorPaletteSection()
        1 -> TypographySection()
        2 -> ButtonsSection()
        3 -> InputFieldsSection()
        4 -> ChatBubblesSection()
        5 -> AvatarsAndBadgesSection()
      }
      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}

// 1. Color Palette Section
@Composable
fun ColorPaletteSection() {
  val context = LocalContext.current
  val clipboard = LocalClipboardManager.current

  val copyHex: (String) -> Unit = { hex ->
    clipboard.setText(AnnotatedString(hex))
    Toast.makeText(context, "$hex nusxalandi!", Toast.LENGTH_SHORT).show()
  }

  Text(
    text = "Ranglar Palitrasi (Color Palette)",
    style = MaterialTheme.typography.titleLarge,
    fontWeight = FontWeight.Bold,
    color = MaterialTheme.colorScheme.onBackground
  )
  Spacer(modifier = Modifier.height(4.dp))
  Text(
    text = "Figma uchun qulay barcha brend va tizim ranglari. Nusxalash uchun ustiga bosing.",
    style = MaterialTheme.typography.bodyMedium,
    color = MaterialTheme.colorScheme.onSurfaceVariant
  )

  Spacer(modifier = Modifier.height(20.dp))

  Text(
    text = "Asosiy va Aksent Ranglar",
    style = MaterialTheme.typography.titleSmall,
    fontWeight = FontWeight.SemiBold,
    color = PrimaryBlue
  )
  Spacer(modifier = Modifier.height(10.dp))

  ColorCard("Asosiy Ko'k (Primary)", "#2563EB", PrimaryBlue, true, onCopy = copyHex)
  Spacer(modifier = Modifier.height(8.dp))
  ColorCard("To'q Ko'k (Primary Dark)", "#1D4ED8", PrimaryBlueDark, true, onCopy = copyHex)
  Spacer(modifier = Modifier.height(8.dp))
  ColorCard("Aksent Yashil (Online / Success)", "#22C55E", AccentGreen, true, onCopy = copyHex)
  Spacer(modifier = Modifier.height(8.dp))
  ColorCard("O'qildi Belgisi (Read Check)", "#60A5FA", ReadCheckBlue, true, onCopy = copyHex)
  Spacer(modifier = Modifier.height(8.dp))
  ColorCard("Xatolik Qizil (Error)", "#EF4444", ErrorRed, true, onCopy = copyHex)

  Spacer(modifier = Modifier.height(24.dp))
  Text(
    text = "Light Mavzu (Yorug')",
    style = MaterialTheme.typography.titleSmall,
    fontWeight = FontWeight.SemiBold,
    color = PrimaryBlue
  )
  Spacer(modifier = Modifier.height(10.dp))
  ColorCard("Fon (Background Light)", "#F8FAFC", BgLight, false, onCopy = copyHex)
  Spacer(modifier = Modifier.height(8.dp))
  ColorCard("Kartalar (Card Light)", "#FFFFFF", CardLight, false, onCopy = copyHex)
  Spacer(modifier = Modifier.height(8.dp))
  ColorCard("Suhbatdosh Pufakchasi", "#F1F5F9", BubbleOtherLight, false, onCopy = copyHex)

  Spacer(modifier = Modifier.height(24.dp))
  Text(
    text = "Dark Mavzu (Tungi)",
    style = MaterialTheme.typography.titleSmall,
    fontWeight = FontWeight.SemiBold,
    color = PrimaryBlue
  )
  Spacer(modifier = Modifier.height(10.dp))
  ColorCard("Fon (Background Dark)", "#0F172A", BgDark, true, onCopy = copyHex)
  Spacer(modifier = Modifier.height(8.dp))
  ColorCard("Kartalar (Card Dark)", "#1E293B", CardDark, true, onCopy = copyHex)
  Spacer(modifier = Modifier.height(8.dp))
  ColorCard("Suhbatdosh Pufakchasi Dark", "#334155", BubbleOtherDark, true, onCopy = copyHex)
}

@Composable
fun ColorCard(
  name: String,
  hex: String,
  color: Color,
  isDarkText: Boolean,
  onCopy: (String) -> Unit
) {
  val isDark = MaterialTheme.colorScheme.background == BgDark
  val borderColor = if (isDark) BorderDark else BorderLight

  Surface(
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.surface,
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, borderColor, RoundedCornerShape(16.dp))
      .clickable { onCopy(hex) }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(color)
          .border(1.dp, Color.Black.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
      )

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = name,
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = hex,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Icon(
        imageVector = Icons.Outlined.ContentCopy,
        contentDescription = "Nusxalash",
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(18.dp)
      )
    }
  }
}

// 2. Typography Section
@Composable
fun TypographySection() {
  Text(
    text = "Shriftlar Tizimi (Typography)",
    style = MaterialTheme.typography.titleLarge,
    fontWeight = FontWeight.Bold,
    color = MaterialTheme.colorScheme.onBackground
  )
  Spacer(modifier = Modifier.height(4.dp))
  Text(
    text = "Inter / Roboto shriftlari ierarxiyasi:",
    style = MaterialTheme.typography.bodyMedium,
    color = MaterialTheme.colorScheme.onSurfaceVariant
  )

  Spacer(modifier = Modifier.height(20.dp))

  TypeSampleCard(
    tag = "Sarlavha (Headline Large • 28sp Bold)",
    sample = "Katta Ekranga Kirish",
    fontSize = "28sp",
    weight = "Bold"
  ) {
    Text(
      text = "Katta Ekranga Kirish",
      style = MaterialTheme.typography.headlineLarge,
      color = MaterialTheme.colorScheme.onBackground
    )
  }

  Spacer(modifier = Modifier.height(14.dp))

  TypeSampleCard(
    tag = "Sarlavha (Title Large • 20sp Semibold)",
    sample = "Messenger Suhbatlar",
    fontSize = "20sp",
    weight = "SemiBold"
  ) {
    Text(
      text = "Messenger Suhbatlar",
      style = MaterialTheme.typography.titleLarge,
      color = MaterialTheme.colorScheme.onBackground
    )
  }

  Spacer(modifier = Modifier.height(14.dp))

  TypeSampleCard(
    tag = "Matn (Body Large • 16sp Regular)",
    sample = "Assalomu alaykum! Xabar pufakchasi ichidagi asosiy xabar matni.",
    fontSize = "16sp",
    weight = "Regular"
  ) {
    Text(
      text = "Assalomu alaykum! Xabar pufakchasi ichidagi asosiy xabar matni.",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onBackground
    )
  }

  Spacer(modifier = Modifier.height(14.dp))

  TypeSampleCard(
    tag = "Kichik Izoh (Label Small • 12sp)",
    sample = "Bugun 10:45 • O'qildi ✓✓ • 2 ta yangi xabar",
    fontSize = "12sp",
    weight = "Normal"
  ) {
    Text(
      text = "Bugun 10:45 • O'qildi ✓✓ • 2 ta yangi xabar",
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

@Composable
fun TypeSampleCard(
  tag: String,
  sample: String,
  fontSize: String,
  weight: String,
  content: @Composable () -> Unit
) {
  val isDark = MaterialTheme.colorScheme.background == BgDark
  val borderColor = if (isDark) BorderDark else BorderLight

  Surface(
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.surface,
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, borderColor, RoundedCornerShape(16.dp))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = tag,
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = PrimaryBlue
        )
        Text(
          text = "$fontSize / $weight",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
      content()
    }
  }
}

// 3. Buttons Section
@Composable
fun ButtonsSection() {
  Text(
    text = "Tugmalar To'plami (Buttons)",
    style = MaterialTheme.typography.titleLarge,
    fontWeight = FontWeight.Bold,
    color = MaterialTheme.colorScheme.onBackground
  )
  Spacer(modifier = Modifier.height(4.dp))
  Text(
    text = "Burchaklar 18dp yumaloq, yengil ripple va hover animatsiyalari:",
    style = MaterialTheme.typography.bodyMedium,
    color = MaterialTheme.colorScheme.onSurfaceVariant
  )

  Spacer(modifier = Modifier.height(20.dp))

  Text(text = "1. Katta Ko'k Asosiy Tugma (Primary)", style = MaterialTheme.typography.titleSmall, color = PrimaryBlue)
  Spacer(modifier = Modifier.height(8.dp))
  MessengerButton(
    text = "Kirish",
    onClick = {},
    variant = ButtonVariant.PRIMARY,
    modifier = Modifier.fillMaxWidth()
  )

  Spacer(modifier = Modifier.height(16.dp))
  Text(text = "2. Google Bilan Davom Etish (Google Sign-In)", style = MaterialTheme.typography.titleSmall, color = PrimaryBlue)
  Spacer(modifier = Modifier.height(8.dp))
  GoogleSignInButton(onClick = {}, modifier = Modifier.fillMaxWidth())

  Spacer(modifier = Modifier.height(16.dp))
  Text(text = "3. Chegarali Tugma (Outlined)", style = MaterialTheme.typography.titleSmall, color = PrimaryBlue)
  Spacer(modifier = Modifier.height(8.dp))
  MessengerButton(
    text = "Yangi suhbat ochish",
    onClick = {},
    variant = ButtonVariant.OUTLINED,
    modifier = Modifier.fillMaxWidth()
  )

  Spacer(modifier = Modifier.height(16.dp))
  Text(text = "4. Qizil Ogohlantirish Tugmasi (Danger)", style = MaterialTheme.typography.titleSmall, color = ErrorRed)
  Spacer(modifier = Modifier.height(8.dp))
  MessengerButton(
    text = "Chiqish",
    onClick = {},
    variant = ButtonVariant.DANGER,
    modifier = Modifier.fillMaxWidth()
  )

  Spacer(modifier = Modifier.height(16.dp))
  Text(text = "5. Suzuvchi Action Tugmasi (FAB)", style = MaterialTheme.typography.titleSmall, color = PrimaryBlue)
  Spacer(modifier = Modifier.height(8.dp))
  Row(verticalAlignment = Alignment.CenterVertically) {
    FloatingActionButton(
      onClick = {},
      containerColor = PrimaryBlue,
      contentColor = Color.White,
      shape = CircleShape
    ) {
      Icon(Icons.Default.Add, contentDescription = null)
    }
    Spacer(modifier = Modifier.width(14.dp))
    Text(
      text = "Pastki o'ng suzuvchi yangi chat tugmasi (+)",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

// 4. Input Fields Section
@Composable
fun InputFieldsSection() {
  var sampleUser by remember { mutableStateOf("sanobar_n") }
  var samplePass by remember { mutableStateOf("secret123") }
  var passVisible by remember { mutableStateOf(false) }

  Text(
    text = "Input Maydonlari (Form Inputs)",
    style = MaterialTheme.typography.titleLarge,
    fontWeight = FontWeight.Bold,
    color = MaterialTheme.colorScheme.onBackground
  )
  Spacer(modifier = Modifier.height(4.dp))
  Text(
    text = "Yumaloq burchaklar (16dp), ikonkalar, ko'z bilan ko'rsatish va xatolik holatlari:",
    style = MaterialTheme.typography.bodyMedium,
    color = MaterialTheme.colorScheme.onSurfaceVariant
  )

  Spacer(modifier = Modifier.height(20.dp))

  Text(text = "Foydalanuvchi nomi (Username Input)", style = MaterialTheme.typography.titleSmall, color = PrimaryBlue)
  Spacer(modifier = Modifier.height(8.dp))
  MessengerTextField(
    value = sampleUser,
    onValueChange = { sampleUser = it },
    placeholder = "Username",
    leadingIcon = Icons.Outlined.Person,
    modifier = Modifier.fillMaxWidth()
  )

  Spacer(modifier = Modifier.height(16.dp))

  Text(text = "Parol maydoni (Eye Toggle bilan)", style = MaterialTheme.typography.titleSmall, color = PrimaryBlue)
  Spacer(modifier = Modifier.height(8.dp))
  MessengerTextField(
    value = samplePass,
    onValueChange = { samplePass = it },
    placeholder = "Parol",
    leadingIcon = Icons.Outlined.Lock,
    isPassword = true,
    isPasswordVisible = passVisible,
    onPasswordToggle = { passVisible = !passVisible },
    modifier = Modifier.fillMaxWidth()
  )

  Spacer(modifier = Modifier.height(16.dp))

  Text(text = "Xatolik holatidagi input (Error State)", style = MaterialTheme.typography.titleSmall, color = ErrorRed)
  Spacer(modifier = Modifier.height(8.dp))
  MessengerTextField(
    value = "xato_login",
    onValueChange = {},
    placeholder = "Username",
    leadingIcon = Icons.Outlined.Person,
    isError = true,
    errorMessage = "Bu username allaqachon band yoki parol xato",
    modifier = Modifier.fillMaxWidth()
  )
}

// 5. Chat Bubbles Section
@Composable
fun ChatBubblesSection() {
  Text(
    text = "Xabarlar Pufakchalari (Chat Bubbles)",
    style = MaterialTheme.typography.titleLarge,
    fontWeight = FontWeight.Bold,
    color = MaterialTheme.colorScheme.onBackground
  )
  Spacer(modifier = Modifier.height(4.dp))
  Text(
    text = "O'zimniki (ko'k #2563EB), suhbatdoshniki (och kulrang/dark slate), vaqt va ✓ / ✓✓ belgilari:",
    style = MaterialTheme.typography.bodyMedium,
    color = MaterialTheme.colorScheme.onSurfaceVariant
  )

  Spacer(modifier = Modifier.height(16.dp))

  DateDivider(text = "Bugun")

  ChatBubble(
    message = Message(
      id = 1,
      senderId = 2,
      receiverId = 1,
      text = "Salom! Loyihaning dizayni tayyormi?",
      createdAt = "10:30",
      readAt = "10:31",
      delivered = true
    ),
    isMine = false
  )

  Spacer(modifier = Modifier.height(4.dp))

  ChatBubble(
    message = Message(
      id = 2,
      senderId = 1,
      receiverId = 2,
      text = "Ha, albatta! Material 3 andozalarida Telegram kabi toza minimalistik UI tayyorladik.",
      createdAt = "10:32",
      readAt = "10:33",
      delivered = true
    ),
    isMine = true
  )

  Spacer(modifier = Modifier.height(4.dp))

  ChatBubble(
    message = Message(
      id = 3,
      senderId = 1,
      receiverId = 2,
      text = "Hozircha o'qilmagan (bitta ✓ belgi)",
      createdAt = "10:35",
      readAt = null,
      delivered = true
    ),
    isMine = true
  )

  Spacer(modifier = Modifier.height(10.dp))

  TypingIndicatorBubble(username = "Alisher")
}

// 6. Avatars & Badges Section
@Composable
fun AvatarsAndBadgesSection() {
  Text(
    text = "Avatarlar, Badges & Shimmer Skeletons",
    style = MaterialTheme.typography.titleLarge,
    fontWeight = FontWeight.Bold,
    color = MaterialTheme.colorScheme.onBackground
  )
  Spacer(modifier = Modifier.height(4.dp))
  Text(
    text = "Online yashil nuqta (#22C55E), o'qilmagan xabarlar badge va shimmer yuklanish:",
    style = MaterialTheme.typography.bodyMedium,
    color = MaterialTheme.colorScheme.onSurfaceVariant
  )

  Spacer(modifier = Modifier.height(20.dp))

  Text(text = "Avatarlar (Online va Oflayn holatda)", style = MaterialTheme.typography.titleSmall, color = PrimaryBlue)
  Spacer(modifier = Modifier.height(10.dp))
  Row(
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    UserAvatar(name = "Ali", isOnline = true, size = 60.dp)
    UserAvatar(name = "Dilnoza", isOnline = true, size = 52.dp)
    UserAvatar(name = "Jasur", isOnline = false, size = 48.dp)
    UserAvatar(name = "Madina", isOnline = false, size = 40.dp)
  }

  Spacer(modifier = Modifier.height(24.dp))

  Text(text = "O'qilmagan xabarlar badge (Unread Badges)", style = MaterialTheme.typography.titleSmall, color = PrimaryBlue)
  Spacer(modifier = Modifier.height(10.dp))
  Row(
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    UnreadBadge(count = 1)
    UnreadBadge(count = 5)
    UnreadBadge(count = 42)
    UnreadBadge(count = 120)
  }

  Spacer(modifier = Modifier.height(24.dp))

  Text(text = "Shimmer Skeleton Bloklari", style = MaterialTheme.typography.titleSmall, color = PrimaryBlue)
  Spacer(modifier = Modifier.height(10.dp))
  Row(verticalAlignment = Alignment.CenterVertically) {
    ShimmerBox(modifier = Modifier.size(50.dp), cornerRadius = 25.dp)
    Spacer(modifier = Modifier.width(12.dp))
    Column(modifier = Modifier.weight(1f)) {
      ShimmerBox(modifier = Modifier.width(140.dp).height(16.dp), cornerRadius = 4.dp)
      Spacer(modifier = Modifier.height(6.dp))
      ShimmerBox(modifier = Modifier.fillMaxWidth().height(14.dp), cornerRadius = 4.dp)
    }
  }
}
