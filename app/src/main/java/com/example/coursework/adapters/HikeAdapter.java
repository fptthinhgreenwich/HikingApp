package com.example.coursework.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.R;
import com.example.coursework.models.Hike;
import com.example.coursework.utils.Constants;
import com.example.coursework.utils.DateUtils;

import java.util.List;

/**
 * RecyclerView adapter for displaying hikes in a list
 */
public class HikeAdapter extends RecyclerView.Adapter<HikeAdapter.HikeViewHolder> {

    private Context context;
    private List<Hike> hikeList;
    private OnHikeClickListener listener;

    /**
     * Interface for handling hike item clicks
     */
    public interface OnHikeClickListener {
        void onHikeClick(Hike hike);
    }

    /**
     * Constructor
     */
    public HikeAdapter(Context context, List<Hike> hikeList, OnHikeClickListener listener) {
        this.context = context;
        this.hikeList = hikeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hike, parent, false);
        return new HikeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HikeViewHolder holder, int position) {
        Hike hike = hikeList.get(position);
        holder.bind(hike);
    }

    @Override
    public int getItemCount() {
        return hikeList != null ? hikeList.size() : 0;
    }

    /**
     * Updates the hike list and refreshes the view
     */
    public void updateList(List<Hike> newList) {
        this.hikeList = newList;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for hike items
     */
    class HikeViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView tvName, tvLocation, tvDate, tvLength, tvDifficulty;
        private View difficultyIndicator;

        public HikeViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_hike);
            tvName = itemView.findViewById(R.id.tv_hike_name);
            tvLocation = itemView.findViewById(R.id.tv_hike_location);
            tvDate = itemView.findViewById(R.id.tv_hike_date);
            tvLength = itemView.findViewById(R.id.tv_hike_length);
            tvDifficulty = itemView.findViewById(R.id.tv_hike_difficulty);
            difficultyIndicator = itemView.findViewById(R.id.difficulty_indicator);
        }

        /**
         * Binds hike data to view components
         */
        public void bind(Hike hike) {
            tvName.setText(hike.getName());
            tvLocation.setText(hike.getLocation());
            tvLength.setText(String.format(context.getString(R.string.km_format), hike.getLength()));
            tvDifficulty.setText(hike.getDifficulty());

            // Format date for display
            try {
                String formattedDate = DateUtils.formatDate(
                        DateUtils.parseDate(hike.getDate(), Constants.DATE_FORMAT_DATABASE),
                        Constants.DATE_FORMAT_DISPLAY
                );
                tvDate.setText(formattedDate);
            } catch (Exception e) {
                tvDate.setText(hike.getDate());
            }

            // Set difficulty color
            int colorResId = getDifficultyColor(hike.getDifficulty());
            difficultyIndicator.setBackgroundColor(context.getResources().getColor(colorResId, context.getTheme()));
            tvDifficulty.setTextColor(context.getResources().getColor(colorResId, context.getTheme()));

            // Set click listener
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onHikeClick(hike);
                }
            });
        }

        /**
         * Returns the color resource for a given difficulty level
         */
        private int getDifficultyColor(String difficulty) {
            switch (difficulty.toLowerCase()) {
                case "easy":
                    return R.color.difficulty_easy;
                case "moderate":
                    return R.color.difficulty_moderate;
                case "hard":
                    return R.color.difficulty_hard;
                case "expert":
                    return R.color.difficulty_expert;
                default:
                    return R.color.text_secondary;
            }
        }
    }
}
