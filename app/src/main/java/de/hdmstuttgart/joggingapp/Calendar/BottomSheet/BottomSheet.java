package de.hdmstuttgart.joggingapp.Calendar.BottomSheet;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

import de.hdmstuttgart.joggingapp.Calendar.Tasks.SelectTrainingsByDayTask;
import de.hdmstuttgart.joggingapp.Database.DatabaseHandler;
import de.hdmstuttgart.joggingapp.Database.Training.Jogging.Jogging;
import de.hdmstuttgart.joggingapp.Database.Training.Training;
import de.hdmstuttgart.joggingapp.Database.Training.Workout.Workout;
import de.hdmstuttgart.joggingapp.R;

public class BottomSheet extends BottomSheetDialogFragment {

    // Datenbankhandler
    private DatabaseHandler handler;

    // Der Tag, den man im Kalender angeklickt hat
    private LocalDate date;

    private Context context;

    private CoordinatorLayout coordinatorLayout;

    // AsyncTask um alle Training aus der Datenbank zu bekommen vom aktuellem Tag
    private static SelectTrainingsByDayTask trainingDayTask;

    // ArrayList, in der die Trainings gespeichert werden, von der Datenbank abfrage
    public static ArrayList<Training> dayTrainings = new ArrayList<>();

    // RecyclerView, in dem die Trainings aufgelistet werden sollen
    private static RecyclerView recyclerView;


    // Adapter für RecyclerView
    private static BottomSheetAdapter adapter;

    /**
     * Konstruktor für das BottomSheet
     * @param context Context von MainActivity
     * @param localDate Der Tag, den man im Kalender angeklickt hat
     */
    public BottomSheet(Context context, LocalDate localDate) {
        this.context = context;
        handler = new DatabaseHandler(context);
        date = localDate;

    }

    // Das BottomSheet soll runde Ecken haben. Daher muss man den eigenen Style benutzen
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new BottomSheetDialog(context, R.style.BottomSheetTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);

        coordinatorLayout = view.findViewById(R.id.rootView);

        // RecyclerView
        recyclerView = view.findViewById(R.id.trainingRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);

        // RecyclerView Adapter
        adapter = new BottomSheetAdapter();
        recyclerView.setAdapter(adapter);

        // Wenn man nach links swiped soll der Eintrag gelöscht werden
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder item, int direction) {
                int position = item.getAdapterPosition();

                Training deletedTraining = dayTrainings.get(position);

                UndoTask undoTask = new UndoTask(deletedTraining, position);
                undoTask.execute();
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    /**
     * Selektiert Trainings vom Tag (erneut)
     */
    public static void updateData() {
        trainingDayTask.execute();
    }

    /**
     * Selektiert die Trainingseinheiten im onResume(), da die Daten geupdatet werden müssen,
     * wenn man den Namen in der Detailansicht ändert und zurück kommt.
     */
    @Override
    public void onResume() {
        super.onResume();

        trainingDayTask = new SelectTrainingsByDayTask(date, handler, adapter);
        updateData();

        Log.i("BottomSheet", "Daten wurden geupdatet");
    }


    public class UndoTask extends AsyncTask<Void, Void, Void> {

        Training training;

        Jogging j;
        Workout w;

        int position;

        UndoTask(Training training, int position) {
            this.training = training;
            this.position = position;

        }

        @Override
        protected Void doInBackground(Void... voids) {

            // Training vom Typ Workout
            if(training.getType() == 'w') {

                // Workout selektieren
                w = handler.selectWorkout(training.getId());
                Workout workout = new Workout(w.getDate(), w.getName(), w.getRepetitions());
                workout.setId(w.getId());

                // Wenn das BottomSheet sich schließt, muss der Text in der Activity angezeigt werden
                if(dayTrainings.size() == 1) {
                    Snackbar.make(getActivity().findViewById(R.id.calender), workout.getName(), Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    handler.insert(workout);

                                    Training newTraining = new Training(workout.getId(), workout.getDate(), workout.getName());
                                    newTraining.setType('w');

                                    dayTrainings.add(newTraining);
                                    dayTrainings.sort((t1, t2) -> t1.getDate().compareTo(t2.getDate()));
                                    adapter.notifyDataSetChanged();
                                }
                            }).show();

                // Training nicht letztes Element in Recycler View
                } else {
                    Snackbar.make(coordinatorLayout, workout.getName(), Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    handler.insert(workout);

                                    Training newTraining = new Training(workout.getId(), workout.getDate(), workout.getName());
                                    newTraining.setType('w');

                                    dayTrainings.add(newTraining);
                                    dayTrainings.sort((t1, t2) -> t1.getDate().compareTo(t2.getDate()));
                                    adapter.notifyDataSetChanged();
                                }
                            }).show();

                }

            } else {

                // Jogging selektieren
                j = handler.selectJogging(training.getId());
                Jogging jogging = new Jogging(j.getDate(), j.getName(), j.getDuration(), j.getDistance(), j.getSpeed(), j.getPace());
                jogging.setId(j.getId());

                // Wenn das BottomSheet sich schließt, muss der Text in der Activity angezeigt werden
                if(dayTrainings.size() == 1) {
                    Snackbar.make(getActivity().findViewById(R.id.calender), jogging.getName(), Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    handler.insert(jogging);

                                    Training newTraining = new Training(jogging.getId(), jogging.getDate(), jogging.getName());
                                    newTraining.setType('j');

                                    dayTrainings.add(newTraining);
                                    dayTrainings.sort((t1, t2) -> t1.getDate().compareTo(t2.getDate()));
                                    adapter.notifyDataSetChanged();
                                }
                            }).show();

                    // Training nicht letztes Element in Recycler View
                } else {
                    Snackbar.make(coordinatorLayout, jogging.getName(), Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    handler.insert(jogging);

                                    Training newTraining = new Training(jogging.getId(), jogging.getDate(), jogging.getName());
                                    newTraining.setType('j');

                                    dayTrainings.add(newTraining);
                                    dayTrainings.sort((t1, t2) -> t1.getDate().compareTo(t2.getDate()));
                                    adapter.notifyDataSetChanged();
                                }
                            }).show();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // löscht in der Datenbank
            handler.delete(dayTrainings.get(position));

            // löscht im lokalem Array, damit man nicht erneut eine
            // select Anfrage schicken muss
            dayTrainings.remove(position);

            // Updatet das Adapter
            adapter.notifyDataSetChanged();

            // schließt das BottomSheet, wenn keine Trainingseinheiten
            // mehr gespeichert sind
            if(dayTrainings.size() == 0) {
                dismiss();

            }

            Log.i("BottomSheet", "Training wurde gelöscht");
        }
    }
}
