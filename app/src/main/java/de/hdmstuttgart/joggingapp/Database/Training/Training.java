package de.hdmstuttgart.joggingapp.Database.Training;

import android.util.Log;

import androidx.room.Ignore;

import java.time.LocalDateTime;

public class Training {
    private long id;
    private LocalDateTime date;
    private String name;

    @Ignore
    private char type;

    /**
     * Erstellt ein Training
     * @param id id vom Training
     * @param date Wann das Training absolviert wurde
     * @param name wie das Training heißt
     */
    public Training(long id, LocalDateTime date, String name) {
        this.id = id;
        this.date = date;
        this.name = name;

        Log.i("Training", "Ein Training mit dem Namen" + name + " wurde erstellt.");
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

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

}
