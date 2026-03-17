package com.example.AgentUsersApp.feature.auth.data

import com.example.AgentUsersApp.feature.auth.domain.AuthRepository
import com.example.AgentUsersApp.feature.auth.domain.AuthUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthUser {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user = requireNotNull(result.user) { "Authentication failed." }

        return AuthUser(
            uid = user.uid,
            email = user.email.orEmpty(),
            displayName = user.displayName.orEmpty().ifBlank { user.email.orEmpty() },
        )
    }

    override suspend fun register(email: String, password: String): AuthUser {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = requireNotNull(result.user) { "Registration failed." }

        firestore.collection(USERS_COLLECTION)
            .document(user.uid)
            .set(
                mapOf(
                    "uid" to user.uid,
                    "email" to email.trim(),
                )
            )
            .await()

        return AuthUser(
            uid = user.uid,
            email = user.email.orEmpty(),
            displayName = user.email.orEmpty(),
        )
    }

    private companion object {
        const val USERS_COLLECTION = "users"
    }
}
