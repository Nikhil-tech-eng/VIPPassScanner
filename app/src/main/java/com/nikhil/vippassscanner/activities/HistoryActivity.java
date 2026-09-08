package com.nikhil.vippassscanner.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nikhil.vippassscanner.R;
import com.nikhil.vippassscanner.adapters.HistoryAdapter;
import com.nikhil.vippassscanner.models.HistoryItem;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerHistory;
    private HistoryAdapter historyAdapter;
    private List<HistoryItem> historyList;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyAdapter = new HistoryAdapter(historyList);
        db = FirebaseFirestore.getInstance();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Scan History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerHistory = findViewById(R.id.recyclerHistory);

        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));

        historyList = new ArrayList<>();

        historyAdapter = new HistoryAdapter(historyList);

        recyclerHistory.setAdapter(historyAdapter);

        loadHistory();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadHistory() {

        db.collection("scanHistory")
                .orderBy("scannedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    historyList.clear();

                    SimpleDateFormat sdf =
                            new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault());

                    queryDocumentSnapshots.forEach(document -> {

                        String passCode = document.getString("passCode");
                        String scannedBy = document.getString("scannedBy");
                        String status = document.getString("status");

                        Timestamp timestamp = document.getTimestamp("scannedAt");

                        String scannedAt = "";

                        if (timestamp != null) {
                            scannedAt = sdf.format(timestamp.toDate());
                        }

                        historyList.add(
                                new HistoryItem(
                                        passCode,
                                        scannedBy,
                                        scannedAt,
                                        status
                                )
                        );

                    });

                    historyAdapter.notifyDataSetChanged();

                });

    }
}