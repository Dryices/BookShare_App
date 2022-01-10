package com.sp.bookshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    private TextView register;
    private TextView forgot;
    private Button login;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        register=findViewById(R.id.register_login);
        forgot=findViewById(R.id.forgot_login);
        login=findViewById(R.id.button_login);

        register.setOnClickListener(onNewRegister);
    }

    private View.OnClickListener onNewRegister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            intent = new Intent(Login.this, com.sp.bookshare.Register.class);
            startActivity(intent);
        }
    };
}