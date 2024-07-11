package com.example.bloodbank.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bloodbank.MainActivity;
import com.example.bloodbank.ModelClasses.DonorUsers;
import com.example.bloodbank.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class DonorRegistration extends AppCompatActivity {
    EditText etName, etPassword, etEmail, etNumber, etLocation, etGender, etBloodGroup;
    AppCompatButton btnRegister;
    AppCompatImageButton btnChoose;
    LinearLayout lAlready;
    ProgressDialog pd;
    CircleImageView ivProfile;
    String name, email, password, number, location, gender, blood;
    FirebaseAuth mAuth;
    View bottomSheetView;
    Uri uri;
    Bitmap bitmap;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_registration);

        ivProfile = findViewById(R.id.profile_image);
        btnChoose = findViewById(R.id.btn_pick_image);
        etName = findViewById(R.id.et_name);
        etPassword = findViewById(R.id.et_password);
        etEmail = findViewById(R.id.et_email);
        etNumber = findViewById(R.id.et_number);
        etLocation = findViewById(R.id.et_location);
        etGender = findViewById(R.id.et_gender);
        etBloodGroup = findViewById(R.id.et_age);
        btnRegister = findViewById(R.id.btn_register);
        lAlready = findViewById(R.id.already);

        mAuth = FirebaseAuth.getInstance();

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Validate input data
                if (validateInputs()) {
                    name = etName.getText().toString().trim();
                    email = etEmail.getText().toString().trim();
                    password = etPassword.getText().toString().trim();
                    number = etNumber.getText().toString().trim();
                    location = etLocation.getText().toString().trim();
                    gender = etGender.getText().toString().trim();
                    blood = etBloodGroup.getText().toString().trim().toUpperCase();
                    
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String id = task.getResult().getUser().getUid();
                                    uploadProfileImage(id);
                                    startActivity(new Intent(DonorRegistration.this, MainActivity.class));
                                } else {
                                    Toast.makeText(DonorRegistration.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
            }
        });
        lAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DonorRegistration.this, LoginActivity.class));
            }
        });

    }
    private boolean validateInputs() {
        boolean valid = true;

        if (uri == null) {
            Toast.makeText(this, "Please choose a profile image", Toast.LENGTH_LONG).show();
            valid = false;
        }

        if (etName.getText().toString().trim().isEmpty()) {
            etName.setError("Name is required");
            valid = false;
        } else {
            etName.setError(null);
        }

        if (etEmail.getText().toString().trim().isEmpty()) {
            etEmail.setError("Email is required");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        if (etPassword.getText().toString().trim().isEmpty()) {
            etPassword.setError("Password is required");
            valid = false;
        } else if (etPassword.getText().toString().trim().length() < 8) {
            etPassword.setError("Password must be at least 8 characters");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        if (etNumber.getText().toString().trim().isEmpty()) {
            etNumber.setError("Phone number is required");
            valid = false;
        } else {
            etNumber.setError(null);
        }

        if (etLocation.getText().toString().trim().isEmpty()) {
            etLocation.setError("Location is required");
            valid = false;
        } else {
            etLocation.setError(null);
        }

        if (etGender.getText().toString().trim().isEmpty()) {
            etGender.setError("Gender is required");
            valid = false;
        } else {
            etGender.setError(null);
        }

        if (etBloodGroup.getText().toString().trim().isEmpty()) {
            etBloodGroup.setError("Blood group is required");
            valid = false;
        } else if (!isValidBloodGroup(etBloodGroup.getText().toString().trim())) {
            etBloodGroup.setError("Blood group must be in uppercase (e.g., A+, B-, O+)");
            valid = false;
        } else {
            etBloodGroup.setError(null);
        }

        return valid;
    }
    private boolean isValidBloodGroup(String bloodGroup) {
        return bloodGroup.equals(bloodGroup.toUpperCase());
    }
        private void showBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DonorRegistration.this);
        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        TextView tvChoose = bottomSheetView.findViewById(R.id.tv_choose);

        tvChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                Dexter.withActivity(DonorRegistration.this)
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
        });
        bottomSheetDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                ivProfile.setImageBitmap(bitmap);
            } catch (Exception ex) {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getExtension() {
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(getContentResolver().getType(uri));
    }

    private void uploadProfileImage(final String userId) {
        pd = new ProgressDialog(DonorRegistration.this);
        pd.setTitle("Account Creation");
        pd.show();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference("profile_images/" + System.currentTimeMillis() + "." + getExtension());
        reference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                                        DatabaseReference root = db.getReference().child("Donors").child(userId);
                                        DonorUsers users = new DonorUsers(name, email, password, number, location, gender, blood, uri.toString());
                                        root.setValue(users)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            startActivity(new Intent(DonorRegistration.this, MainActivity.class));
                                                            Toast.makeText(DonorRegistration.this, "Registration successful", Toast.LENGTH_SHORT).show();

                                                            finish();
                                                            etName.setText("");
                                                            etEmail.setText("");
                                                            etPassword.setText("");
                                                            etNumber.setText("");
                                                            etLocation.setText("");
                                                            etGender.setText("");
                                                            etBloodGroup.setText("");
                                                        } else {
                                                            Toast.makeText(DonorRegistration.this, "Failed to save user info: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            etName.setText("");
                                                            etEmail.setText("");
                                                            etPassword.setText("");
                                                            etNumber.setText("");
                                                            etLocation.setText("");
                                                            etGender.setText("");
                                                            etBloodGroup.setText("");
                                                        }
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(DonorRegistration.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        float percent = (100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                        pd.setMessage("Registration successful:"+(int)percent+" %");
                    }
                });
    }

}