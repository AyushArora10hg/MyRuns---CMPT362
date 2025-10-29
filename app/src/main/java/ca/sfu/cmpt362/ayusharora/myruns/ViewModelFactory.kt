package ca.sfu.cmpt362.ayusharora.myruns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutRepository
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutViewModel

// Copied from XD's deom on room database
class ViewModelFactory(private val repository: WorkoutRepository) : ViewModelProvider.Factory{
    override fun<T: ViewModel> create (modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(WorkoutViewModel::class.java))
            return WorkoutViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}