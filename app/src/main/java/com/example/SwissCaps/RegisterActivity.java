package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {


    EditText regEmail;
    EditText regPassword;
    Button registerBtn;
    TextView signIn;
    FirebaseAuth fireAuth;
    Switch regUserType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fireAuth = FirebaseAuth.getInstance();

        regEmail = findViewById(R.id.email);
        regPassword = findViewById(R.id.password);
        registerBtn = findViewById(R.id.registerBtn);
        signIn = findViewById(R.id.signinHere);
        String sourceString ="Are you registered ? <b> Sign In</b> Now";
        signIn.setText(Html.fromHtml(sourceString));
        regUserType = findViewById(R.id.switch1);
        registerBtn.setOnClickListener(view -> {
            createUser();

        });
        signIn.setOnClickListener(View -> {
            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
        });
    }

    private void createUser(){
        String email = regEmail.getText().toString().trim();
        String password = regPassword.getText().toString().trim();
        final String userType = regUserType.isChecked() ? "premium" : "standard";
        //"premium"; // or set user type based on user input or some other way

        if(TextUtils.isEmpty(email)){
            regEmail.setError("El correo electrónico no puede estar vacío");
            regEmail.requestFocus();
        }else if(TextUtils.isEmpty(password)){
            regPassword.setError("La palabra clave no puede estar vacía");
            regPassword.requestFocus();
        }else{
            fireAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser user = task.getResult().getUser();
                        String userId = user.getUid();

                        // Store user type in Firebase Database
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference userRef = database.getReference("users").child(userId);
                        userRef.child("userType").setValue(userType);

                        Toast.makeText(RegisterActivity.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    }else{
                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}