package de.hdmstuttgart.joggingapp.Calendar.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.time.LocalDate;
import java.util.ArrayList;

import de.hdmstuttgart.joggingapp.Calendar.BottomSheet.BottomSheetAdapter;
import de.hdmstuttgart.joggingapp.Database.DatabaseHandler;
import de.hdmstuttgart.joggingapp.Database.Training.Training;

import static de.hdmstuttgart.joggingapp.Calendar.BottomSheet.BottomSheet.dayTrainings;

public class SelectTrainingsByDayTask extends AsyncTask<Void, Void, ArrayList<Training>> {

    // Das Datum, wo nach Trainingseinheiten gesucht werden soll
    private LocalDate date;

    private DatabaseHandler handler;

    // Adapter vom RecyclerView vom BottomSheet
    private BottomSheetAdapter adapter;

    /**
     * Selektiert alle Trainingseinheiten für einen bestimmten Tag
     * @param date Das Datum, an dem nach Trainingseinheiten gesucht werden soll
     * @param handler Databasehandler
     * @param adapter Adapter vom BottomSheet / RecyclerView
     */
    public SelectTrainingsByDayTask(LocalDate date, DatabaseHandler handler, BottomSheetAdapter adapter) {
        this.date = date;
        this.handler = handler;
        this.adapter = adapter;
    }

    // Selektiert alle Trainingseinheiten für den Tag
    @Override
    protected ArrayList<Training> doInBackground(Void... voids) {
        return handler.selectTrainingsByDay(date);

    }

    /**
     * Updatet RecyclerView
     * @param result Ergebnis, was bei der Query herraus kam.
     */
    @Override
    protected void onPostExecute(ArrayList<Training> result) {
        dayTrainings = result;
        adapter.notifyDataSetChanged();

    }
}
