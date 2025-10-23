package ca.sfu.cmpt362.ayusharora.myruns.database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkoutRepository (private val workoutDatabaseDao: WorkoutDatabaseDao) {

    val allWorkouts = workoutDatabaseDao.getAllWorkouts()

    fun insert (entry: ExerciseEntry){
        CoroutineScope(Dispatchers.IO).launch {
            workoutDatabaseDao.insertWorkout(entry)
        }
    }

    fun delete (id: Long){
        CoroutineScope(Dispatchers.IO).launch {
            workoutDatabaseDao.deleteWorkout(id)
        }
    }
}