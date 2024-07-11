package com.example.bloodbank.AdaptersClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.bloodbank.R;

public class ViewPagerAdapter extends PagerAdapter {
Context context;
int images[] = {
        R.drawable.onboarding2,
        R.drawable.onboarding4,
        R.drawable.onboarding1,
        R.drawable.onboarding3
};
    int headings[] = {
            R.string.heading1,
            R.string.heading2,
            R.string.heading3,
            R.string.heading4
    };
    int description[] = {
            R.string.des1,
            R.string.des2,
            R.string.des3,
            R.string.des4
    };

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_layout,container,false);
        ImageView sliderImageView = view.findViewById(R.id.onboarding_image);
        TextView slideHeading = view.findViewById(R.id.text_title);
        TextView slideDescription = view.findViewById(R.id.text_description);

        sliderImageView.setImageResource(images[position]);
        slideHeading.setText(headings[position]);
        slideDescription.setText(description[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
