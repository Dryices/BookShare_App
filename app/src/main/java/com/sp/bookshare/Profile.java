package com.sp.bookshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    private TextView name,email,phone;
    private Button logout;
    private Intent intent;
    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();

        name=findViewById(R.id.profile_name);
        email=findViewById(R.id.profile_email);
        phone=findViewById(R.id.profile_phone);
        logout=findViewById(R.id.button_logout);
        logout.setOnClickListener(onLogout);

        user= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users");
        userID=user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile=snapshot.getValue(User.class);

                if (userProfile!=null){
                    String nameStr= userProfile.name;
                    String emailStr= userProfile.email;
                    String phoneStr= userProfile.phone;

                    name.setText("Name: "+nameStr);
                    email.setText("Email: "+emailStr);
                    phone.setText("Phone: "+phoneStr);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this,"An error has occurred!",Toast.LENGTH_LONG).show();
            }
        });



    }

    private View.OnClickListener onLogout = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebaseAuth.getInstance().signOut();
            intent = new Intent(Profile.this, Login.class);
            startActivity(intent);
            Toast.makeText(Profile.this,"You have successfully logged out!", Toast.LENGTH_LONG).show();
        }
    };
}