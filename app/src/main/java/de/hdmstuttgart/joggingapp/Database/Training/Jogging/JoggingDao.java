package de.hdmstuttgart.joggingapp.Database.Training.Jogging;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.time.LocalDateTime;
import java.util.List;

import de.hdmstuttgart.joggingapp.Database.Training.Training;
import de.hdmstuttgart.joggingapp.Database.Training.Workout.Workout;

@Dao
public interface JoggingDao {

    @Query("SELECT * FROM jogging_table WHERE id = :id")
    Jogging getJogging(long id);

    @Query("SELECT id, name, date FROM jogging_table WHERE date BETWEEN :timeStart AND :timeEnd ORDER BY name")
    List<Training> getByDay(LocalDateTime timeStart, LocalDateTime timeEnd);

    @Query("SELECT date FROM jogging_table WHERE date BETWEEN :timeStart AND :timeEnd ORDER BY name")
    List<Long> getByMonth(LocalDateTime timeStart, LocalDateTime timeEnd);

    @Insert
    long insert(Jogging jogging);

    @Query("UPDATE jogging_table SET name = :name WHERE id = :id")
    void updateName(String name, long id);

    @Query("DELETE FROM jogging_table WHERE id = :id")
    void deleteById(long id);

}
