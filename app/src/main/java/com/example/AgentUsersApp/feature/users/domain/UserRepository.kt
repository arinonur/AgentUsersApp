package com.example.AgentUsersApp.feature.users.domain

interface UserRepository {
    suspend fun getUsers(): List<User>
}
