package ca.sfu.cmpt362.ayusharora.myruns.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ExerciseEntry::class], version = 1)
abstract class WorkoutDatabase: RoomDatabase() {

    abstract val workoutDatabaseDao : WorkoutDatabaseDao

    companion object{

        @Volatile
        private var INSTANCE: WorkoutDatabase?=null

        fun getInstance(context: Context) : WorkoutDatabase {
            synchronized(this){
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, WorkoutDatabase::class.java, "workout_data").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}