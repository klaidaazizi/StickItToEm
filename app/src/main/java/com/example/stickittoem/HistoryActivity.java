package com.example.stickittoem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;

import android.os.Bundle;

import android.widget.TextView;


import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    private ArrayList<Message> historyList;

    private RecyclerView recyclerView;
    private RviewAdapter rviewAdapter;
    private RecyclerView.LayoutManager rLayoutManger;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historyList = (ArrayList<Message>) getIntent().getSerializableExtra("messages");
        setTitle("History");
        setContentView(R.layout.activity_history);
        final TextView countTV = (TextView) findViewById(R.id.countTV);
        countTV.setText("Total Stickers Sent: " + historyList.size());
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        createRecyclerView();
    }

    private void createRecyclerView() {
        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.historyRV);
        recyclerView.setHasFixedSize(true);
        rviewAdapter = new RviewAdapter(historyList);

        recyclerView.setAdapter(rviewAdapter);
        recyclerView.setLayoutManager(rLayoutManger);
    }
}
