package com.ddd.attendance.core.datastore.datasource

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class DefaultAccountPreferencesDataSource @Inject constructor (
    @Named("account") private val dataStore: DataStore<Preferences>
): AccountPreferencesDataSource {
    object PreferencesKey {
        val ACCOUNT_ACCESS_TOKEN = stringPreferencesKey("ACCOUNT_ACCESS_TOKEN")
        val ACCOUNT_EMAIL = stringPreferencesKey("ACCOUNT_EMAIL")
        val ACCOUNT_INVITE_TYPE = stringPreferencesKey("ACCOUNT_INVITE_TYPE")
        val ACCOUNT_INVITE_CODE_ID = stringPreferencesKey("ACCOUNT_INVITE_CODE_ID")
        val ACCOUNT_USER_ID = intPreferencesKey("ACCOUNT_USER_ID")
    }

    override val accountAccessToken: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.ACCOUNT_ACCESS_TOKEN] ?: ""
    }

    override suspend fun updateAccountAccessToken(accessToken: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.ACCOUNT_ACCESS_TOKEN] = accessToken
            Log.e("Datastore 액세스 토큰", "${preferences[PreferencesKey.ACCOUNT_ACCESS_TOKEN]}")
        }
    }

    override val accountEmail: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.ACCOUNT_EMAIL] ?: ""
    }

    override suspend fun updateEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.ACCOUNT_EMAIL] = email
            Log.e("Datastore 이메일", "${preferences[PreferencesKey.ACCOUNT_EMAIL]}")
        }
    }

    override val accountInviteType: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.ACCOUNT_INVITE_TYPE] ?: ""
    }

    override suspend fun updateAccountInviteType(inviteType: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.ACCOUNT_INVITE_TYPE] = inviteType
            Log.e("Datastore 초대 타입", "${preferences[PreferencesKey.ACCOUNT_INVITE_TYPE]}")
        }
    }

    override val accountUserId: Flow<Int> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.ACCOUNT_USER_ID] ?: - 1
    }

    override suspend fun updateAccountUserId(userId: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.ACCOUNT_USER_ID] = userId
            Log.e("Datastore 유저 아이디", "${preferences[PreferencesKey.ACCOUNT_USER_ID]}")
        }
    }

    override val accountInviteCodeId: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKey.ACCOUNT_INVITE_CODE_ID] ?: ""
    }

    override suspend fun updateAccountInviteCodeId(inviteCodeId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.ACCOUNT_INVITE_CODE_ID] = inviteCodeId
            Log.e("Datastore 유저 초대 코드 ID", "${preferences[PreferencesKey.ACCOUNT_INVITE_CODE_ID]}")
        }
    }
}