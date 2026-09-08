package com.nikhil.vippassscanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nikhil.vippassscanner.R;
import com.nikhil.vippassscanner.models.HistoryItem;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryItem> historyList;

    public HistoryAdapter(List<HistoryItem> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        HistoryItem item = historyList.get(position);

        holder.tvTicketId.setText(item.getPassCode());
        holder.tvPersonName.setText(item.getScannedBy());
        holder.tvEntryTime.setText(item.getScannedAt());
        holder.tvStatus.setText(item.getStatus());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTicketId, tvPersonName, tvEntryTime, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTicketId = itemView.findViewById(R.id.tvTicketId);
            tvPersonName = itemView.findViewById(R.id.tvPersonName);
            tvEntryTime = itemView.findViewById(R.id.tvEntryTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}