package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Button logoutBtn;
    FirebaseAuth fireAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logoutBtn=  findViewById(R.id.logoutBtn);
        fireAuth = FirebaseAuth.getInstance();

        logoutBtn.setOnClickListener(view -> {
            fireAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = fireAuth.getCurrentUser();

        if(user == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }
}