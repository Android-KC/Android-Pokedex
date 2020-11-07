package com.example.pokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);  // set view
        adapter = new PokedexAdapter(getApplicationContext());  // create custom adapter with parameter
        layoutManager = new LinearLayoutManager(this);  // create linear layout manager for recyclerview

        recyclerView.setAdapter(adapter);   // set adapter to recyclerview
        recyclerView.setLayoutManager(layoutManager);   // set layout to layoutmanager
    }
}