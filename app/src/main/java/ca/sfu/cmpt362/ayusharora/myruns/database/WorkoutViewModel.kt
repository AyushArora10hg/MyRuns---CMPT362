package ca.sfu.cmpt362.ayusharora.myruns.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData

// View Model
// Code adapted from XD's demo lectures
class WorkoutViewModel(private val repository: WorkoutRepository) : ViewModel() {

    val entry = ExerciseEntry(
        id = 0,
        inputType = 0,
        activityType = 0,
        duration = 0.0,
        distance = 0.0,
        avgPace = 0.0,
        avgSpeed = 0.0,
        calorie = 0.0,
        climb = 0.0,
        heartRate = 0.0,
        comment = ""
    )

    val allWorkouts : LiveData<List<ExerciseEntry>> = repository.allWorkouts.asLiveData()

    fun insert(){
        repository.insert(entry)
    }

    fun deleteEntry(id: Long){
        repository.delete(id)
    }
}

class ViewModelFactory(private val repository: WorkoutRepository) : ViewModelProvider.Factory{
    override fun<T: ViewModel> create (modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(WorkoutViewModel::class.java))
            return WorkoutViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}