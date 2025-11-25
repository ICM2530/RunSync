package com.example.runsyncmockups.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.runsyncmockups.model.ChatViewModel
import com.example.runsyncmockups.model.Message
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private val PrimaryColor = Color(0xFFFF5722)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaChatIndividual(
    navController: NavController,
    friendId: String,
    friendName: String,
    friendEmail: String,
    chatViewModel: ChatViewModel = viewModel()
) {
    val chatState by chatViewModel.chatState.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(friendId) {
        chatViewModel.loadMessages(friendId)
    }

    LaunchedEffect(chatState.currentMessages.size) {
        if (chatState.currentMessages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(chatState.currentMessages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryColor,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                        Column {
                            Text(friendName, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Text(
                                friendEmail,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            ChatInputBar(
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSendMessage = {
                    if (messageText.isNotBlank()) {
                        chatViewModel.sendMessage(
                            friendId = friendId,
                            friendName = friendName,
                            friendEmail = friendEmail,
                            text = messageText
                        )
                        messageText = ""
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                chatState.isLoading && chatState.currentMessages.isEmpty() -> {
                    CircularProgressIndicator(
                        color = PrimaryColor,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                chatState.currentMessages.isEmpty() -> {
                    EmptyMessagesState(
                        friendName = friendName,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(chatState.currentMessages) { message ->
                            MessageBubble(
                                message = message,
                                isCurrentUser = message.senderId == currentUserId
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    isCurrentUser: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isCurrentUser) 18.dp else 6.dp,
                bottomEnd = if (isCurrentUser) 6.dp else 18.dp
            ),
            tonalElevation = 4.dp,
            shadowElevation = 2.dp,
            color = if (isCurrentUser) PrimaryColor else Color(0xFFFFFFFF),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.text,
                    color = if (isCurrentUser) Color.White else Color.Black,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatMessageTime(message.timestamp),
                    fontSize = 11.sp,
                    color = if (isCurrentUser) {
                        Color.White.copy(alpha = 0.7f)
                    } else {
                        Color.Black.copy(alpha = 0.6f)
                    },
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun ChatInputBar(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Surface(
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = onMessageChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe un mensaje...") },
                shape = RoundedCornerShape(20.dp),
                maxLines = 4
            )
            Spacer(modifier = Modifier.width(8.dp))

            FloatingActionButton(
                onClick = onSendMessage,
                modifier = Modifier.size(52.dp),
                containerColor = PrimaryColor
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Enviar",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun EmptyMessagesState(
    friendName: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ChatBubbleOutline,
            contentDescription = null,
            modifier = Modifier.size(70.dp),
            tint = PrimaryColor.copy(alpha = 0.6f)
        )
        Text(
            text = "Inicia la conversación",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "Envía un mensaje a $friendName",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

fun formatMessageTime(timestamp: com.google.firebase.Timestamp): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp.toDate())
}
