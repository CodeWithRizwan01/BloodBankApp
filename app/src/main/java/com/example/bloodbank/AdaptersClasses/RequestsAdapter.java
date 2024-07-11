package com.example.bloodbank.AdaptersClasses;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bloodbank.ModelClasses.DonorUsers;
import com.example.bloodbank.R;

import java.util.ArrayList;
import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {
    Context context;
    List<DonorUsers> donorUsers;

    public RequestsAdapter(Context context, List<DonorUsers> donorUsers) {
        this.context = context;
        this.donorUsers = donorUsers;
    }

    @NonNull
    @Override
    public RequestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.request_sample,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsAdapter.ViewHolder holder, int position) {
        DonorUsers donor = donorUsers.get(position);
        holder.tvName.setText(donor.getName());
        holder.tvCity.setText(donor.getRequestCity());
        holder.tvLocation.setText(donor.getRequestLocation());
        holder.tvMessage.setText(donor.getRequestMessage());
        holder.btnBloodGroup.setText(donor.getRequestBloodGroup());
        Glide.with(context).load(donor.getProfileImage()).into(holder.ivProfile);

        holder.btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + donor.getRequestActiveNumber()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return donorUsers.size();
    }
    public void setDonorUsersList(List<DonorUsers> donorsList) {
        this.donorUsers = donorsList;
        notifyDataSetChanged(); // Notify the adapter to refresh the RecyclerView
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfile;
        AppCompatButton btnBloodGroup,btnContact;
        TextView tvName,tvMessage,tvCity,tvLocation;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.profile_image);
            btnBloodGroup = itemView.findViewById(R.id.btn_blood_request);
            btnContact = itemView.findViewById(R.id.btn_call_request);
            tvName = itemView.findViewById(R.id.tv_name);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvCity = itemView.findViewById(R.id.tv_city);
            tvLocation = itemView.findViewById(R.id.tv_location);
        }
    }
}
