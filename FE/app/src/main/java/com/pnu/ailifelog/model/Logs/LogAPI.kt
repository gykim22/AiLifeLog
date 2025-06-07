package com.pnu.ailifelog.model.Logs

import android.content.Context
import com.pnu.ailifelog.model.SignUp.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import javax.inject.Named
import javax.inject.Singleton

interface CreateRecordApi {
    @POST("/api/v2/logs")
    suspend fun createRecord(@Body request: CreateRequest): Response<CreateResponse>

    @GET("/api/v2/logs")
    suspend fun getLogs(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null
    ): Response<LogPageResponse>

    @DELETE("/api/v2/logs/{id}")
    suspend fun deleteLog(@retrofit2.http.Path("id") id: Long): Response<Unit>


    @POST("/api/v2/llms/ask")
    suspend fun askLLM(
        @Body request: AskRequest
    ): Response<AskResponse>


    @GET("/api/v2/users/self")
    suspend fun getUserInfo(): Response<UserInfoResponse>

    @DELETE("/api/v2/users/self")
    suspend fun deleteAccount(@Body request: DeleteAccountRequest): Response<Unit>
}

interface CreateRecordRepository {
    suspend fun createRecord(request: CreateRequest): Result<CreateResponse>
    suspend fun getLogs(page: Int?, size: Int?, from: String?, to: String?): Result<LogPageResponse>
    suspend fun ask(prompt: String): Result<String>
    suspend fun deleteLog(id: Long): Result<Unit>
    suspend fun getUserInfo(): Result<UserInfoResponse>
    suspend fun deleteAccount(password: String): Result<Unit>
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context)) // AccessToken 자동 주입
            .build()
    }

    @Provides
    @Singleton
    @Named("SearchRetrofit")
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://swallow104.gonetis.com:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    fun provideCreateRecordApi(@Named("SearchRetrofit") retrofit: Retrofit): CreateRecordApi {
        return retrofit.create(CreateRecordApi::class.java)
    }

    @Provides
    fun provideCreateRecordRepository(api: CreateRecordApi): CreateRecordRepository {
        return CreateRecordRepositoryImpl(api)
    }
}