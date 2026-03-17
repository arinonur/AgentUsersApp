package com.example.AgentUsersApp.network

import com.example.AgentUsersApp.feature.users.data.UserApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val USERS_BASE_URL = "https://jsonplaceholder.typicode.com/"

    private val usersRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(USERS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val userApi: UserApi by lazy {
        usersRetrofit.create(UserApi::class.java)
    }
}
