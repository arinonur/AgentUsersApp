package com.example.AgentUsersApp.feature.users.domain

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val website: String,
    val avatarUrl: String
)
