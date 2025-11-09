package ca.sfu.cmpt362.ayusharora.myruns.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
// View Model
// Code adapted from XD's demo lectures
class WorkoutViewModel(private val repository: WorkoutRepository) : ViewModel() {

    val entry: ExerciseEntry = ExerciseEntry()

    val allWorkouts : LiveData<List<ExerciseEntry>> = repository.allWorkouts.asLiveData()

    fun insert(){
        repository.insert(entry)
    }

    fun deleteEntry(id: Long){
        repository.delete(id)
    }

    fun deleteAll(){
        val workouts = allWorkouts.value
        if (workouts != null && workouts.isNotEmpty())
            repository.deleteAll()
    }

}