package com.sp.bookshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {

    private TextView name, email, phone,logout;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name=view.findViewById(R.id.profile_name);
        email=view.findViewById(R.id.profile_email);
        phone=view.findViewById(R.id.profile_phone);

        user= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users");
        userID=user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userProfile=snapshot.getValue(Users.class);

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
                Toast.makeText(getActivity(),"An error has occurred!",Toast.LENGTH_LONG).show();
            }
        });
        // initiating the tabhost
        TabHost tabhost = (TabHost) view.findViewById(R.id.tabhost_id);

        // setting up the tab host
        tabhost.setup();

        // Code for adding Tab 1 to the tabhost
        TabHost.TabSpec spec = tabhost.newTabSpec("Listings");
        spec.setContent(R.id.Listings);

        // setting the name of the tab 1 as "Tab One"
        spec.setIndicator("Listings");

        // adding the tab to tabhost
        tabhost.addTab(spec);

        // Code for adding Tab 2 to the tabhost
        spec = tabhost.newTabSpec("Ratings");
        spec.setContent(R.id.Ratings);

        // setting the name of the tab 1 as "Tab Two"
        spec.setIndicator("Ratings");
        tabhost.addTab(spec);

        // Code for adding Tab 3 to the tabhost
        spec = tabhost.newTabSpec("About");
        spec.setContent(R.id.About);
        spec.setIndicator("About");
        tabhost.addTab(spec);
    }
}
