package com.example.bloodbank.AdaptersClasses;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bloodbank.ModelClasses.DonorUsers;
import com.example.bloodbank.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.ViewHolder> {
    Context context;
    private List<DonorUsers> donorsList;

    public DonorAdapter(Context context, List<DonorUsers> donorsList) {
        this.context = context;
        this.donorsList = donorsList;
    }

    @NonNull
    @Override
    public DonorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.donor_sample, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorAdapter.ViewHolder holder, int position) {

        DonorUsers donor = donorsList.get(position);
        holder.tvDonorName.setText(donor.getName());
        holder.tvDonorLocation.setText(donor.getLocation());
        holder.tvBlood.setText(donor.getBlood());
        holder.btnRequest.setText("Request");

        holder.btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + donor.getNumber()));
                context.startActivity(intent);
                Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();
            }
        });
        Glide.with(context)
                .load(donor.getProfileImage())
                .into(holder.ivDonor);

    }

    @Override
    public int getItemCount() {
        return donorsList.size();
    }

    public void setDonorsList(List<DonorUsers> donorsList) {
        this.donorsList = donorsList;
        notifyDataSetChanged(); // Notify the adapter to refresh the RecyclerView
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivDonor;
        TextView tvDonorName, tvDonorLocation,tvBlood;
        AppCompatButton btnRequest;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDonor = itemView.findViewById(R.id.profile_donor);
            tvDonorName = itemView.findViewById(R.id.tv_profile_name_donor);
            tvDonorLocation = itemView.findViewById(R.id.tv_profile_location_donor);
            tvBlood = itemView.findViewById(R.id.tv_blood);
            btnRequest = itemView.findViewById(R.id.btn_call_donor);
        }
    }
}
