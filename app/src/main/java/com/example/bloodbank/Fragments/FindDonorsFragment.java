package com.example.bloodbank.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.bloodbank.AdaptersClasses.DonorAdapter;
import com.example.bloodbank.AdaptersClasses.RequestsAdapter;
import com.example.bloodbank.ModelClasses.DonorUsers;
import com.example.bloodbank.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindDonorsFragment extends Fragment {
    Toolbar toolbar;
    RecyclerView recyclerView;
    RequestsAdapter requestsAdapter;
    List<DonorUsers> donorUsersList;
    DatabaseReference donorsRef, requestsRef;
    AppCompatButton btnAll, btnAPositive, btnANegative, btnBPositive, btnBNegative, btnABPositive, btnABNegative, btnOPositive, btnONegative;

    public FindDonorsFragment() {
        // Required empty public constructor
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find_donors, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        toolbar = view.findViewById(R.id.toolbar_balance);
        donorUsersList = new ArrayList<>();

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Blood Requests");

        ImageSlider slider = view.findViewById(R.id.image_slider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.slider1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slider2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slider3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slider4, ScaleTypes.FIT));

        slider.setImageList(slideModels,ScaleTypes.FIT);

        requestsAdapter = new RequestsAdapter(getContext(), donorUsersList  );
        recyclerView.setAdapter(requestsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadDataFromFirebase();
        setupButtonListeners(view);
        // By default, select the "All" button
        filterRequests("All", btnAll);
        return view;

    }
    private void loadDataFromFirebase() {
        donorsRef = FirebaseDatabase.getInstance().getReference("Donors");
        requestsRef = FirebaseDatabase.getInstance().getReference("Blood Requests");

        requestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                donorUsersList.clear();
                for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                    String userId = requestSnapshot.getKey();
                    String requestLocation = requestSnapshot.child("requestLocation").getValue(String.class);
                    String requestMessage = requestSnapshot.child("requestMessage").getValue(String.class);
                    String requestCity = requestSnapshot.child("requestCity").getValue(String.class);
                    String requestBloodGroup = requestSnapshot.child("requestBloodGroup").getValue(String.class);
                    String requestActiveNumber = requestSnapshot.child("requestActiveNumber").getValue(String.class);

                    DonorUsers request = new DonorUsers();
                    request.setRequestLocation(requestLocation);
                    request.setRequestMessage(requestMessage);
                    request.setRequestCity(requestCity);
                    request.setRequestBloodGroup(requestBloodGroup);
                    request.setRequestActiveNumber(requestActiveNumber);

                    donorsRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot donorSnapshot) {
                            if (donorSnapshot.exists()) {
                                String name = donorSnapshot.child("name").getValue(String.class);
                                String profileImage = donorSnapshot.child("profileImage").getValue(String.class);

                                request.setName(name);
                                request.setProfileImage(profileImage);

                                donorUsersList.add(request);
                                requestsAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Failed to load donor data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load requests", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to set up button listeners
    private void setupButtonListeners(View view) {
        btnAll = view.findViewById(R.id.btn_all);
        btnAPositive = view.findViewById(R.id.btn_a_positive);
        btnANegative = view.findViewById(R.id.btn_a_negative);
        btnBPositive = view.findViewById(R.id.btn_b_positive);
        btnBNegative = view.findViewById(R.id.btn_b_negative);
        btnABPositive = view.findViewById(R.id.btn_ab_positive);
        btnABNegative = view.findViewById(R.id.btn_ab_negative);
        btnOPositive = view.findViewById(R.id.btn_o_positive);
        btnONegative = view.findViewById(R.id.btn_o_negative);

        btnAll.setOnClickListener(v -> filterRequests("All", btnAll));
        btnAPositive.setOnClickListener(v -> filterRequests("A+", btnAPositive));
        btnANegative.setOnClickListener(v -> filterRequests("A-", btnANegative));
        btnBPositive.setOnClickListener(v -> filterRequests("B+", btnBPositive));
        btnBNegative.setOnClickListener(v -> filterRequests("B-", btnBNegative));
        btnABPositive.setOnClickListener(v -> filterRequests("AB+", btnABPositive));
        btnABNegative.setOnClickListener(v -> filterRequests("AB-", btnABNegative));
        btnOPositive.setOnClickListener(v -> filterRequests("O+", btnOPositive));
        btnONegative.setOnClickListener(v -> filterRequests("O-", btnONegative));
    }

    // Method to filter requests based on blood group and update button styling
    private void filterRequests(String bloodGroup, Button selectedButton) {
        List<DonorUsers> filteredList = new ArrayList<>();
        if (bloodGroup.equals("All")) {
            filteredList = donorUsersList;
        } else {
            for (DonorUsers donor : donorUsersList) {
                if (donor.getRequestBloodGroup().equals(bloodGroup)) {
                    filteredList.add(donor);
                }
            }
        }

        requestsAdapter.setDonorUsersList(filteredList);
        requestsAdapter.notifyDataSetChanged();

        // Reset the styling of all buttons
        resetButtonStyling();

        // Change the styling of the selected button
        selectedButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.color_primary));
        selectedButton.setTextColor(Color.WHITE);
    }

    // Method to reset the styling of all buttons
    private void resetButtonStyling() {
        btnAll.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.default_button_color));
        btnAPositive.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.default_button_color));
        btnANegative.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.default_button_color));
        btnBPositive.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.default_button_color));
        btnBNegative.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.default_button_color));
        btnABPositive.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.default_button_color));
        btnABNegative.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.default_button_color));
        btnOPositive.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.default_button_color));
        btnONegative.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.default_button_color));

        // Reset text colors
        btnAll.setTextColor(Color.BLACK);
        btnAPositive.setTextColor(Color.BLACK);
        btnANegative.setTextColor(Color.BLACK);
        btnBPositive.setTextColor(Color.BLACK);
        btnBNegative.setTextColor(Color.BLACK);
        btnABPositive.setTextColor(Color.BLACK);
        btnABNegative.setTextColor(Color.BLACK);
        btnOPositive.setTextColor(Color.BLACK);
        btnONegative.setTextColor(Color.BLACK);
    }

    // Rest of the code...
}


