package de.hdmstuttgart.joggingapp.Database.Training.Workout;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.time.LocalDateTime;
import java.util.List;

import de.hdmstuttgart.joggingapp.Database.Training.Training;

@Dao
public interface WorkoutDao {

    @Query("SELECT * FROM workout_table WHERE id = :id")
    Workout getWorkout(long id);

    @Query("SELECT id, name, date FROM workout_table WHERE date BETWEEN :timeStart AND :timeEnd ORDER BY name")
    List<Training> getByDay(LocalDateTime timeStart, LocalDateTime timeEnd);

    @Query("SELECT date FROM workout_table WHERE date BETWEEN :timeStart AND :timeEnd ORDER BY name")
    List<Long> getByMonth(LocalDateTime timeStart, LocalDateTime timeEnd);

    @Insert
    long insert(Workout workout);

    @Query("UPDATE workout_table SET name = :name WHERE id = :id")
    void updateName(String name, long id);

    @Query("DELETE FROM workout_table WHERE id = :id")
    void deleteById(long id);
}
