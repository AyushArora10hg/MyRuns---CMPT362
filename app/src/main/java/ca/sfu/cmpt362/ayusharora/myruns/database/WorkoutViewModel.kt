package ca.sfu.cmpt362.ayusharora.myruns.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import java.util.Calendar

// View Model
// Code adapted from XD's demo lectures
class WorkoutViewModel(private val repository: WorkoutRepository) : ViewModel() {

    val allWorkouts : LiveData<List<ExerciseEntry>> = repository.allWorkouts.asLiveData()

    fun insert(entry: ExerciseEntry){
        repository.insert(entry)
    }

    fun deleteEntry(id: Long){
        repository.delete(id)
    }
}