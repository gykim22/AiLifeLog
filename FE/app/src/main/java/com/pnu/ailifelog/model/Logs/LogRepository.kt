package com.pnu.ailifelog.model.Logs

class CreateRecordRepositoryImpl(
    private val api: CreateRecordApi
) : CreateRecordRepository {

    override suspend fun createRecord(request: CreateRequest): Result<CreateResponse> {
        return try {
            val response = api.createRecord(request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("응답 바디 없음"))
            } else {
                Result.failure(Exception("에러 코드 ${response.code()}: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLogs(page: Int?, size: Int?, from: String?, to: String?): Result<LogPageResponse> {
        return try {
            val response = api.getLogs(page, size, from, to)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("응답 바디 없음"))
            } else {
                Result.failure(Exception("에러 코드 ${response.code()}: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteLog(id: Long): Result<Unit> {
        return try {
            val response = api.deleteLog(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("에러 코드 ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun ask(prompt: String): Result<String> {
        return try {
            val response = api.askLLM(AskRequest(prompt))
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it.content) }
                    ?: Result.failure(Exception("응답 바디 없음"))
            } else {
                Result.failure(Exception("에러 코드 ${response.code()}: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserInfo(): Result<UserInfoResponse> {
        return try {
            val response = api.getUserInfo()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("응답 바디 없음"))
            } else {
                Result.failure(Exception("에러 코드 ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(password: String): Result<Unit> {
        return try {
            val response = api.deleteAccount(DeleteAccountRequest(password))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("에러 코드 ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}