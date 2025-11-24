package com.example.runsyncmockups.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Message(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val timestamp: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now(),
    val isRead: Boolean = false
)

data class Conversation(
    val id: String = "",
    val friendId: String = "",
    val friendName: String = "",
    val friendEmail: String = "",
    val friendImageUrl: String? = null,
    val lastMessage: String = "",
    val lastMessageTime: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now(),
    val unreadCount: Int = 0
)

data class ChatState(
    val conversations: List<Conversation> = emptyList(),
    val currentMessages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = ""
)

class ChatViewModel : ViewModel() {
    private val _chatState = MutableStateFlow(ChatState())
    val chatState: StateFlow<ChatState> = _chatState.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private var conversationsListener: ListenerRegistration? = null
    private var messagesListener: ListenerRegistration? = null

    init {
        loadConversations()
    }

    // Cargar todas las conversaciones del usuario
    private fun loadConversations() {
        val currentUserId = auth.currentUser?.uid ?: return

        _chatState.value = _chatState.value.copy(isLoading = true)

        conversationsListener = firestore.collection("conversations")
            .whereArrayContains("participants", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _chatState.value = _chatState.value.copy(
                        isLoading = false,
                        error = "Error al cargar conversaciones"
                    )
                    return@addSnapshotListener
                }

                val conversationsList = mutableListOf<Conversation>()

                snapshot?.documents?.forEach { doc ->
                    val participants = doc.get("participants") as? List<String> ?: return@forEach
                    val friendId = participants.find { it != currentUserId } ?: return@forEach

                    val conversation = Conversation(
                        id = doc.id,
                        friendId = friendId,
                        friendName = doc.getString("friendName") ?: "",
                        friendEmail = doc.getString("friendEmail") ?: "",
                        friendImageUrl = doc.getString("friendImageUrl"),
                        lastMessage = doc.getString("lastMessage") ?: "",
                        lastMessageTime = doc.getTimestamp("lastMessageTime")
                            ?: com.google.firebase.Timestamp.now(),
                        unreadCount = (doc.getLong("unreadCount_$currentUserId") ?: 0).toInt()
                    )
                    conversationsList.add(conversation)
                }

                // Ordenar por última actividad
                conversationsList.sortByDescending { it.lastMessageTime }

                _chatState.value = _chatState.value.copy(
                    conversations = conversationsList,
                    isLoading = false
                )
            }
    }

    // Cargar mensajes de una conversación específica
    fun loadMessages(friendId: String) {
        val currentUserId = auth.currentUser?.uid ?: return

        _chatState.value = _chatState.value.copy(isLoading = true)

        // Generar ID de conversación consistente
        val conversationId = getConversationId(currentUserId, friendId)

        messagesListener?.remove()
        messagesListener = firestore.collection("conversations")
            .document(conversationId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _chatState.value = _chatState.value.copy(
                        isLoading = false,
                        error = "Error al cargar mensajes"
                    )
                    return@addSnapshotListener
                }

                val messagesList = snapshot?.documents?.mapNotNull { doc ->
                    Message(
                        id = doc.id,
                        senderId = doc.getString("senderId") ?: "",
                        receiverId = doc.getString("receiverId") ?: "",
                        text = doc.getString("text") ?: "",
                        timestamp = doc.getTimestamp("timestamp")
                            ?: com.google.firebase.Timestamp.now(),
                        isRead = doc.getBoolean("isRead") ?: false
                    )
                } ?: emptyList()

                _chatState.value = _chatState.value.copy(
                    currentMessages = messagesList,
                    isLoading = false
                )

                // Marcar mensajes como leídos
                markMessagesAsRead(conversationId, friendId)
            }
    }

    // Enviar mensaje
    fun sendMessage(friendId: String, friendName: String, friendEmail: String, text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                val currentUserName = auth.currentUser?.displayName ?: "Usuario"
                val conversationId = getConversationId(currentUserId, friendId)

                // Crear o actualizar conversación
                val conversationData = hashMapOf(
                    "participants" to listOf(currentUserId, friendId),
                    "lastMessage" to text,
                    "lastMessageTime" to com.google.firebase.Timestamp.now(),
                    "friendName" to friendName,
                    "friendEmail" to friendEmail,
                    "unreadCount_$friendId" to com.google.firebase.firestore.FieldValue.increment(1)
                )

                firestore.collection("conversations")
                    .document(conversationId)
                    .set(conversationData, com.google.firebase.firestore.SetOptions.merge())
                    .await()

                // Agregar mensaje
                val messageData = hashMapOf(
                    "senderId" to currentUserId,
                    "receiverId" to friendId,
                    "text" to text,
                    "timestamp" to com.google.firebase.Timestamp.now(),
                    "isRead" to false
                )

                firestore.collection("conversations")
                    .document(conversationId)
                    .collection("messages")
                    .add(messageData)
                    .await()

            } catch (e: Exception) {
                _chatState.value = _chatState.value.copy(
                    error = "Error al enviar mensaje: ${e.message}"
                )
            }
        }
    }

    // Marcar mensajes como leídos
    private fun markMessagesAsRead(conversationId: String, friendId: String) {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch

                // Resetear contador de no leídos
                firestore.collection("conversations")
                    .document(conversationId)
                    .update("unreadCount_$currentUserId", 0)
                    .await()

                // Marcar mensajes individuales como leídos
                val unreadMessages = firestore.collection("conversations")
                    .document(conversationId)
                    .collection("messages")
                    .whereEqualTo("receiverId", currentUserId)
                    .whereEqualTo("isRead", false)
                    .get()
                    .await()

                unreadMessages.documents.forEach { doc ->
                    doc.reference.update("isRead", true)
                }

            } catch (e: Exception) {
                // Error silencioso
            }
        }
    }

    // Generar ID único y consistente para la conversación
    private fun getConversationId(userId1: String, userId2: String): String {
        return if (userId1 < userId2) {
            "${userId1}_${userId2}"
        } else {
            "${userId2}_${userId1}"
        }
    }

    // Obtener total de mensajes no leídos
    fun getTotalUnreadCount(): Int {
        return _chatState.value.conversations.sumOf { it.unreadCount }
    }

    fun clearError() {
        _chatState.value = _chatState.value.copy(error = "")
    }

    override fun onCleared() {
        super.onCleared()
        conversationsListener?.remove()
        messagesListener?.remove()
    }
}