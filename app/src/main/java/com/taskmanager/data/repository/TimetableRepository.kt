package com.taskmanager.data.repository

import android.content.Context
import com.taskmanager.data.network.EventScheduleApiService
import com.taskmanager.data.network.models.Period
import com.taskmanager.data.network.models.TimetableResponse
import com.taskmanager.utils.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TimetableRepository(
    private val apiService: EventScheduleApiService,
    private val context: Context
) {
    fun getTimetable(): Flow<Result<TimetableResponse>> = flow {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            emit(Result.failure(Exception("No network connection")))
            return@flow
        }

        try {
            val rawResponse = apiService.getTimetable()
            val classes = rawResponse.mapValues { (_, dayMap) ->
                val days = dayMap.mapValues { (_, periods) ->
                    periods.map { periodMap ->
                        Period(
                            subject = periodMap["subject"] ?: "",
                            venue = periodMap["venue"] ?: ""
                        )
                    }
                }
                com.taskmanager.data.network.models.ClassTimetable(days = days)
            }
            val response = TimetableResponse(classes = classes)
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
