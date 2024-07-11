package com.example.bloodbank.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bloodbank.AdaptersClasses.ViewPagerAdapter;
import com.example.bloodbank.MainActivity;
import com.example.bloodbank.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class onBoardingActivity extends AppCompatActivity {
    ViewPager slidePager;
    TextView tvSkip;
    LinearLayout mDotLayout;
    TextView[] dots;
    ViewPagerAdapter adapter;
    AppCompatImageButton btnNext, btnBack;
    private SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE); // Initialize SharedPreferences correctly
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);

        if (!isFirstRun) {
            // If not the first run, go directly to MainActivity
            startActivity(new Intent(onBoardingActivity.this, RegistrationActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_on_boarding);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(onBoardingActivity.this, MainActivity.class));
            finish();
        }

        slidePager = findViewById(R.id.viewPager);
        tvSkip = findViewById(R.id.tv_skip);
        mDotLayout = findViewById(R.id.indicator_layout);
        btnNext = findViewById(R.id.btn_next);
        btnBack = findViewById(R.id.btn_back);

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markOnboardingComplete();
                // Navigate to main activity or dashboard
                navigateToMainActivity();

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getItem(0) > 0) {
                    slidePager.setCurrentItem(getItem(-1), true);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getItem(0) < 3) {
                    slidePager.setCurrentItem(getItem(1), true);
                } else {
                    startActivity(new Intent(onBoardingActivity.this, RegistrationActivity.class));
                    finish();
                }
            }
        });

        adapter = new ViewPagerAdapter(this);
        slidePager.setAdapter(adapter);
        setUpIndicator(0);
        slidePager.addOnPageChangeListener(viewListener);


    }
    public void setUpIndicator(int position) {
        dots = new TextView[4];
        mDotLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.light_grey, getApplicationContext().getTheme()));
            mDotLayout.addView(dots[i]);

        }
        dots[position].setTextColor(getResources().getColor(R.color.green, getApplicationContext().getTheme()));
    }
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setUpIndicator(position);

            if (position > 0) {
                btnBack.setVisibility(View.VISIBLE);
            } else {
                btnBack.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private int getItem(int i) {
        return slidePager.getCurrentItem() + i;
    }
//    private void markOnboardingComplete() {
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putBoolean("isFirstRun", false);
//        editor.apply();
//    }
//
//    private void navigateToMainActivity() {
//        Intent intent = new Intent(onBoardingActivity.this, RegistrationActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
//    }
private void markOnboardingComplete() {
    SharedPreferences.Editor editor = prefs.edit();
    editor.putBoolean("isFirstRun", false);
    editor.apply();
}

    private void navigateToMainActivity() {
        Intent intent = new Intent(onBoardingActivity.this, RegistrationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}