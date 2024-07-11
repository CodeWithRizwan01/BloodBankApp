package com.example.bloodbank.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bloodbank.Activities.MapActivity;
import com.example.bloodbank.ModelClasses.DonorUsers;
import com.example.bloodbank.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class NotificationFragment extends Fragment {
    EditText etBlood, etCity, etNumber, etLocation, etMessage;
    TextView tvSelectOnMap;
    AppCompatButton btnAddRequest;

    FirebaseDatabase database;
    DatabaseReference reference;
    private static final int REQUEST_LOCATION_CODE = 1;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        etBlood = view.findViewById(R.id.et_blood_request);
        etCity = view.findViewById(R.id.et_request_city);
        etNumber = view.findViewById(R.id.et_request_number);
        etLocation = view.findViewById(R.id.et_request_location);
        etMessage = view.findViewById(R.id.et_request_messsage);

        tvSelectOnMap = view.findViewById(R.id.tv_choose_location);
        btnAddRequest = view.findViewById(R.id.btn_add_request);


        tvSelectOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapForSelection();
            }
        });

        database = FirebaseDatabase.getInstance();
        btnAddRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToFirebase();
            }
        });


        return view;
    }

    private void uploadToFirebase() {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Data Uploading");
        dialog.setMessage("Please wait we are uploading your data");
        dialog.show();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        String userId = currentUser.getUid();
        String bloodGroup = etBlood.getText().toString();
        String city = etCity.getText().toString();
        String activeNumber = etNumber.getText().toString();
        String requestLocation = etLocation.getText().toString();
        String requestMessage = etMessage.getText().toString();

        // Validate inputs
        if (bloodGroup.isEmpty()) {
            etBlood.setError("Blood group is required");
            dialog.dismiss();
            return;
        } else if (!isUppercase(bloodGroup)) {
            etBlood.setError("Blood group must be in uppercase (e.g., A+, B-, O+)");
            dialog.dismiss();
            return;
        } else {
            etBlood.setError(null);
        }

        if (city.isEmpty()) {
            etCity.setError("City is required");
            dialog.dismiss();
            return;
        } else {
            etCity.setError(null);
        }

        if (activeNumber.isEmpty()) {
            etNumber.setError("Phone number is required");
            dialog.dismiss();
            return;
        } else {
            etNumber.setError(null);
        }

        if (requestLocation.isEmpty()) {
            etLocation.setError("Location is required");
            dialog.dismiss();
            return;
        } else {
            etLocation.setError(null);
        }

        if (requestMessage.isEmpty()) {
            etMessage.setError("Message is required");
            dialog.dismiss();
            return;
        } else {
            etMessage.setError(null);
        }

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DonorUsers user = new DonorUsers(requestLocation, requestMessage, city, bloodGroup, activeNumber);
        DatabaseReference root = db.getReference("Blood Requests").child(userId);

        root.setValue(user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.dismiss();
                                if (task.isSuccessful()){
                                    etBlood.setText("");
                                    etCity.setText("");
                                    etNumber.setText("");
                                    etLocation.setText("");
                                    etMessage.setText("");
                                    Toast.makeText(getActivity(), "Data uploaded Successfully", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getActivity(), "Failed to upload the data", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss(); // Ensure the dialog is dismissed on failure
                        Toast.makeText(getActivity(), "Data upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }
    private boolean isUppercase(String text) {
        return text.equals(text.toUpperCase());
    }
    private void openMapForSelection() {
        Intent intent = new Intent(getActivity(), MapActivity.class);
        startActivityForResult(intent, REQUEST_LOCATION_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION_CODE && resultCode == Activity.RESULT_OK && data != null) {
            String address = data.getStringExtra("address");
            if (address != null) {
                etLocation.setText(address);
            }
        }
    }

}