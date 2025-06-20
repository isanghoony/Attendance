package com.ddd.attendance.core.network

import com.ddd.attendance.core.model.accounts.Registration
import kotlinx.coroutines.flow.Flow

interface AccountsRepository {
    fun registration(owner: String, email: String, password1: String, password2: String): Flow<Registration>
    fun getAccessToken(): Flow<String>
}