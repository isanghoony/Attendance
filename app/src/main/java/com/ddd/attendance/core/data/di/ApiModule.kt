package com.ddd.attendance.core.data.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.ddd.attendance.BuildConfig
import com.ddd.attendance.core.data.AuthorizationInterceptor
import com.ddd.attendance.core.data.api.AccountsApi
import com.ddd.attendance.core.data.api.AttendanceApi
import com.ddd.attendance.core.data.api.InvitesApi
import com.ddd.attendance.core.data.api.ProfilesApi
import com.ddd.attendance.core.data.api.QrCodeApi
import com.ddd.attendance.core.datastore.datasource.AccountPreferencesDataSource
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson().newBuilder().create()
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
    ): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://home.ufxpri.dev")
        .addConverterFactory(gsonConverterFactory)
        .build()


    /**
     * 네트워크 요청 및 응답을 모니터링하고 로깅하는 데 특화된 라이브러리
     *
     * 참고 : [Chucker GitHub Repository](https://github.com/ChuckerTeam/chucker)
     *
     * @author hoon
     */

    @Provides
    @Singleton
    fun provideChuckerInterceptor(
        @ApplicationContext context: Context
    ): ChuckerInterceptor {
        return ChuckerInterceptor.Builder(context)
            .collector(ChuckerCollector(context))
            .maxContentLength(250_000L)
            .redactHeaders(emptySet())
            .alwaysReadResponseBody(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthorizationInterceptor(
        dataSource: AccountPreferencesDataSource
    ): AuthorizationInterceptor {
        return AuthorizationInterceptor(dataSource)
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor()
            .apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        chuckerInterceptor: ChuckerInterceptor,
        authorizationInterceptor: AuthorizationInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authorizationInterceptor)
        .addInterceptor(chuckerInterceptor)
        .addInterceptor(httpLoggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideAccountsApi(retrofit: Retrofit): AccountsApi {
        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideInvitesApi(retrofit: Retrofit): InvitesApi {
        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideAttendanceApi(retrofit: Retrofit): AttendanceApi {
        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideProfilesApi(retrofit: Retrofit): ProfilesApi {
        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideQrCodeApi(retrofit: Retrofit): QrCodeApi {
        return retrofit.create()
    }
}