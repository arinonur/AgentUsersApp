package com.example.AgentUsersApp.feature.users.data

import com.example.AgentUsersApp.feature.users.domain.User

data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val website: String
)

fun UserDto.toDomain(): User {
    return User(
        id = id,
        name = name,
        email = email,
        phone = phone,
        website = website,
        avatarUrl = "https://i.pravatar.cc/150?img=$id"
    )
}
