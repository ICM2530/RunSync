package com.example.runsyncmockups.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChallengeViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    fun sendChallenge(toUser: UserAuthState) {
        val fromId = auth.currentUser?.uid ?: return
        val toId = toUser.id ?: return

        val fromName = auth.currentUser?.displayName ?: "Desconocido"

        val challengeData = mapOf(
            "fromId" to fromId,
            "fromName" to fromName,
            "state" to "pending",
            "createdAt" to System.currentTimeMillis()
        )

        db.getReference("users")
            .child(toId)
            .child("challenge")
            .setValue(challengeData)
    }
}

data class SimpleChallenge(
    val fromId: String = "",
    val fromName: String = "",
    val state: String = "pending"
)

class IncomingChallengeViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    private val _challenge = MutableStateFlow<SimpleChallenge?>(null)
    val challenge: StateFlow<SimpleChallenge?> = _challenge

    private var listener: ValueEventListener? = null

    init {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            // No hay usuario logueado: no inicies el listener
            Log.d("IncomingChallengeVM", "No hay usuario autenticado, no se escuchan retos.")
        }
            else {
            val ref = db.getReference("users").child(uid).child("challenge")

            listener = ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val ch = snapshot.getValue(SimpleChallenge::class.java)
                    // solo nos interesa si está pendiente
                    _challenge.value = if (ch?.state == "pending") ch else null
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    fun answer(accept: Boolean, onAccepted: (SimpleChallenge) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        val ch = _challenge.value ?: return

        val newState = if (accept) "accepted" else "rejected"

        db.getReference("users")
            .child(uid)
            .child("challenge")
            .child("state")
            .setValue(newState)
            .addOnCompleteListener {
                if (accept) onAccepted(ch)
                _challenge.value = null
            }
    }

    override fun onCleared() {
        super.onCleared()
        val uid = auth.currentUser?.uid ?: return
        listener?.let {
            db.getReference("users")
                .child(uid)
                .child("challenge")
                .removeEventListener(it)
        }
    }
}

class OutgoingChallengeViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    data class OpponentChallenge(
        val opponentId: String = "",
        val opponentName: String = ""
    )

    private val _acceptedChallenge = MutableStateFlow<OpponentChallenge?>(null)
    val acceptedChallenge: StateFlow<OpponentChallenge?> = _acceptedChallenge

    private var listener: ValueEventListener? = null

    init {
        val myUid = auth.currentUser?.uid
        if (myUid == null) {
            Log.d("OutgoingChallengeVM", "No hay usuario logueado, no escucho retos.")
        }

        val ref = db.getReference("users")

        listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUid = auth.currentUser?.uid ?: return

                var found: OpponentChallenge? = null

                for (userSnap in snapshot.children) {
                    val opponentId = userSnap.key ?: continue
                    if (opponentId == currentUid) continue // me salto a mí mismo

                    val fromId = userSnap.child("challenge/fromId")
                        .getValue(String::class.java)

                    val state = userSnap.child("challenge/state")
                        .getValue(String::class.java)

                    if (fromId == currentUid && state == "accepted") {
                        val opponentName = userSnap.child("name")
                            .getValue(String::class.java) ?: "Rival"

                        found = OpponentChallenge(
                            opponentId = opponentId,
                            opponentName = opponentName
                        )
                        break
                    }
                }

                _acceptedChallenge.value = found
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OutgoingChallengeVM", "Error: ${error.message}")
            }
        })
    }

    fun consumeChallenge() {
        _acceptedChallenge.value = null
    }

    fun clearOpponentChallenge(opponentId: String) {
        db.getReference("users")
            .child(opponentId)
            .child("challenge")
            .removeValue()
    }

    override fun onCleared() {
        super.onCleared()
        listener?.let {
            db.getReference("users").removeEventListener(it)
        }
    }
}


