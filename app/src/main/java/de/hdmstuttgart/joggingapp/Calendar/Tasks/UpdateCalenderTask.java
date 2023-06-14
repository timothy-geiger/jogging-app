package de.hdmstuttgart.joggingapp.Calendar.Tasks;

import android.os.AsyncTask;

import java.util.ArrayList;

import de.hdmstuttgart.joggingapp.Database.DatabaseHandler;

import static de.hdmstuttgart.joggingapp.ui.main.Fragments.CalenderFragment.adapter;
import static de.hdmstuttgart.joggingapp.ui.main.Fragments.CalenderFragment.firstDate;
import static de.hdmstuttgart.joggingapp.ui.main.Fragments.CalenderFragment.trainings;

public class UpdateCalenderTask extends AsyncTask<Void, Void, ArrayList<Integer>> {

    private DatabaseHandler handler;

    /**
     * Updatet die Marker im Kalender, wenn man z.B. ein Workout im BottomSheet löscht, sollen sich
     * gleichzeitig die Marker updaten
     * @param handler DatabaseHandler
     */
    public UpdateCalenderTask(DatabaseHandler handler) {
        this.handler = handler;

    }

    // Selektiert Alle Tage im Monat an denen ein Workout oder Jogging gemacht wurde
    @Override
    protected ArrayList<Integer> doInBackground(Void... params) {

        return handler.selectTrainingsByMonth(firstDate);
    }

    /**
     * Updatet den Kalender
     * @param result Ergebnis, was bei der Query herraus kam.
     */
    @Override
    protected void onPostExecute(ArrayList<Integer> result) {
        trainings = result;
        adapter.notifyDataSetChanged();

    }
}
