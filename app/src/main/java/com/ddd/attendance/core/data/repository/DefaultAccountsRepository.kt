package com.ddd.attendance.core.data.repository

import com.ddd.attendance.core.data.api.AccountsApi
import com.ddd.attendance.core.data.api.model.accounts.RegistrationResponse
import com.ddd.attendance.core.data.api.request.accounts.CheckEmailRequest
import com.ddd.attendance.core.data.api.request.accounts.RegistrationRequest
import com.ddd.attendance.core.data.api.request.accounts.TokenEmailRequest
import com.ddd.attendance.core.datastore.datasource.AccountPreferencesDataSource
import com.ddd.attendance.core.model.accounts.CheckEmail
import com.ddd.attendance.core.model.accounts.Registration
import com.ddd.attendance.core.model.accounts.TokenEmail
import com.ddd.attendance.core.network.AccountsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DefaultAccountsRepository @Inject constructor(
    private val api: AccountsApi,
    private val dataSource: AccountPreferencesDataSource
) : AccountsRepository {
    private val accessToken: Flow<String> = dataSource.accountAccessToken
    private val email: Flow<String> = dataSource.accountEmail

    override fun registration(
        owner: String,
        email: String,
        password1: String,
        password2: String
    ): Flow<Registration> = flow {
        val response = api.registration(
            request = RegistrationRequest(
                owner = owner,
                email =email,
                password1 = password1,
                password2 = password2
            )
        )

        val data = Registration.from(
            response ?:
            RegistrationResponse(
                accessToken = "",
                refreshToken = "",
                user = null
            )
        )

        //이메일, id, accessToken 저장

        data.user?.email?.let {
            dataSource.updateEmail(it)
        }
        data.user?.id?.let {
            dataSource.updateAccountUserId(it)
        }
        dataSource.updateAccountAccessToken(data.accessToken)

        emit(data)
    }

    override fun checkEmail(email: String): Flow<CheckEmail> = flow {
        val response = api.checkEmail(
            request = CheckEmailRequest(
                email = email
            )
        )

        val data = CheckEmail.from(response.data)
        emit(data)
    }

    override fun loginEmail(email: String): Flow<TokenEmail>  = flow {
        val response = api.loginEmail(
            request = TokenEmailRequest(
                email = email
            )
        )

        val data = TokenEmail.from(response.data)

        dataSource.apply {
            updateEmail(data.email)
            updateAccountUserId(data.id)
            updateAccountAccessToken(data.accessToken)
        }

        emit(data)
    }

    override fun getAccessToken(): Flow<String> {
        return accessToken.filter {
            it.isNotBlank()
        }
    }

    override fun getEmail(): Flow<String> {
        return email.filter {
            it.isNotBlank()
        }
    }
}