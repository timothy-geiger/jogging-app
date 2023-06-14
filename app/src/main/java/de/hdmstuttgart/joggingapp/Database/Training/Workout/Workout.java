package de.hdmstuttgart.joggingapp.Database.Training.Workout;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity(tableName = "workout_table")
public class Workout {
    @NonNull
    @PrimaryKey(autoGenerate = true)

    private long id;
    private LocalDateTime date;
    private String name;
    private String repetitions;

    public Workout(LocalDateTime date, String name, String repetitions) {
        this.date = date;
        this.name = name;
        this.repetitions = repetitions;

        Log.i("Workout", "Ein Workout mit dem Namen " + name + " wurde erstellt.");
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(String repetitions) {
        this.repetitions = repetitions;
    }
}
