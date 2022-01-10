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
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private TextView register,forgot;
    private EditText email,password;
    private Button signIn;
    private Intent intent;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        register=findViewById(R.id.register_login);
        forgot=findViewById(R.id.forgot_login);
        signIn=findViewById(R.id.button_login);
        email=findViewById(R.id.email_id2);
        password=findViewById(R.id.password_id2);
        progressBar=findViewById(R.id.progressBar);
        mAuth=FirebaseAuth.getInstance();

        register.setOnClickListener(onNewRegister);
        forgot.setOnClickListener(onForgot);
        signIn.setOnClickListener(onLogin);
    }

    private View.OnClickListener onNewRegister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        }
    };
    private View.OnClickListener onForgot = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            intent = new Intent(Login.this, ForgotPassword.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener onLogin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            userLogin();
        }
    };

    private void userLogin(){
        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        if (emailStr.isEmpty()) {
            email.setError("Name field is required!");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            email.setError("Enter a valid Email!");
            email.requestFocus();
            return;
        }
        if (passwordStr.isEmpty()) {
            password.setError("Email field is required!");
            password.requestFocus();
            return;
        }
        if (passwordStr.length() < 6) {
            password.setError("Minimum password length is 6 characters!");
            password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            if(user.isEmailVerified()) {
                                //redirect to home page
                                progressBar.setVisibility(View.GONE);
                                intent = new Intent(Login.this, Profile.class);
                                startActivity(intent);
                            }else {
                                user.sendEmailVerification();
                                Toast.makeText(Login.this,"Check your email to verify your account!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(Login.this, "Invalid Credentials!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }
}