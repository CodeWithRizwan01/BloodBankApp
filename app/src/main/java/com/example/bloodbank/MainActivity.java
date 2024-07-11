package com.example.bloodbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.bloodbank.Fragments.FindDonorsFragment;
import com.example.bloodbank.Fragments.HomeFragment;
import com.example.bloodbank.Fragments.NotificationFragment;
import com.example.bloodbank.Fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    FrameLayout mainFrameLayout;
    BottomNavigationView mainBottomNavigation;

    Boolean exitFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFrameLayout = findViewById(R.id.mainFrameLayout);
        mainBottomNavigation = findViewById(R.id.mainBottomNavigation);

        replaceFragment(new HomeFragment());

        mainBottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.opt_home){
                    replaceFragment(new HomeFragment());
                } else if (id == R.id.opt_recipe){
                    replaceFragment(new FindDonorsFragment());
                } else if (id == R.id.opt_favorite){
                    replaceFragment(new NotificationFragment());
                } else if (id == R.id.opt_setting){
                    replaceFragment(new ProfileFragment());
                }
                return true;
            }
        });

    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.mainFrameLayout, fragment);
        ft.commit();
    }
    // -----> Back Button Showing Dialog
    @Override
    public void onBackPressed() {
        showAlertDialog();
        if (exitFlag) {
            super.onBackPressed();
        }
    }
    public void openNavigationFragment(Fragment fragment, int id){
        replaceFragment(fragment);
        mainBottomNavigation.setSelectedItemId(id);
    }
    // -----> Exit confirmation dialog
    private void showAlertDialog() {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setCancelable(false)
                .setTitle("Exit")
                .setMessage("Are you sure want to exit?")
                .setIcon(R.drawable.baseline_error_24)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitFlag = false;
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitFlag = true;
                        finishAffinity();
                    }
                })
                .create();
        dialog.show();
    }
}