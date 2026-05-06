package com.taskmanager.data.repository

import android.content.Context
import com.taskmanager.data.database.dao.EventScheduleCacheDao
import com.taskmanager.data.database.entities.EventScheduleCache
import com.taskmanager.data.network.EventScheduleApiService
import com.taskmanager.data.network.models.EventScheduleResponse
import com.taskmanager.utils.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

class EventScheduleRepository(
    private val apiService: EventScheduleApiService,
    private val cacheDao: EventScheduleCacheDao,
    private val context: Context
) {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    private val cacheDuration = TimeUnit.HOURS.toMillis(24)

    fun getEventSchedule(forceRefresh: Boolean = false): Flow<Result<EventScheduleResponse>> = flow {
        val cache = cacheDao.getCacheOnce()
        val isCacheValid = cache != null && (System.currentTimeMillis() - cache.lastFetched) < cacheDuration

        if (!forceRefresh && isCacheValid && cache != null) {
            try {
                val response = json.decodeFromString<EventScheduleResponse>(cache.jsonData)
                emit(Result.success(response))
                return@flow
            } catch (e: Exception) {
                cacheDao.clearCache()
            }
        }

        if (!NetworkUtils.isNetworkAvailable(context)) {
            if (cache != null) {
                try {
                    val response = json.decodeFromString<EventScheduleResponse>(cache.jsonData)
                    emit(Result.success(response))
                } catch (e: Exception) {
                    emit(Result.failure(Exception("No network and cache is invalid")))
                }
            } else {
                emit(Result.failure(Exception("No network connection")))
            }
            return@flow
        }

        try {
            val response = apiService.getEventSchedule()
            val jsonData = json.encodeToString(response)
            cacheDao.insertCache(
                EventScheduleCache(
                    id = 1,
                    jsonData = jsonData,
                    lastFetched = System.currentTimeMillis()
                )
            )
            emit(Result.success(response))
        } catch (e: Exception) {
            if (cache != null) {
                try {
                    val response = json.decodeFromString<EventScheduleResponse>(cache.jsonData)
                    emit(Result.success(response))
                } catch (decodeError: Exception) {
                    emit(Result.failure(e))
                }
            } else {
                emit(Result.failure(e))
            }
        }
    }

    suspend fun clearCache() {
        cacheDao.clearCache()
    }
}
