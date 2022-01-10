package com.sp.bookshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText email;
    private Button reset;
    private ProgressBar progressBar;
    private Intent intent;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().hide();

        email=findViewById(R.id.email_id);
        reset=findViewById(R.id.button_reset);
        progressBar=findViewById(R.id.progressBar3);

        auth=FirebaseAuth.getInstance();
        reset.setOnClickListener(onReset);
    }

    private View.OnClickListener onReset = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            resetPassword();
        }
    };

    private void resetPassword() {
        String emailStr = email.getText().toString().trim();

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

        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(emailStr).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPassword.this,"Please check your email to reset your password",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    intent = new Intent(ForgotPassword.this, Login.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(ForgotPassword.this,"An error has occurred!",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }
}