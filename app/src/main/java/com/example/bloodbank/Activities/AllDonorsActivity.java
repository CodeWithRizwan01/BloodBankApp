package com.example.bloodbank.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.bloodbank.AdaptersClasses.DonorAdapter;
import com.example.bloodbank.ModelClasses.DonorUsers;
import com.example.bloodbank.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllDonorsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DonorAdapter adapter;
    Toolbar toolbar;
    List<DonorUsers> donorsList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_donors);

        recyclerView = findViewById(R.id.recyclerView_all_donors);
        toolbar = findViewById(R.id.toolbar_balance);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("All Donors");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        adapter = new DonorAdapter(this, donorsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));


        DatabaseReference donorsRef = FirebaseDatabase.getInstance().getReference().child("Donors");
        donorsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                donorsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DonorUsers donor = dataSnapshot.getValue(DonorUsers.class);
                    if (donor != null) {
                        donorsList.add(donor);
                    }
                }
                adapter.setDonorsList(donorsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}