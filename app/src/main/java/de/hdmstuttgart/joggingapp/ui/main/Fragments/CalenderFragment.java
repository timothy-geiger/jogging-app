package de.hdmstuttgart.joggingapp.ui.main.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import de.hdmstuttgart.joggingapp.Calendar.BottomSheet.BottomSheet;
import de.hdmstuttgart.joggingapp.Calendar.CalendarAdapter;
import de.hdmstuttgart.joggingapp.Calendar.Tasks.UpdateNewMonthTask;
import de.hdmstuttgart.joggingapp.R;

public class CalenderFragment extends Fragment {
    public static CalendarAdapter adapter;

    // Data for Calendar
    public static ArrayList<Integer> trainings = new ArrayList<>();
    public static LocalDate[] dates = new LocalDate[42];
    public static LocalDate firstDate;
    public static LocalDate lastDayOfMonth;
    public static LocalDate startDate;
    public static int dayOfWeekInt;

    private Context context;

    private GridView calendar;

    // UI
    private TextView infoCalenderMonth, infoCalenderYear;

    public CalenderFragment() {

    }

    public static CalenderFragment newInstance() {
        return new CalenderFragment();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calender, container, false);

        setupUI(rootView);
        setupCalendar(rootView);
        updateMonth();

        return rootView;
    }

    /**
     * Setzt das UI auf + definiert onClickListener
     * @param v RootView from Activity
     */
    private void setupUI(View v) {
        infoCalenderMonth = v.findViewById(R.id.info_calendar_month);
        infoCalenderYear = v.findViewById(R.id.info_calendar_year);

        // Wechsel auf den letzten Monat
        v.findViewById(R.id.left).setOnClickListener(v1 -> {
            firstDate = firstDate.minusMonths(1);

            // Kalender updaten
            updateMonth();
            Log.i("Month", "Month Changed to previous Month.");
        });

        // Wechsel auf den neusten Monat
        v.findViewById(R.id.right).setOnClickListener(v12 -> {
            firstDate = firstDate.plusMonths(1);

            // Kalender updaten
            updateMonth();
            Log.i("Month", "Month Changed to next Month.");
        });

        Log.i("CalenderUI", "Calender UI is set up.");
    }

    /**
     * Setzt den Kalender auf
     * @param view View
     */
    private void setupCalendar(View view) {
        context = getContext();

        calendar = view.findViewById(R.id.calender_days);
        calendar.setColumnWidth(GridView.AUTO_FIT);
        calendar.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

        calendar.setOnItemClickListener((parent, view1, position, id) -> {
            if(view1.findViewById(R.id.workoutMarker).getVisibility() == View.VISIBLE || view1.findViewById(R.id.joggingMarker).getVisibility() == View.VISIBLE ) {
                BottomSheet bottomSheet = new BottomSheet(getContext(), dates[position]);
                bottomSheet.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "dayTrainingInfo");
            }
        });

        // aktueller Tag
        LocalDate today = LocalDate.now();

        // erster Tag des aktuellen Monats
        firstDate = today.withDayOfMonth(1);

        adapter = new CalendarAdapter(Objects.requireNonNull(getContext()));
        calendar.setAdapter(adapter);
        Log.i("Calender", "Calender is set up.");
    }

    /**
     * Monat updaten, wenn man den Monat wechselt + wenn der Kaleder erstellt wird
     */
    private void updateMonth() {
        // Setzt die Info Texte, bei welchem Monat/Jahr man sich gerade befindet
        infoCalenderMonth.setText(firstDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));

        infoCalenderYear.setText(firstDate.getYear() + getString(R.string.space));

        // Selektiert alle Trainingseinheiten (Workout + Training) für den jeweiligen Monat aus den
        // jeweiligen Datenbanken und speichert diese in ArrayList. Wenn man z.B. am 14. joggen
        // war, beinhaltet die Liste eine 14. Wenn am 14 aber noch ein Workout gemacht hat,
        // beinhaltet die Liste eine 448. Beim Workout wird Monatstag * 32 gerechnet.

        // erster Tag des Monats (Montag = 0, Dienstag = 1, ...). Da der übergebene Tag schon der
        // 1. ist, muss man jetzt noch wissen welcher Wochentag das ist.
        DayOfWeek firstDayOfWeek = firstDate.getDayOfWeek();
        dayOfWeekInt = firstDayOfWeek.getValue() - 1;

        // Kalender soll immer mit einem Montag anfangen, auch wenn dieser nicht zum Monat gehört
        startDate = firstDate.minusDays(dayOfWeekInt);
        lastDayOfMonth = firstDate.withDayOfMonth(firstDate.lengthOfMonth());

        new UpdateNewMonthTask(context).execute();

    }
}
