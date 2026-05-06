package com.taskmanager.data.network

import com.taskmanager.data.network.models.EventScheduleResponse
import retrofit2.http.GET

interface EventScheduleApiService {
    @GET("event-schedule")
    suspend fun getEventSchedule(): EventScheduleResponse
}
