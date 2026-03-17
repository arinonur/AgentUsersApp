package com.example.AgentUsersApp.feature.users.data

import com.example.AgentUsersApp.feature.users.domain.User
import com.example.AgentUsersApp.feature.users.domain.UserRepository

class UserRepositoryImpl(
    private val api: UserApi
) : UserRepository {
    override suspend fun getUsers(): List<User> {
        return api.getUsers().map { it.toDomain() }
    }
}
