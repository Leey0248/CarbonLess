package com.homecoming.carbonless;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FeedItemAdapter extends RecyclerView.Adapter<FeedItemAdapter.TaskViewHolder> {

    private final List<FeedItem> dataList;

    private OnItemLongClickListener longClickListener;

    public interface OnItemLongClickListener {
        // We pass the item and return a boolean (true means the event is consumed)
        boolean onItemLongClick(View view, FeedItem item, int position);
    }

    public FeedItemAdapter(List<FeedItem> dataList) {
        this.dataList = dataList;
    }

    // --- 1. The ViewHolder: Holds the view references and click logic ---
    public class TaskViewHolder extends RecyclerView.ViewHolder {
        public ImageView Image;
        public TextView Title;
        public TextView Summary;

        public TaskViewHolder(View itemView) {
            super(itemView);
            Image = itemView.findViewById(R.id.Image);
            Title = itemView.findViewById(R.id.Title);
            Summary = itemView.findViewById(R.id.Summary);

            // Set the Long Click listener on the entire item view
            itemView.setOnClickListener(v -> {

            });
        }
    }

    // --- 2. onCreateViewHolder: Called when RecyclerView needs a new ViewHolder ---
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout (list_item.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_item, parent, false);
        return new TaskViewHolder(view);
    }

    // --- 3. onBindViewHolder: Binds the data to the views in the ViewHolder ---
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        FeedItem currentItem = dataList.get(position);
        String imageUrl = String.valueOf(currentItem.getImageUrl());
        ImageDownloader.downloadAndSetImage(imageUrl, holder.Image);
        holder.Title.setText(currentItem.getTitle());
        holder.Summary.setText(currentItem.getSummary());
    }

    // --- 4. getItemCount: Returns the total number of items ---
    @Override
    public int getItemCount() {
        return dataList.size();
    }
}