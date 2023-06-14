package de.hdmstuttgart.joggingapp.ui.main.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import de.hdmstuttgart.joggingapp.Calendar.BottomSheet.TrainingDetail.JoggingDetailActivity;
import de.hdmstuttgart.joggingapp.Calendar.BottomSheet.TrainingDetail.WorkoutDetailActivity;
import de.hdmstuttgart.joggingapp.Database.DatabaseHandler;
import de.hdmstuttgart.joggingapp.Database.Training.Jogging.Jogging;
import de.hdmstuttgart.joggingapp.Database.Training.Workout.Workout;
import de.hdmstuttgart.joggingapp.Locate.MyLocationListener;
import de.hdmstuttgart.joggingapp.R;

public class WorkoutFragment extends Fragment {

    private Context mContext;
    private DatabaseHandler handler;
    private LocationManager locationManager;
    private MyLocationListener locationListener;

    private Button startBtn, stopBtn;

    final double earth_radius = 6371000;

    //Creating the fitting Decimal-Format.
    private DecimalFormat f = new DecimalFormat("##.00");

    //Stores Location Data during the workout.
    public static List<double[]> positions = new ArrayList<>();
    private double pace, averageSpeed;
    private boolean isTracking = false;
    private Instant start, end;
    private LocalDateTime workoutDate;
    private EditText et_timePicker;

    Calendar date;


    public WorkoutFragment() {
        // Required empty public constructor
    }

    public static WorkoutFragment newInstance() {

        return new WorkoutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new DatabaseHandler(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_workout, container, false);

        setupUI(rootView);

        return rootView;
    }

    /**
     * Turns a Duration value into a prettier format.
     *
     * @param duration duration of the workout
     * @return A version of the String that's readable/ prettier
     */
    public static String humanReadableFormat(Duration duration) {
        return duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }

    /**
     * Starts the tracking. An Instant is generated to calculate the duration later on and the current date is stored.
     */
    private void tracking() {
        start = Instant.now();
        workoutDate = LocalDateTime.now();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] s = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), s, 1);
            return;
        }
        Log.i("Workout", "tracking started");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, locationListener);
    }

    /**
     * Calculates the distance between two coordinates.
     *
     * @param point1 The earlier position (Point A)
     * @param point2 The current/ later Position (Point B)
     * @return Distance in km
     */
    private double distanceCalculations(double[] point1, double[] point2) {
        double degToRad = Math.PI / 180;
        return earth_radius * degToRad * Math.sqrt(Math.pow(Math.cos(point1[1] * degToRad) * (point1[0] - point2[0]), 2) + Math.pow(point1[1] - point2[1], 2));
    }


    private void setupUI(View view) {
        mContext = view.getContext();
        TextView et_jogName = view.findViewById(R.id.et_jogName);

        EditText et_workoutName = view.findViewById(R.id.et_name);
        EditText et_workoutCount = view.findViewById(R.id.et_count);
        et_timePicker = view.findViewById(R.id.et_timePicker);

        startBtn = view.findViewById(R.id.btn_start);
        stopBtn = view.findViewById(R.id.btn_stop);

        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener(mContext);

        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    closeKeyboard();
                    return false;
                }
            });
        }

        //Start Button to track location and Permission Requests
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBtn.setVisibility(View.GONE);

                stopBtn.setText("");
                stopBtn.setVisibility(View.VISIBLE);

                if (isTracking) {
                    Toast.makeText(getContext(), "There is already a tracking session in progress. Stop the current Workout first.", Toast.LENGTH_LONG).show();

                } else {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        String[] s = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
                        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), s, 1);
                        return;
                    }

                    closeKeyboard();

                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setMessage("Get ready!");
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                    isTracking = true;

                    /**
                     * Countdown timer that shows the user when the workout is about to start
                     */
                    new CountDownTimer(6000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            stopBtn.setText(R.string.countdown);
                            if (millisUntilFinished > 4000) {
                                alertDialog.setMessage("Get ready...");
                                alertDialog.show();
                            } else if (millisUntilFinished < 1000) {
                                stopBtn.setText(R.string.btn_stop_tracking);
                                alertDialog.setMessage("Go!");
                                alertDialog.show();
                            } else {
                                alertDialog.setMessage("Tracking starts in: " + millisUntilFinished / 1000);
                                alertDialog.show();
                            }
                        }

                        public void onFinish() {
                            Log.i("countdown", "Countdown finished successfully");
                            alertDialog.cancel();
                            tracking();
                        }

                    }.start();
                }
            }
        });

        view.findViewById(R.id.et_timePicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                showDateTimePicker();
            }
        });

        //Stops tracking the location
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBtn.setVisibility(View.VISIBLE);
                stopBtn.setVisibility(View.GONE);

                if (!isTracking) {
                    Toast.makeText(getContext(), "There is no Workout to stop.", Toast.LENGTH_SHORT).show();
                } else {
                    /**
                     * Tracking is being stopped. Instant is taken for Duration-calculations.
                     */
                    end = Instant.now();
                    locationManager.removeUpdates(locationListener);
                    isTracking = false;
                    Log.i("Workout", "tracking was stopped.");


                    /**
                     * Calculations
                     */
                    double totalDistance = 0;
                    for (int i = 0; i < positions.size() - 1; i++) {
                        totalDistance += distanceCalculations(positions.get(i), positions.get(i + 1));
                    }

                    Duration time = Duration.between(start, end);
                    String timeString = humanReadableFormat(time);

                    averageSpeed = (totalDistance / 1000) / ((float) time.toMillis() / 3600000);
                    pace = ((float) time.toMillis() / 60000) / (totalDistance / 1000);

                    workoutDate = LocalDateTime.now();

                    Log.i("distance", String.valueOf(totalDistance));
                    Log.i("time", timeString + " " + time);
                    Log.i("speed", String.valueOf(averageSpeed));
                    Log.i("pace", String.valueOf(pace));
                    Log.i("date", String.valueOf(workoutDate));

                    Toast.makeText(mContext, "Great Job!", Toast.LENGTH_LONG).show();
                    positions = new ArrayList<>();
                    Jogging joggen;
                    /**
                     * The results are being sent to the database
                     */
                    if (et_jogName.getText().toString().equals("")) {
                        joggen = new Jogging(workoutDate, "Outdoor Jogging", timeString, f.format((totalDistance / 1000)), f.format(averageSpeed), f.format(pace));
                    } else {
                        joggen = new Jogging(workoutDate, et_jogName.getText().toString(), timeString, f.format((totalDistance / 1000)), f.format(averageSpeed), f.format(pace));
                    }

                    long id = handler.insertFirst(joggen);
                    workoutDate = null;

                    Intent intent = new Intent(v.getContext(), JoggingDetailActivity.class);
                    intent.putExtra("id", id);
                    v.getContext().startActivity(intent);
                }
            }


        });

        view.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("test", et_workoutCount.toString());
                if (et_workoutName.getText().toString().equals("") || et_workoutCount.getText().toString().equals("") || workoutDate == null) {
                    Toast.makeText(v.getContext(), "Some inputs are missing.", Toast.LENGTH_LONG).show();
                } else {
                    String workoutName = et_workoutName.getText().toString();
                    String workoutCount = et_workoutCount.getText().toString();
                    Log.i("workout", et_workoutName.getText().toString());
                    Log.i("workout", et_workoutCount.getText().toString());
                    Log.i("workout", workoutDate.toString());

                    Workout freeworkout = new Workout(workoutDate, workoutName, workoutCount);

                    long id = handler.insertFirst(freeworkout);
                    Toast.makeText(v.getContext(), "Your Workout was saved successfully!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(v.getContext(), WorkoutDetailActivity.class);
                    intent.putExtra("id", id);
                    v.getContext().startActivity(intent);
                }
            }
        });
    }

    /**
     * Is used to create a dialog for picking the date and Time.
     */
    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();

        new DatePickerDialog(getContext(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(getContext(), R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        workoutDate = LocalDateTime.of(year, monthOfYear + 1, dayOfMonth, hourOfDay, minute, 0);
                        et_timePicker.setText(workoutDate.getDayOfMonth() + ". " + workoutDate.getMonth() + " " + workoutDate.getYear() + " " + workoutDate.getHour() + ":" + workoutDate.getMinute());
                        Log.v("DateChange", String.valueOf(workoutDate));
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();

        if(view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
