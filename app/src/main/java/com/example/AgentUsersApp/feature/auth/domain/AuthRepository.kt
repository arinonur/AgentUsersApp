package com.example.AgentUsersApp.feature.auth.domain

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthUser
    suspend fun register(email: String, password: String): AuthUser
}

data class AuthUser(
    val uid: String,
    val email: String,
    val displayName: String,
)
