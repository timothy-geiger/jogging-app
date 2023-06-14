package de.hdmstuttgart.joggingapp.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import de.hdmstuttgart.joggingapp.Database.Training.Jogging.Jogging;
import de.hdmstuttgart.joggingapp.Database.Training.Jogging.JoggingDao;
import de.hdmstuttgart.joggingapp.Database.Training.Workout.Workout;
import de.hdmstuttgart.joggingapp.Database.Training.Workout.WorkoutDao;

@Database(entities = {Jogging.class, Workout.class}, version = 3)
@TypeConverters({Converter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract JoggingDao joggingDao();
    public abstract WorkoutDao workoutDao();
}
