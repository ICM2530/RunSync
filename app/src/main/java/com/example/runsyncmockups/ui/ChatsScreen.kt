package com.example.runsyncmockups.ui

import BottomBarView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.runsyncmockups.Navigation.AppScreens
import com.example.runsyncmockups.model.ChatViewModel
import com.example.runsyncmockups.model.Conversation
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    chatViewModel: ChatViewModel = viewModel()
) {
    val chatState by chatViewModel.chatState.collectAsState()
    val totalUnread = chatViewModel.getTotalUnreadCount()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Mensajes")
                        if (totalUnread > 0) {
                            Text(
                                text = "$totalUnread no leídos",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(AppScreens.ListaAmigos.name)
                    }) {
                        Icon(
                            Icons.Default.PersonAdd,
                            contentDescription = "Nuevo chat"
                        )
                    }
                }
            )
        },
        bottomBar = { BottomBarView(navController) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                chatState.isLoading && chatState.conversations.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                chatState.conversations.isEmpty() -> {
                    EmptyChatsState(
                        onStartChat = {
                            navController.navigate(AppScreens.ListaAmigos.name)
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(chatState.conversations) { conversation ->
                            ConversationItem(
                                conversation = conversation,
                                onClick = {
                                    navController.navigate(
                                        "${AppScreens.ChatIndividual.name}/${conversation.friendId}/${conversation.friendName}/${conversation.friendEmail}"
                                    )
                                }
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationItem(
    conversation: Conversation,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Foto de perfil
        Box {
            if (conversation.friendImageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(conversation.friendImageUrl),
                    contentDescription = "Foto de ${conversation.friendName}",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Foto de ${conversation.friendName}",
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Badge de mensajes no leídos
            if (conversation.unreadCount > 0) {
                Badge(
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = if (conversation.unreadCount > 99) "99+" else conversation.unreadCount.toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Información del chat
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conversation.friendName,
                    fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = formatTimestamp(conversation.lastMessageTime),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = conversation.lastMessage,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = if (conversation.unreadCount > 0) 0.9f else 0.6f
                ),
                fontWeight = if (conversation.unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun EmptyChatsState(
    onStartChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ChatBubbleOutline,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Text(
            text = "No tienes conversaciones",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Inicia un chat con tus amigos para comenzar a conversar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Button(
            onClick = onStartChat,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ver Amigos")
        }
    }
}

fun formatTimestamp(timestamp: com.google.firebase.Timestamp): String {
    val now = Calendar.getInstance()
    val messageTime = Calendar.getInstance().apply {
        time = timestamp.toDate()
    }

    return when {
        now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == messageTime.get(Calendar.DAY_OF_YEAR) -> {
            // Hoy - mostrar hora
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp.toDate())
        }
        now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) - 1 == messageTime.get(Calendar.DAY_OF_YEAR) -> {
            "Ayer"
        }
        now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
                now.get(Calendar.WEEK_OF_YEAR) == messageTime.get(Calendar.WEEK_OF_YEAR) -> {
            // Esta semana - mostrar día
            SimpleDateFormat("EEEE", Locale.getDefault()).format(timestamp.toDate())
        }
        else -> {
            // Más antiguo - mostrar fecha
            SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(timestamp.toDate())
        }
    }
}