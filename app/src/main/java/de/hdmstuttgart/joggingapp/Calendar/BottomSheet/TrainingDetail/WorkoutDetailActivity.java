package de.hdmstuttgart.joggingapp.Calendar.BottomSheet.TrainingDetail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import de.hdmstuttgart.joggingapp.Calendar.BottomSheet.BottomSheet;
import de.hdmstuttgart.joggingapp.Database.DatabaseHandler;
import de.hdmstuttgart.joggingapp.Database.Training.Jogging.Jogging;
import de.hdmstuttgart.joggingapp.Database.Training.Workout.Workout;
import de.hdmstuttgart.joggingapp.R;

public class WorkoutDetailActivity extends AppCompatActivity {

    // Format wie das Datum angezeigt werden soll, wann man ein Workout gemacht hat
    private final DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd.MM YYY HH:mm");

    // Anzeigen für Workout Infos
    private TextView name, date, repetitions;

    // Button um den Namen zu editieren
    private FloatingActionButton editFab;

    // Edit Text erscheint, wenn man den Namen ändern will
    private EditText editText;

    // Ob man gerade am editieren ist
    private boolean isEdit;

    // Um die Datenbank abfrage zu handeln
    private DatabaseHandler handler;

    // Jogging Objekt, was vom Database Handler zurückgegeben wird
    private Workout workout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);

        Log.i("Workout Detail", "Jogging Detail Activity is displayed.");

        /*
            Wenn man aus dem Textfeld rausklickt, beim bearbeiten, soll sich dieses schließen
        */
        View view = findViewById(R.id.name).getRootView();

        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    Log.i("Workout Detail", "Tastatur weggedrückt.");

                    closeKeyboard();
                    return false;
                }
            });
        }

        handler = new DatabaseHandler(this);

        name = findViewById(R.id.name);
        date = findViewById(R.id.date);
        repetitions = findViewById(R.id.repetitions);
        editFab = findViewById(R.id.fab);
        editText = findViewById(R.id.edit);

        // Namen bearbeiten
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdit = !isEdit;

                // Anfagen zu bearbeiten
                if(isEdit) {
                    Log.i("Workout Detail", "User fängt an zu bearbeiten.");

                    editText.setText(name.getText());
                    name.setVisibility(View.GONE);
                    editText.setVisibility(View.VISIBLE);
                    editFab.setBackgroundTintList(ColorStateList.valueOf(0xFFf7d563));

                // Ende bearbeiten und bearbeitendes Objekt in der Datenbank updaten
                } else {
                    Log.i("Workout Detail", "User hat aufgehört zu bearbeiten.");

                    name.setText(editText.getText());
                    editText.setVisibility(View.GONE);
                    name.setVisibility(View.VISIBLE);
                    editFab.setBackgroundTintList(ColorStateList.valueOf(0xFFF7FFF7));

                    workout.setName(name.getText().toString());
                    handler.update(workout);

                }

            }
        });

        long id = getIntent().getLongExtra("id", -1);

        Thread thread = new Thread(() -> {
            workout = handler.selectWorkout(id);
        });

        thread.start();
        try {
            thread.join();

            // Setzt die Anzeigen auf die jeweiligen Werte vom Workout Objekt
            name.setText(workout.getName());
            date.setText(sdf.format(workout.getDate()));
            repetitions.setText(workout.getRepetitions() + getString(R.string.space));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Methode um das Keyboard zu schließen
     */
    private void closeKeyboard() {
        View view = getCurrentFocus();

        if(view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        Log.i("Workout Detail", "Keyboard wurde geschlossen.");
    }
}
