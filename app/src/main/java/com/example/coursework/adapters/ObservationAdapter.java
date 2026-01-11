package com.example.coursework.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;
import com.example.coursework.models.Observation;
import com.example.coursework.utils.Constants;
import com.example.coursework.utils.DateUtils;

import java.util.List;

/**
 * RecyclerView adapter for displaying observations in a list
 */
public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.ObservationViewHolder> {

    private Context context;
    private List<Observation> observationList;
    private OnObservationActionListener listener;

    /**
     * Interface for handling observation item actions
     */
    public interface OnObservationActionListener {
        void onObservationClick(Observation observation);
        void onDeleteClick(Observation observation);
    }

    /**
     * Constructor
     */
    public ObservationAdapter(Context context, List<Observation> observationList,
                             OnObservationActionListener listener) {
        this.context = context;
        this.observationList = observationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ObservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_observation, parent, false);
        return new ObservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObservationViewHolder holder, int position) {
        Observation observation = observationList.get(position);
        holder.bind(observation);
    }

    @Override
    public int getItemCount() {
        return observationList != null ? observationList.size() : 0;
    }

    /**
     * Updates the observation list and refreshes the view
     */
    public void updateList(List<Observation> newList) {
        this.observationList = newList;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for observation items
     */
    class ObservationViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView tvObservation, tvTime, tvComments;
        private ImageButton btnDelete;

        public ObservationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_observation);
            tvObservation = itemView.findViewById(R.id.tv_observation_text);
            tvTime = itemView.findViewById(R.id.tv_observation_time);
            tvComments = itemView.findViewById(R.id.tv_observation_comments);
            btnDelete = itemView.findViewById(R.id.btn_delete_observation);
        }

        /**
         * Binds observation data to view components
         */
        public void bind(Observation observation) {
            tvObservation.setText(observation.getObservation());

            // Format time for display
            try {
                String formattedTime = DateUtils.formatDate(
                        DateUtils.parseDate(observation.getTime(), Constants.DATETIME_FORMAT_DATABASE),
                        Constants.DATETIME_FORMAT_DISPLAY
                );
                tvTime.setText(formattedTime);
            } catch (Exception e) {
                tvTime.setText(observation.getTime());
            }

            // Show/hide comments
            if (observation.getComments() != null && !observation.getComments().isEmpty()) {
                tvComments.setVisibility(View.VISIBLE);
                tvComments.setText(observation.getComments());
            } else {
                tvComments.setVisibility(View.GONE);
            }

            // Set click listener for card
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onObservationClick(observation);
                }
            });

            // Set click listener for delete button
            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(observation);
                }
            });
        }
    }
}
