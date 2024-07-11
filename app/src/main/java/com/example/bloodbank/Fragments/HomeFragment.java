package com.example.bloodbank.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.bloodbank.Activities.AllDonorsActivity;
import com.example.bloodbank.AdaptersClasses.DonorAdapter;
import com.example.bloodbank.AdaptersClasses.RequestsAdapter;
import com.example.bloodbank.ModelClasses.DonorUsers;
import com.example.bloodbank.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    RecyclerView recyclerView;
    DonorAdapter adapter;
    AppCompatButton btnAll, btnAPositive, btnANegative, btnBPositive, btnBNegative, btnABPositive, btnABNegative, btnOPositive, btnONegative;
    List<DonorUsers> donorsList = new ArrayList<>();
    List<DonorUsers> displayedDonorsList = new ArrayList<>();

    private List<AppCompatButton> buttons = new ArrayList<>();
    private Button selectedButton = null;
    TextView tvNoResults,tvSeeAll;

    ImageView ivDisclaimer;

    EditText searchEditText;
    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
        ivDisclaimer = view.findViewById(R.id.imageView_disc);
        tvNoResults = view.findViewById(R.id.tv_no_results);
        tvSeeAll = view.findViewById(R.id.see_all);
        adapter = new DonorAdapter(getContext(), donorsList);
//        adapter = new DonorAdapter(getContext(), displayedDonorsList);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        ivDisclaimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_disclaimer);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView btnOk = dialog.findViewById(R.id.tvBtnOk);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

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
                showLimitedDonors();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Set up the search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDonors(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Setup buttons
        setupButtons(view);

        // Select "All" button by default
        btnAll = view.findViewById(R.id.btn_all);
        filterDonorsByBloodGroup("All", btnAll);

        tvSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AllDonorsActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
    private void showLimitedDonors() {
        List<DonorUsers> limitedDonors = donorsList.size() > 8 ? donorsList.subList(0, 8) : donorsList;
        adapter.setDonorsList(limitedDonors);
        toggleNoResultsMessage();
    }
    private void setupButtons(View view) {
        btnAll = view.findViewById(R.id.btn_all);
        btnAPositive = view.findViewById(R.id.btn_a_positive);
        btnANegative = view.findViewById(R.id.btn_a_negative);
        btnBPositive = view.findViewById(R.id.btn_b_positive);
        btnBNegative = view.findViewById(R.id.btn_b_negative);
        btnABPositive = view.findViewById(R.id.btn_ab_positive);
        btnABNegative = view.findViewById(R.id.btn_ab_negative);
        btnOPositive = view.findViewById(R.id.btn_o_positive);
        btnONegative = view.findViewById(R.id.btn_o_negative);

        buttons.add(btnAll);
        buttons.add(btnAPositive);
        buttons.add(btnANegative);
        buttons.add(btnBPositive);
        buttons.add(btnBNegative);
        buttons.add(btnABPositive);
        buttons.add(btnABNegative);
        buttons.add(btnOPositive);
        buttons.add(btnONegative);

        btnAll.setOnClickListener(v -> filterDonorsByBloodGroup("All", btnAll));
        btnAPositive.setOnClickListener(v -> filterDonorsByBloodGroup("A+", btnAPositive));
        btnANegative.setOnClickListener(v -> filterDonorsByBloodGroup("A-", btnANegative));
        btnBPositive.setOnClickListener(v -> filterDonorsByBloodGroup("B+", btnBPositive));
        btnBNegative.setOnClickListener(v -> filterDonorsByBloodGroup("B-", btnBNegative));
        btnABPositive.setOnClickListener(v -> filterDonorsByBloodGroup("AB+", btnABPositive));
        btnABNegative.setOnClickListener(v -> filterDonorsByBloodGroup("AB-", btnABNegative));
        btnOPositive.setOnClickListener(v -> filterDonorsByBloodGroup("O+", btnOPositive));
        btnONegative.setOnClickListener(v -> filterDonorsByBloodGroup("O-", btnONegative));
    }
    private void filterDonorsByBloodGroup(String bloodGroup, Button selectedButton) {
        List<DonorUsers> filteredList = new ArrayList<>();
        if (bloodGroup.equals("All")) {
            filteredList = donorsList;
        } else {
            for (DonorUsers donor : donorsList) {
                if (donor.getBlood().equals(bloodGroup)) {
                    filteredList.add(donor);
                }
            }
        }

        adapter.setDonorsList(filteredList);
        toggleNoResultsMessage();

        // Change the selected button color
        selectedButton.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.color_primary));
        selectedButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

        // Reset the color of the previously selected button
        if (this.selectedButton != null && this.selectedButton != selectedButton) {
            this.selectedButton.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.default_button_color));
            this.selectedButton.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

        }

        // Set the current button as the selected button
        this.selectedButton = selectedButton;
    }

    private void resetButtonColors(Button currentButton) {
        ViewGroup parent = (ViewGroup) currentButton.getParent();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof Button && child != currentButton) {
                Button button = (Button) child;
                button.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            }
        }
    }

    private void filterDonors(String searchText) {
        List<DonorUsers> filteredList = new ArrayList<>();
        for (DonorUsers donor : donorsList) {
            if (donor.getLocation().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(donor);
            }
        }
        adapter.setDonorsList(filteredList);
        toggleNoResultsMessage();
    }
    private void toggleNoResultsMessage() {
        if (adapter.getItemCount() == 0) {
            tvNoResults.setVisibility(View.VISIBLE);
        } else {
            tvNoResults.setVisibility(View.GONE);
        }
    }
}
