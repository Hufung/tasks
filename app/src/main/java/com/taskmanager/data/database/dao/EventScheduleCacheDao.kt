package com.taskmanager.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taskmanager.data.database.entities.EventScheduleCache
import kotlinx.coroutines.flow.Flow

@Dao
interface EventScheduleCacheDao {
    @Query("SELECT * FROM event_schedule_cache WHERE id = 1")
    fun getCache(): Flow<EventScheduleCache?>

    @Query("SELECT * FROM event_schedule_cache WHERE id = 1")
    suspend fun getCacheOnce(): EventScheduleCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(cache: EventScheduleCache)

    @Query("DELETE FROM event_schedule_cache")
    suspend fun clearCache()
}
