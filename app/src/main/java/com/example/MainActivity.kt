package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.data.repository.MessengerRepository
import com.example.notification.MessengerNotificationManager
import com.example.ui.screens.MainAppContainer

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    MessengerNotificationManager.createNotificationChannel(applicationContext)
    val repository = MessengerRepository.getInstance(applicationContext)

    val notificationUserId = intent?.getIntExtra(MessengerNotificationManager.EXTRA_OPEN_USER_ID, -1)?.takeIf { it != -1 }
    val notificationUsername = intent?.getStringExtra(MessengerNotificationManager.EXTRA_OPEN_USERNAME)

    setContent {
      val context = LocalContext.current

      // Gracefully request POST_NOTIFICATIONS permission on Android 13+ (API 33+)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
          ActivityResultContracts.RequestPermission()
        ) { _ -> }

        LaunchedEffect(Unit) {
          if (ContextCompat.checkSelfPermission(
              context,
              Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
          ) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
          }
        }
      }

      Surface(modifier = Modifier.fillMaxSize()) {
        MainAppContainer(
          repository = repository,
          initialChatUserId = notificationUserId,
          initialChatUsername = notificationUsername
        )
      }
    }
  }
}
