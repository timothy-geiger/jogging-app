package de.hdmstuttgart.joggingapp.Database;

import android.util.Log;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Converter {

    /**
     * Konvertiert long value in ein LocalDateTime Objekt
     * @param value Date as long Value
     * @return Date as LocalDateTime Object
     */
    @TypeConverter
    public static LocalDateTime toLocalDateTime(Long value) {
        if(value == null) {
            Log.e("Database Converter", "Long value is null");
            return null;

        } else {
            Log.i("Database Converter", "Converts long value: " + value + ", to LocalDateTime.");
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneId.systemDefault());
        }
    }

    /**
     * Konvertiert ein LocalDateTime Objekt in ein long value
     * @param date Date as LocalDateTime Object
     * @return Date as long Value
     */
    @TypeConverter
    public static Long toLong(LocalDateTime date) {
        if(date == null) {
            Log.e("Database Converter", "LocalDateTime Object is null");
            return null;

        } else {
            Log.i("Database Converter", "Converts LocalDateTime Object: " + date.toString() + ", to long value.");
            return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        }
    }
}
