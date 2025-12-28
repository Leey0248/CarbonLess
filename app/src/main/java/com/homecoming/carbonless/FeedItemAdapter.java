package com.homecoming.carbonless;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FeedItemAdapter extends RecyclerView.Adapter<FeedItemAdapter.FeedViewHolder> {

    private final List<FeedItem> dataList;
    private final OnItemClickListener listener; // Added as final

    public interface OnItemClickListener {
        void onItemClick(FeedItem item); // Passing the data object is often most helpful
    }

    // Updated Constructor: Now requires both data and the listener
    public FeedItemAdapter(List<FeedItem> dataList, OnItemClickListener listener) {
        this.dataList = dataList;
        this.listener = listener;
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        public ImageView Image;
        public TextView Title;
        public TextView Summary;

        public FeedViewHolder(View itemView) {
            super(itemView);
            Image = itemView.findViewById(R.id.Image);
            Title = itemView.findViewById(R.id.Title);
            Summary = itemView.findViewById(R.id.Summary);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                // Ensure the click is valid and the listener exists
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(dataList.get(position));
                }
            });
        }
    }

    // --- 2. onCreateViewHolder: Called when RecyclerView needs a new ViewHolder ---
    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout (list_item.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_item, parent, false);
        return new FeedViewHolder(view);
    }

    // --- 3. onBindViewHolder: Binds the data to the views in the ViewHolder ---
    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
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