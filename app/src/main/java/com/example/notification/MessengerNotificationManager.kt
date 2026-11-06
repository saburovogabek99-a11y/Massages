package com.example.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

object MessengerNotificationManager {

  const val CHANNEL_ID = "messenger_messages_channel"
  private const val CHANNEL_NAME = "Yangi Xabarlar"
  private const val CHANNEL_DESCRIPTION = "Suhbatdoshlardan kelgan yangi xabarlar va bildirishnomalar"

  const val EXTRA_OPEN_USER_ID = "extra_open_user_id"
  const val EXTRA_OPEN_USERNAME = "extra_open_username"

  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
        description = CHANNEL_DESCRIPTION
        enableVibration(true)
        enableLights(true)
        setShowBadge(true)
        vibrationPattern = longArrayOf(0, 250, 150, 250)
      }
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
      notificationManager?.createNotificationChannel(channel)
    }
  }

  fun showMessageNotification(
    context: Context,
    senderId: Int,
    senderName: String,
    messageText: String
  ) {
    // Check permission on Android 13+ (Tiramisu / API 33+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ActivityCompat.checkSelfPermission(
          context,
          Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        return
      }
    }

    createNotificationChannel(context)

    // Intent to open chat directly when notification is clicked
    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
      putExtra(EXTRA_OPEN_USER_ID, senderId)
      putExtra(EXTRA_OPEN_USERNAME, senderName)
    }

    val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    } else {
      PendingIntent.FLAG_UPDATE_CURRENT
    }

    val pendingIntent = PendingIntent.getActivity(
      context,
      senderId,
      intent,
      pendingIntentFlags
    )

    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.drawable.app_logo_icon_1793955155524)
      .setContentTitle(senderName)
      .setContentText(messageText)
      .setStyle(NotificationCompat.BigTextStyle().bigText(messageText))
      .setAutoCancel(true)
      .setSound(defaultSoundUri)
      .setVibrate(longArrayOf(0, 250, 150, 250))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setCategory(NotificationCompat.CATEGORY_MESSAGE)
      .setContentIntent(pendingIntent)

    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.notify(senderId, builder.build())
  }
}
