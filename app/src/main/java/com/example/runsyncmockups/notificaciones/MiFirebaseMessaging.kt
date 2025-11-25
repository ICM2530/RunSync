package com.example.runsyncmockups.notificaciones

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.runsyncmockups.MainActivity
import com.example.runsyncmockups.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        saveTokenToDatabase(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        println("NOTIFICACIÓN RECIBIDA - Datos: ${message.data}")

        // Extraer datos del mensaje
        val senderId = message.data["senderId"] ?: return
        val senderName = message.data["senderName"] ?: "Usuario"
        val messageText = message.data["messageText"] ?: ""
        val conversationId = message.data["conversationId"] ?: ""

        println(" Procesando notificación: $senderName - $messageText")

        // Mostrar notificación
        showNotification(senderId, senderName, messageText, conversationId)
    }

    private fun showNotification(
        senderId: String,
        senderName: String,
        messageText: String,
        conversationId: String
    ) {
        val channelId = "chat_messages"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal de notificación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Mensajes de Chat",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de mensajes nuevos"
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir el chat cuando se toque la notificación
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("openChat", true)
            putExtra("friendId", senderId)
            putExtra("friendName", senderName)
            putExtra("conversationId", conversationId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            "${senderId}_${conversationId}".hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construir la notificación
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notifi)
            .setContentTitle(senderName)
            .setContentText(messageText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .build()

        notificationManager.notify("${senderId}_${conversationId}".hashCode(), notification)
    }

    private fun saveTokenToDatabase(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        println("Guardando nuevo token FCM: ${token.take(10)}...")

        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("fcmToken")
            .setValue(token)
            .addOnSuccessListener {
                println("Token FCM guardado CORRECTAMENTE")
            }
            .addOnFailureListener { e ->
                println(" Error al guardar token FCM: ${e.message}")
            }
    }
}