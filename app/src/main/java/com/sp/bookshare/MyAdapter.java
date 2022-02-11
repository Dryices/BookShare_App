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
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.LogDescriptor;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<GetUserdata> list;

    public MyAdapter(Context context, ArrayList<GetUserdata> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.exploreitems,parent,false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        GetUserdata user = list.get(position);
        holder.username.setText(user.getItemname());
        holder.price.setText(user.getPrice());
        holder.itemname.setText(user.getItemname());
        holder.category.setText(user.getCategory());
        holder.modulecode.setText(user.getModulecode());
        String link=user.getimageURL();
        if(link.isEmpty()==false) {
            Log.d("Test", " Image URL=" + link);
            Picasso.get().load(link).into(holder.itemImage);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), ListDetails.class);
                intent.putExtra("name",user.getItemname());
                Log.d("test",user.getItemname());
                view.getContext().startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView username,price,itemname, category,modulecode;
        ImageView itemImage;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.view_username);
            price = itemView.findViewById(R.id.view_price);
            itemname = itemView.findViewById(R.id.view_name);
            category = itemView.findViewById(R.id.view_category);
            modulecode = itemView.findViewById(R.id.view_modulecode);
            itemImage=itemView.findViewById((R.id.view_item));
        }
    }

}

