package com.ddd.attendance.core.data.di

import com.ddd.attendance.core.data.api.AccountsApi
import com.ddd.attendance.core.data.api.InvitesApi
import com.ddd.attendance.core.data.repository.DefaultAccountsRepository
import com.ddd.attendance.core.data.repository.DefaultInvitesRepository
import com.ddd.attendance.core.network.AccountsRepository
import com.ddd.attendance.core.network.InvitesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideRegistrationRepository(api: AccountsApi): AccountsRepository = DefaultAccountsRepository(api)

    @Provides
    @Singleton
    fun provideInvitesRepository(api: InvitesApi): InvitesRepository = DefaultInvitesRepository(api)
}