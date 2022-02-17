package com.sp.bookshare;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<GetUserdata> list;
    DatabaseReference database;
    Double lon, lat;
    String sellerID;

    public MyAdapter(Context context, ArrayList<GetUserdata> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.exploreitems, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        GetUserdata user = list.get(position);


        holder.username.setText(user.getSeller());
        holder.price.setText(user.getPrice());
        holder.itemname.setText(user.getItemname());
        holder.category.setText(user.getCategory());
        holder.modulecode.setText(user.getModulecode());

        String link = user.getimageURL();

        if (link.isEmpty() == false) {
            //Log.d("Test", " Image URL=" + link);
            Picasso.get().load(link).into(holder.itemImage);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sellerID = user.getUserID();
                Log.d("Check24", "onClick: " + sellerID);

                database = FirebaseDatabase.getInstance().getReference().child("Users");
                database.child(sellerID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        lat = dataSnapshot.child("latitude").getValue(Double.class);
                        lon = dataSnapshot.child("longitude").getValue(Double.class);


                        Log.d("Check23", "onClick: " + lat);
                        Log.d("Check23", "onClick: " + lon);


                        Intent intent = new Intent(view.getContext(), ListDetails.class);
                        intent.putExtra("username", user.getSeller());
                        intent.putExtra("itemName", user.getItemname());
                        intent.putExtra("price", user.getPrice());
                        intent.putExtra("category", user.getCategory());
                        intent.putExtra("module", user.getModulecode());
                        intent.putExtra("description", user.getDescription());
                        intent.putExtra("image", user.getimageURL());
                        intent.putExtra("userID", user.getUserID());
                        intent.putExtra("LATITUDE", lat);
                        intent.putExtra("LONGITUDE", lon);
                        intent.putExtra("itemID", user.getList());
                        Log.d("Check24", "oniditem: " + user.getList());
                        Log.d("Check24", "oniditem: " + user.getUserID());
                        Log.d("Check24", "onClick: " + lon);
                        view.getContext().startActivity(intent);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView username, price, itemname, category, modulecode;
        ImageView itemImage;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.view_username);
            price = itemView.findViewById(R.id.view_price);
            itemname = itemView.findViewById(R.id.view_name);
            category = itemView.findViewById(R.id.view_category);
            modulecode = itemView.findViewById(R.id.view_modulecode);
            itemImage = itemView.findViewById((R.id.view_item));
        }
    }

}

