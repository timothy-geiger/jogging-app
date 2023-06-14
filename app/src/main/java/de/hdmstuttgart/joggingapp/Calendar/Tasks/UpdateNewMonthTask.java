package de.hdmstuttgart.joggingapp.Calendar.Tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import de.hdmstuttgart.joggingapp.Database.DatabaseHandler;

import static de.hdmstuttgart.joggingapp.ui.main.Fragments.CalenderFragment.adapter;
import static de.hdmstuttgart.joggingapp.ui.main.Fragments.CalenderFragment.dates;
import static de.hdmstuttgart.joggingapp.ui.main.Fragments.CalenderFragment.firstDate;
import static de.hdmstuttgart.joggingapp.ui.main.Fragments.CalenderFragment.startDate;
import static de.hdmstuttgart.joggingapp.ui.main.Fragments.CalenderFragment.trainings;

public class UpdateNewMonthTask extends AsyncTask<Void, Void, ArrayList<Integer>> {

    private DatabaseHandler handler;

    /**
     * Updatet den Kalender, wenn man den Monat wechselt oder wenn man die App startet
     * @param context MainActivity Context
     */
    public UpdateNewMonthTask(Context context) {
        this.handler = new DatabaseHandler(context);
    }

    // Erstellt das Dataset für das GridView + füllt dieses mit den passenden Daten
    @Override
    protected ArrayList<Integer> doInBackground(Void... params) {

        // Kalendar soll 7 * 6 einträge haben
        Thread thread = new Thread() {
            @Override
            public void run() {
                for(int counter = 0; counter < 42; counter++) {
                    dates[counter] = startDate.plusDays(counter);
                }

            }
        };

        thread.start();
        ArrayList<Integer> result = handler.selectTrainingsByMonth(firstDate);

        try {
            thread.join();

        } catch (InterruptedException e) {
            e.printStackTrace();

        }

        return result;

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
