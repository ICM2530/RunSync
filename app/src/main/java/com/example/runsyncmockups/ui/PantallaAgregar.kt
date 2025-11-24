package com.example.runsyncmockups.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.example.runsyncmockups.model.FriendsViewModel

@Composable
fun AgregarAmigoDialog(
    onDismiss: () -> Unit,
    friendsViewModel: FriendsViewModel
) {
    var email by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var userFound by remember { mutableStateOf<com.example.runsyncmockups.model.Friend?>(null) }
    val friendsState by friendsViewModel.friendsState.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Agregar amigo"
                )
                Text("Agregar Amigo")
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Busca a tu amigo por su correo electrónico:")

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        userFound = null
                        friendsViewModel.clearSearchError()
                    },
                    label = { Text("Email del amigo") },
                    placeholder = { Text("ejemplo@email.com") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    isError = friendsState.searchError.isNotEmpty()
                )

                if (friendsState.searchError.isNotEmpty()) {
                    Text(
                        text = friendsState.searchError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (userFound != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Usuario encontrado:",
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = userFound!!.nombre,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = userFound!!.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        },
        confirmButton = {
            if (userFound != null) {
                Button(
                    onClick = {
                        userFound?.let { friendsViewModel.addFriend(it) }
                        onDismiss()
                    }
                ) {
                    Text("Agregar")
                }
            } else {
                Button(
                    onClick = {
                        isSearching = true
                        friendsViewModel.searchUserByEmail(email) { result ->
                            isSearching = false
                            userFound = result
                        }
                    },
                    enabled = email.isNotBlank() && !isSearching
                ) {
                    Text("Buscar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}