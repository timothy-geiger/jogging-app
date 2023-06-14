package de.hdmstuttgart.joggingapp.Database.Training.Jogging;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import de.hdmstuttgart.joggingapp.Database.Training.Training;

@Entity(tableName = "jogging_table")
public class Jogging {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long id;

    private LocalDateTime date;
    private String name;
    private String duration;
    private String distance;
    private String pace;
    private String speed;

    public Jogging(LocalDateTime date, String name, String duration, String distance, String speed, String pace) {
        this.date = date;
        this.name = name;
        this.duration = duration;
        this.distance = distance;
        this.speed = speed;
        this.pace = pace;

        Log.i("Jogging", "Ein Jogging mit dem Namen " + name + " wurde erstellt.");
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPace() {
        return pace;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

}
