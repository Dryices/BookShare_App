package com.sp.bookshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private TextView back;
    private EditText name,email,phone,password;
    private ProgressBar progressBar;
    private Button register;
    Intent intent;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        back=findViewById(R.id.back_register);
        name=findViewById(R.id.name_id);
        email=findViewById(R.id.email_id);
        phone=findViewById(R.id.phone_id);
        password=findViewById(R.id.password_id);
        register=findViewById(R.id.button_register);
        progressBar=findViewById(R.id.progressBar2);

        back.setOnClickListener(goBack);
        register.setOnClickListener(onRegister);
        mAuth = FirebaseAuth.getInstance();
    }

    private View.OnClickListener goBack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            intent = new Intent(Register.this, Login.class);
            startActivity(intent);
        }
    };
    private View.OnClickListener onRegister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            registerUser();
        }
    };

    private void registerUser() {
        String nameStr = name.getText().toString().trim();
        String emailStr = email.getText().toString().trim();
        String phoneStr = phone.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        if (nameStr.isEmpty()) {
            name.setError("Name field is required!");
            name.requestFocus();
            return;
        }
        if (emailStr.isEmpty()) {
            email.setError("Email field is required!");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            email.setError("Enter a valid Email!");
            email.requestFocus();
            return;
        }
        if (phoneStr.isEmpty()) {
            phone.setError("Phone field is required!");
            phone.requestFocus();
            return;
        }
        if (phoneStr.length() < 8) {
            phone.setError("Enter a valid phone number!");
            phone.requestFocus();
            return;
        }
        if (passwordStr.isEmpty()) {
            password.setError("Password field is required!");
            password.requestFocus();
            return;
        }
        if (passwordStr.length() < 6) {
            password.setError("Minimum password length is 6 characters!");
            password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.VISIBLE);
                            User user = new User(nameStr, emailStr, phoneStr);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(Register.this,"User has been registered successfully!",Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        intent = new Intent(Register.this, Login.class);
                                        startActivity(intent);

                                        // redirect to log in activity
                                    } else {
                                        Toast.makeText(Register.this,"Failed to register2! Try again!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(Register.this,"Failed to register! Email is already being used!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }


}