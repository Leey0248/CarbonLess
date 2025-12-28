package com.homecoming.carbonless;

import android.content.Context; // Added import
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
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(FeedItem item);
    }

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
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(dataList.get(position));
                }
            });
        }
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_item, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        FeedItem currentItem = dataList.get(position);

        // 1. Get the context from the view
        Context context = holder.itemView.getContext();

        // 2. Get the URL string
        String imageUrl = String.valueOf(currentItem.getImageUrl());

        // 3. Pass the context to our updated ImageDownloader
        ImageDownloader.downloadAndSetImage(context, imageUrl, holder.Image);

        holder.Title.setText(currentItem.getTitle());
        holder.Summary.setText(currentItem.getSummary());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}