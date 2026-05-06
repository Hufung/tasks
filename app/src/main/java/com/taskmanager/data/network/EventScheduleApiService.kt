package com.taskmanager.data.network

import com.taskmanager.data.network.models.EventScheduleResponse
import com.taskmanager.data.network.models.TimetableResponse
import retrofit2.http.GET

interface EventScheduleApiService {
    @GET("event-schedule")
    suspend fun getEventSchedule(): EventScheduleResponse

    @GET("timetable")
    suspend fun getTimetable(): Map<String, Map<String, List<Map<String, String>>>>
}
