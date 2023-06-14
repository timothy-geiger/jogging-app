package de.hdmstuttgart.joggingapp.Calendar.BottomSheet.TrainingDetail;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import de.hdmstuttgart.joggingapp.Database.DatabaseHandler;
import de.hdmstuttgart.joggingapp.Database.Training.Jogging.Jogging;
import de.hdmstuttgart.joggingapp.R;

public class JoggingDetailActivity extends AppCompatActivity {

    // Format wie das Datum angezeigt werden soll, wann man gejoggt ist
    private final DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd.MM YYY HH:mm");

    // Anzeigen für Jogging Infos
    private TextView name, date, duration, distance, speed, pace;

    // Button um den Namen zu editieren
    private FloatingActionButton editFab;

    // Edit Text erscheint, wenn man den Namen ändern will
    private EditText editText;

    // Ob man gerade am editieren ist
    private boolean isEdit;

    // Um die Datenbank abfrage zu handeln
    private DatabaseHandler handler;

    // Jogging Objekt, was vom Database Handler zurückgegeben wird
    private Jogging jogging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogging_detail);

        Log.i("Jogging Detail", "Jogging Detail Activity is displayed.");

        /*
            Wenn man aus dem Textfeld rausklickt, beim bearbeiten, soll sich dieses schließen
         */
        View view = findViewById(R.id.name).getRootView();

        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    Log.i("Jogging Detail", "Tastatur weggedrückt.");

                    closeKeyboard();
                    return false;
                }
            });
        }

        handler = new DatabaseHandler(this);

        name = findViewById(R.id.name);
        date = findViewById(R.id.date);
        duration = findViewById(R.id.duration);
        distance = findViewById(R.id.distance);
        speed = findViewById(R.id.speed);
        pace = findViewById(R.id.pace);
        editFab = findViewById(R.id.fab);
        editText = findViewById(R.id.edit);


        // Namen bearbeiten
        editFab.setOnClickListener(v -> {
            isEdit = !isEdit;

            // Anfagen zu bearbeiten
            if(isEdit) {
                Log.i("Jogging Detail", "User fängt an zu bearbeiten.");

                editText.setText(name.getText());
                name.setVisibility(View.GONE);
                editText.setVisibility(View.VISIBLE);
                editFab.setBackgroundTintList(ColorStateList.valueOf(0xFFf7d563));

                // Ende bearbeiten und bearbeitendes Objekt in der Datenbank updaten
            } else {
                Log.i("Jogging Detail", "User hat aufgehört zu bearbeiten.");

                name.setText(editText.getText());
                editText.setVisibility(View.GONE);
                name.setVisibility(View.VISIBLE);
                editFab.setBackgroundTintList(ColorStateList.valueOf(0xFFF7FFF7));

                jogging.setName(name.getText().toString());
                handler.update(jogging);

            }

        });

        long id = getIntent().getLongExtra("id", -1);

        Thread thread = new Thread(() -> {
            jogging = handler.selectJogging(id);
        });

        thread.start();

        try {
            thread.join();

            // Setzt die Anzeigen auf die jeweiligen Werte vom Jogging Objekt
            name.setText(jogging.getName());
            date.setText(sdf.format(jogging.getDate()));
            duration.setText(jogging.getDuration());
            distance.setText(jogging.getDistance() + getString(R.string.DetailKm));
            speed.setText(jogging.getSpeed() + getString(R.string.DetailKmh));
            pace.setText(jogging.getPace() + getString(R.string.DetailMinKm));

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


        Log.i("Jogging Detail", "Keyboard wurde geschlossen.");
    }
}
