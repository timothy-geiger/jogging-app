package de.hdmstuttgart.joggingapp.Calendar.BottomSheet;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;

import de.hdmstuttgart.joggingapp.Calendar.BottomSheet.TrainingDetail.JoggingDetailActivity;
import de.hdmstuttgart.joggingapp.Calendar.BottomSheet.TrainingDetail.WorkoutDetailActivity;
import de.hdmstuttgart.joggingapp.Database.Training.Training;
import de.hdmstuttgart.joggingapp.R;

public class BottomSheetAdapter extends RecyclerView.Adapter<BottomSheetAdapter.ViewHolder> {
    // Format wie das Datum angezeigt werden soll bei den einzelnen Trainings items im RecaclerView
    private final DateTimeFormatter sdf = DateTimeFormatter.ofPattern("HH:mm");

    @NonNull
    @Override
    public BottomSheetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_sheet_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetAdapter.ViewHolder holder, int position) {
        // Das jeweilige Training
        final Training training = BottomSheet.dayTrainings.get(position);

        // Wenn vom Typ Joggen -> Background + Icon passend setzten
        if(training.getType() == 'j') {
            holder.icon_bg.setBackgroundResource(R.drawable.j_icon_bg);
            holder.icon.setImageResource(R.drawable.run);

        // Wenn vom Typ Workout -> Background + Icon passend setzten
        } else {
            holder.icon_bg.setBackgroundResource(R.drawable.w_icon_bg);
            holder.icon.setImageResource(R.drawable.gym);

        }

        // Namen + Datum anzeigen
        holder.name.setText(training.getName());
        holder.date.setText(sdf.format(training.getDate()));

        // OnClickListener passend setzten
        holder.linearLayout.setOnClickListener(v -> {
            Intent intent;

            if(training.getType() == 'j') {
                intent = new Intent(v.getContext(), JoggingDetailActivity.class);

            } else {
                intent = new Intent(v.getContext(), WorkoutDetailActivity.class);

            }

            intent.putExtra("id", training.getId());

            v.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return BottomSheet.dayTrainings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView name;
        public TextView date;
        public LinearLayout icon_bg;
        public LinearLayout linearLayout;

        public ViewHolder(@NonNull View v) {
            super(v);

            icon = v.findViewById(R.id.trainingIcon);
            name = v.findViewById(R.id.trainingName);
            date = v.findViewById(R.id.trainingDate);
            icon_bg = v.findViewById(R.id.icon_bg);
            linearLayout = v.findViewById(R.id.linearLayout);
        }
    }
}
