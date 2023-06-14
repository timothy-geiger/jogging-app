package de.hdmstuttgart.joggingapp.Database;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdmstuttgart.joggingapp.Calendar.Tasks.UpdateCalenderTask;
import de.hdmstuttgart.joggingapp.Database.Training.Jogging.Jogging;
import de.hdmstuttgart.joggingapp.Database.Training.Training;
import de.hdmstuttgart.joggingapp.Database.Training.Workout.Workout;

public class DatabaseHandler {

    private AppDatabase db;

    public DatabaseHandler(Context context) {
        this.db = Room.databaseBuilder(context, AppDatabase.class, "trainingDb").build();
    }

    class ComplexLong {
        long value;

        void setValue(long value) {
            this.value = value;
        }

        long getValue() {
            return this.value;
        }
    }

    /**
     *
     * @param obj Should be from type Jogging or Workout
     * @return returns autogenerated id from inserted Object
     */
    public long insertFirst(final  Object obj) {
        final ComplexLong id = new ComplexLong();

        Thread thread = new Thread(() -> {
            if (obj instanceof Jogging) {
                Jogging jogging = (Jogging) obj;
                long tmp_id = db.joggingDao().insert(jogging);
                id.setValue(tmp_id);

                Log.i("Database", "Inserted " + jogging.getName() + " in trainingDB.");

            } else {
                Workout workout = (Workout) obj;
                long tmp_id = db.workoutDao().insert(workout);
                id.setValue(tmp_id);

                Log.i("Database", "Inserted " + workout.getName() + " in trainingDB.");

            }

            new UpdateCalenderTask(this).execute();
        });

        thread.start();

        try {
            thread.join();

        } catch (InterruptedException e) {
            e.printStackTrace();

        }

        return id.getValue();
    }

    /**
     * Insert Workout or Jogging into database
     * @param obj Should be from type Jogging or Workout
     */
    public void insert(final Object obj) {
        new Thread(() -> {
            if (obj instanceof Jogging) {
                Jogging jogging = (Jogging) obj;
                db.joggingDao().insert(jogging);

                Log.i("Database", "Inserted " + jogging.getName() + " in trainingDB.");

            } else {
                Workout workout = (Workout) obj;
                db.workoutDao().insert(workout);

                Log.i("Database", "Inserted " + workout.getName() + " in trainingDB.");

            }

            new UpdateCalenderTask(this).execute();
        }).start();
    }


    /**
     * Updates Workout or Jogging in database.
     * @param obj Should be from type Jogging or Workout.
     */
    public void update(final Object obj) {
        new Thread(() -> {

            if (obj instanceof Jogging) {
                Jogging jogging = (Jogging) obj;
                db.joggingDao().updateName(jogging.getName(), jogging.getId());
                Log.i("Database ID ----", "" + jogging.getId());
                Log.i("Database", "Updated " + jogging.getName() + " in JoggingDB.");

            } else {
                Workout workout = (Workout) obj;
                db.workoutDao().updateName(workout.getName(), workout.getId());

                Log.i("Database", "Updated " + workout.getName() + " in WorkoutDB.");

            }
        }).start();
    }

    /**
     * Deletes Workout or Jogging in database.
     * @param training Training Object.
     */
    public void delete(final Training training) {
        new Thread(() -> {

            if(training.getType() == 'j') {
                db.joggingDao().deleteById(training.getId());

            } else {
                db.workoutDao().deleteById(training.getId());

            }

            Log.i("Database", "Deleted " + training.getName() + " in trainingDB.");
            new UpdateCalenderTask(this).execute();
        }).start();
    }

    public Jogging selectJogging(long id) {
        Log.i("Database", "Select Jogging with id " + id + ".");
        return db.joggingDao().getJogging(id);
    }

    public Workout selectWorkout(long id) {
        Log.i("Database", "Select Workout with id " + id + ".");
        return db.workoutDao().getWorkout(id);
    }

    /**
     * Selects all Trainings from one date (date, name, type)
     * @param date The Date of the day.
     * @return Returns Training Object with 'name', 'date' and 'type'.
     */
    public ArrayList<Training> selectTrainingsByDay(LocalDate date) {
        // Start vom Tag
        final LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIDNIGHT);

        // Ende vom Tag
        final LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);

        ArrayList<Training> result = new ArrayList<>();

        // Selects date, name
        List<Training> listJogging = db.joggingDao().getByDay(startOfDay, endOfDay);
        List<Training> listWorkout = db.workoutDao().getByDay(startOfDay, endOfDay);

        // sets type of Training to display it in Recycler View
        listJogging
                .forEach(t -> t.setType('j'));

        Log.i("List", "Jogging List updated from Select Query.");

        listWorkout
                .forEach(t -> t.setType('w'));

        Log.i("List", "Workout List updated from Select Query.");

        result.addAll(listJogging);
        result.addAll(listWorkout);

        result.sort((t1, t2) -> t1.getDate().compareTo(t2.getDate()));

        return result;
    }

    /**
     * Selects all Trainings from one Month, to display Markers on the Calender.
     * Jogging = dayOfMonth, Workout = dayOfMonth * 32
     * @param date The Date
     * @return Returns a Integer ArrayList
     */
    public ArrayList<Integer> selectTrainingsByMonth(LocalDate date) {
        // Start des Monats
        LocalDate startDate = date.withDayOfMonth(1);
        final LocalDateTime startOfMonth = LocalDateTime.of(startDate, LocalTime.MIDNIGHT);

        // Ende des Monats
        LocalDate endDate = date.withDayOfMonth(date.lengthOfMonth());
        final LocalDateTime endOfMonth = LocalDateTime.of(endDate, LocalTime.MAX);

        ArrayList<Integer> list = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("dd");

        // Jogging
        List<Long> listJogging = db.joggingDao().getByMonth(startOfMonth, endOfMonth);
        Log.i("Database", "Select Trainings by Month (Jogging Table).");

        listJogging
                .forEach(t -> list.add(Integer.parseInt(dateFormat.format(new Date(t)))));
        Log.i("List", "Edit Jogging List from Select.");

        // Workout
        List<Long> listWorkout = db.workoutDao().getByMonth(startOfMonth, endOfMonth);
        Log.i("Database", "Select Trainings by Month (Workout Table).");

        listWorkout
                .forEach(w -> list.add(Integer.parseInt(dateFormat.format(new Date(w))) + 31));
        Log.i("List", "Edit Workout List from Select.");

        return list;
    }
}