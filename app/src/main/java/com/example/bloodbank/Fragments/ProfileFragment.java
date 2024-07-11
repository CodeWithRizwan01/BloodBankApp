package com.example.bloodbank.Fragments;

import static android.app.Activity.RESULT_OK;

import static androidx.browser.customtabs.CustomTabsClient.getPackageName;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bloodbank.Activities.DonorRegistration;
import com.example.bloodbank.Activities.LoginActivity;
import com.example.bloodbank.ModelClasses.DonorUsers;
import com.example.bloodbank.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    Toolbar toolbar;
    CircleImageView ivProfileImage, ivPlace;
    TextView tvName, tvLocation;
    AppCompatButton btnContactNow, btnBloodGroup;
    LinearLayout lEditProfile, lShareApp, lLogout;

    FirebaseAuth mAuth;
    DatabaseReference userRef;
    private String phoneNumber;

    EditText etName, etLocation, etBloodGroup, etNumber;
    ProgressDialog pd;

    private String userProfileImageUrl;
    private Uri imageUri;
    Bitmap bitmap;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        toolbar = view.findViewById(R.id.toolbar_detail);
        ivProfileImage = view.findViewById(R.id.profile_image);
        tvName = view.findViewById(R.id.tv_profile_name);
        tvLocation = view.findViewById(R.id.tv_profile_location);
        btnBloodGroup = view.findViewById(R.id.btn_blood);
        btnContactNow = view.findViewById(R.id.btn_call);
        lEditProfile = view.findViewById(R.id.edit_profile);
        lLogout = view.findViewById(R.id.logout);
        lShareApp = view.findViewById(R.id.app_share);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        }

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Donors").child(userId);

        fetchUserData();

        btnContactNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Phone number not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileBottomSheet();
            }
        });
        lShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAppLink();
            }
        });

        lLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog();

            }
        });
        return view;

    }
    // Option --> share app
    private void shareAppLink() {
        Intent myIntent = new Intent(Intent.ACTION_SEND);
        myIntent.setType("text/plain");
        String body = "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName();
        String sub = "Your Subject";
        myIntent.putExtra(Intent.EXTRA_SUBJECT, sub);
        myIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(myIntent, "Share Using"));
    }
    private void alertDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setIcon(R.drawable.logout)
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                })

                .create();
        dialog.show();
    }

    private void fetchUserData() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DonorUsers user = snapshot.getValue(DonorUsers.class);

                    if (user != null) {
                        tvName.setText(user.getName());
                        tvLocation.setText(user.getLocation());
                        btnBloodGroup.setText(user.getBlood());
                        phoneNumber = user.getNumber();
                        userProfileImageUrl = user.getProfileImage();

                        if (user.getProfileImage() != null) {
                            Glide.with(getContext())
                                    .load(Uri.parse(user.getProfileImage()))
                                    .into(ivProfileImage);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void showEditProfileBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_profile, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        etName = bottomSheetView.findViewById(R.id.et_edit_name);
        etLocation = bottomSheetView.findViewById(R.id.et_edit_location);
        etBloodGroup = bottomSheetView.findViewById(R.id.et_edit_blood_group);
        etNumber = bottomSheetView.findViewById(R.id.et_edit_number);
        AppCompatImageButton ivEditImage = bottomSheetView.findViewById(R.id.btn_edit_profile_image);
        ivPlace = bottomSheetView.findViewById(R.id.iv_edit_profile_image);
        AppCompatButton btnUpdate = bottomSheetView.findViewById(R.id.btn_update_profile);

        // Load current user data into the fields
        etName.setText(tvName.getText());
        etLocation.setText(tvLocation.getText());
        etBloodGroup.setText(btnBloodGroup.getText());
        etNumber.setText(phoneNumber);
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            etNumber.setText(phoneNumber);
        }
        if (userProfileImageUrl != null && !userProfileImageUrl.isEmpty()) {
            Glide.with(getContext()).load(userProfileImageUrl).into(ivPlace);
        }

        ivEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle profile image change
                // Code to pick image from gallery or take a new photo
                chooseImage();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate inputs and update profile in Firebase
                String newName = etName.getText().toString().trim();
                String newLocation = etLocation.getText().toString().trim();
                String newBloodGroup = etBloodGroup.getText().toString().trim();
                String newNumber = etNumber.getText().toString().trim();

                if (newName.isEmpty() || newLocation.isEmpty() || newBloodGroup.isEmpty() || newNumber.isEmpty()) {
                    Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Add validation if needed
                if (imageUri != null) {
                    uploadImageToFirebase(imageUri, newName, newLocation, newBloodGroup, newNumber);
                } else {
                    updateUserProfile(newName, newLocation, newBloodGroup, newNumber, userProfileImageUrl);
                }
//                updateUserProfile(newName, newLocation, newBloodGroup, newNumber);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    private void chooseImage() {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Please select a image"), 1);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            imageUri = data.getData();
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                ivPlace.setImageBitmap(bitmap);
            } catch (Exception ex) {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public String getExtension() {
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(getActivity().getContentResolver().getType(imageUri));
    }

    private void uploadImageToFirebase(Uri uri, String name, String location, String bloodGroup, String number) {
        pd = new ProgressDialog(getActivity());
        pd.setTitle("Account Creation");
        pd.show();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference fileRef = storage.getReference("profile_images/" + System.currentTimeMillis() + "." + getExtension());


        fileRef.putFile(uri).addOnSuccessListener(taskSnapshot ->
                fileRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                    String imageUri = uri1.toString();
                    updateUserProfile(name, location, bloodGroup, number, imageUri);
                    pd.dismiss();
                })).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(getContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUserProfile(String name, String location, String bloodGroup, String number, String profileImageUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("location", location);
        updates.put("blood", bloodGroup);
        updates.put("number", number);
        updates.put("profileImage", profileImageUrl);

        // If the profile image was changed, handle the upload and add the URL to updates

        userRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                // Update local UI with new data
                tvName.setText(name);
                tvLocation.setText(location);
                btnBloodGroup.setText(bloodGroup);
                phoneNumber = number;
                userProfileImageUrl = profileImageUrl;
                if (profileImageUrl != null) {
                    Glide.with(getContext()).load(profileImageUrl).into(ivProfileImage);
                }
            } else {
                Toast.makeText(getContext(), "Profile update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

