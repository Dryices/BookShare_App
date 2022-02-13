package com.sp.bookshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListDetails extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    MapView mapView;
    private LatLng coordinate;
    private double lat, lon;
    private String address;
    DatabaseReference database;

    private TextView username, itemname, price, category, moduleCode, description, location;
    private ImageView itemimage;
    private Button button;
    private String userID, userPhone = "", messagestr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);

        database = FirebaseDatabase.getInstance().getReference().child("Users");

        lat=getIntent().getDoubleExtra("LATITUDE",0);
        lon=getIntent().getDoubleExtra("LONGITUDE",0);


        mapView = findViewById(R.id.mapDetail);
        username = findViewById(R.id.detail_username);
        itemname = findViewById(R.id.detail_itemname);
        price = findViewById(R.id.detail_price);
        category = findViewById(R.id.detail_category);
        moduleCode = findViewById(R.id.detail_module);
        description = findViewById(R.id.detail_description);
        location = findViewById(R.id.detail_location);
        itemimage = findViewById(R.id.detail_image);
        button = findViewById(R.id.chat_btn);
        button.setOnClickListener(chatOpt);
        listdetail();
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

    }

    public void listdetail() {

        String imageURL;
        Bundle extras = getIntent().getExtras();
        username.setText("Username: " + extras.getString("username"));
        itemname.setText("Itemname: " + extras.getString("itemName"));
        price.setText("Price: $" + extras.getString("price"));
        category.setText("Category: " + extras.getString("category"));
        moduleCode.setText("Module: " + extras.getString("module"));
        description.setText("Description: " + extras.getString("description"));
        userID = extras.getString("userID");
        imageURL = extras.getString("image");

        messagestr = "Bookshare: I am interested in buying " + extras.getString("itemName");

        database.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userPhone = dataSnapshot.child("phone").getValue(String.class);
                address = dataSnapshot.child("address").getValue(String.class);
                location.setText("Meet-up location : " +address);
                //Log.d("Check", "onClick: " + userPhone);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (imageURL.isEmpty() == false) {
            //Log.d("Test", " Image URL=" + link);
            Picasso.get().load(imageURL).into(itemimage);
        }

    }

    private View.OnClickListener chatOpt = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            chatDialog();
        }
    };


    private void chatDialog() {
        String options[] = {"Whatsapp", "Telegram"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select contact option with phone number +65 " + userPhone);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 1) {
                    Intent telegramIntent = new Intent(Intent.ACTION_VIEW);
                    telegramIntent.setData(Uri.parse("http://telegram.me/Dryices"));
                    startActivity(telegramIntent);

                } else {
                    String url = "https://api.whatsapp.com/send?phone=65" + userPhone;
                    Log.d("Phone", "onClick: " + userPhone);
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url +
                            "&text=" + messagestr));
                    startActivity(i);
                }
            }
        });
        builder.create().show();
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        coordinate = new LatLng(lat, lon);

        Marker restaurant = mMap.addMarker(new MarkerOptions().position(coordinate).title("Location"));

        //Move the camera instantly to restaurant with zoom of 15
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 15));

        /* Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
         */
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


}
