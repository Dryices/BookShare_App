package com.sp.bookshare;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ListDetails extends AppCompatActivity implements OnMapReadyCallback {

    MapView mapView;
    DatabaseReference database;
    private GoogleMap mMap;
    private LatLng coordinate;
    private double lat, lon;
    private String address;
    private TextView username, itemname, price, category, moduleCode, description, location;
    private ImageView itemimage;
    private Button button,buttondelete;
    private String userID,itemID, userPhone = "", messagestr;
    private View.OnClickListener chatOpt = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            chatDialog();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);

        database = FirebaseDatabase.getInstance().getReference().child("Userdata");

        lat = getIntent().getDoubleExtra("LATITUDE", 0);
        lon = getIntent().getDoubleExtra("LONGITUDE", 0);
        Log.d("Check21", "onClick: " + lat);
        Log.d("Check21", "onClick: " + lon);


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
        buttondelete=findViewById(R.id.delete_btn);
        buttondelete.setOnClickListener(onDelete);
        listdetail();
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

    }

    private View.OnClickListener onDelete = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String checkUser = database.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getKey();
            Bundle extras = getIntent().getExtras();
            userID = extras.getString("userID");
            Log.d("Check21", "checkuser: " + checkUser);

            itemID = extras.getString("itemID");
            Log.d("Check21", "itemidcheck: " + itemID);
            if (checkUser.equals(userID)) {
                database.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(itemID).removeValue();
            } else
                Toast.makeText(ListDetails.this, "This is not your item!", Toast.LENGTH_LONG).show();
        }
       };



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
        DatabaseReference databaseUser= FirebaseDatabase.getInstance().getReference().child("Users");
        messagestr = "Bookshare: I am interested in buying " + extras.getString("itemName");

        databaseUser.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userPhone = dataSnapshot.child("phone").getValue(String.class);
                address = dataSnapshot.child("address").getValue(String.class);
                location.setText("Meet-up location : " + address);
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

    private void chatDialog() {
        String options[] = {"Whatsapp", "Telegram"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Log.d("testg", "chatDialog: "+userPhone);
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
        Log.d("Check21", "coordinate: " + coordinate);


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
