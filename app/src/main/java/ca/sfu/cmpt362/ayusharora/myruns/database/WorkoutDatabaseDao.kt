package ca.sfu.cmpt362.ayusharora.myruns.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Data Access Object
// Code adapted from XD's lecture demos
@Dao
interface WorkoutDatabaseDao {

    @Insert
    suspend fun insertWorkout (workout: ExerciseEntry)

    @Query("SELECT * FROM workout_data")
    fun getAllWorkouts (): Flow<List<ExerciseEntry>>

    @Query ("DELETE FROM workout_data WHERE id = :id")
    suspend fun deleteWorkout(id: Long)

    @Query("DELETE FROM workout_data")
    suspend fun deleteAll()
}