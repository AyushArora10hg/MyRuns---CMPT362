package ca.sfu.cmpt362.ayusharora.myruns.manualinput

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ca.sfu.cmpt362.ayusharora.myruns.database.ExerciseEntry
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutRepository

class ManualInputViewModel(private val repository: WorkoutRepository) : ViewModel() {

    val entry = ExerciseEntry(id = 0,
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

    fun insert(){
        repository.insert(entry)
    }
}

class ViewModelFactory(private val repository: WorkoutRepository) : ViewModelProvider.Factory{
    override fun<T: ViewModel> create (modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(ManualInputViewModel::class.java))
            return ManualInputViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}