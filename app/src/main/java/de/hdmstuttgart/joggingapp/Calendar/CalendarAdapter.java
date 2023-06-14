package de.hdmstuttgart.joggingapp.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.LocalDate;

import de.hdmstuttgart.joggingapp.R;
import de.hdmstuttgart.joggingapp.ui.main.Fragments.CalenderFragment;

import static de.hdmstuttgart.joggingapp.ui.main.Fragments.CalenderFragment.firstDate;
import static de.hdmstuttgart.joggingapp.ui.main.Fragments.CalenderFragment.lastDayOfMonth;

public class CalendarAdapter extends BaseAdapter {

    private LocalDate today;
    private LayoutInflater infalter;

    public CalendarAdapter(Context context) {
        today = LocalDate.now();
        infalter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return CalenderFragment.dates.length;
    }

    @Override
    public Object getItem(int position) {
        return CalenderFragment.dates[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;

        // Für jeder Item im GridView gibt es ein LocalDatePbject in einem Array
        LocalDate date = CalenderFragment.dates[position];

        if(view == null) {
            holder = new ViewHolder();
            view = infalter.inflate(R.layout.day_layout, parent, false);

            holder.dayView = view.findViewById(R.id.day_int);
            holder.date_background = view.findViewById(R.id.date_background);
            holder.joggingMarker = view.findViewById(R.id.joggingMarker);
            holder.workoutMarker = view.findViewById(R.id.workoutMarker);

            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();

        }

        /*
            Einzelne Tage im Monat stylen
         */

        // Text + Farbe
        holder.dayView.setText("" + date.getDayOfMonth());
        holder.dayView.setTextColor(Color.parseColor("#F7FFF7"));


        // Nicht im eigentlichem Monat
        if(date.isBefore(firstDate) || date.isAfter(lastDayOfMonth)) {
            holder.dayView.setTextColor(Color.parseColor("#D5DBD5"));
            holder.dayView.setTextSize(15);

            if(holder.joggingMarker.getVisibility() == View.VISIBLE) {
                holder.joggingMarker.setVisibility(View.INVISIBLE);
            }

            if(holder.workoutMarker.getVisibility() == View.VISIBLE) {
                holder.workoutMarker.setVisibility(View.INVISIBLE);
            }

        // im eigentlichem Monat
        } else {
            holder.dayView.setTextColor(Color.parseColor("#F7FFF7"));
            holder.dayView.setTextSize(25);

            // heutiger Tag -> Kreis einfärben
            if(date.equals(today)) {
                holder.date_background.setVisibility(View.VISIBLE);

            // nicht heutiger Tag -> nicht einfärben
            } else {
                if(holder.date_background.getVisibility() == View.VISIBLE) {
                    holder.date_background.setVisibility(View.INVISIBLE);

                }

            }

            // Training für den Tag gespeichert
            // -> Jogging
            if(CalenderFragment.trainings.contains(position + 1 - CalenderFragment.dayOfWeekInt)) {
                holder.joggingMarker.setVisibility(View.VISIBLE);

            } else {
                if(holder.joggingMarker.getVisibility() == View.VISIBLE) {
                    holder.joggingMarker.setVisibility(View.INVISIBLE);

                }

            }

            // -> Workout
            if(CalenderFragment.trainings.contains((position + 1 - CalenderFragment.dayOfWeekInt) + 31)) {
                holder.workoutMarker.setVisibility(View.VISIBLE);

            } else {
                if(holder.workoutMarker.getVisibility() == View.VISIBLE) {
                    holder.workoutMarker.setVisibility(View.INVISIBLE);

                }
            }
        }

        return view;
    }

    private static class ViewHolder{
        TextView dayView;
        ImageView date_background;
        ImageView joggingMarker;
        ImageView workoutMarker;
    }
}
