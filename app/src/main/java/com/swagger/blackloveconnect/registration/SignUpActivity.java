package com.swagger.blackloveconnect.registration;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.swagger.blackloveconnect.main.MainActivity;
import com.swagger.blackloveconnect.R;
import com.swagger.blackloveconnect.models.UsersModel;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout name, email, password;
    private Button register;

    private FirebaseAuth mAuth;
    private DatabaseReference usesrsRef;


    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    private String currentUserID;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.progress);
        dialog = builder.create();



        name = findViewById(R.id.name);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        register = findViewById(R.id.signup_button);


        mAuth = FirebaseAuth.getInstance();

        usesrsRef = FirebaseDatabase.getInstance().getReference().child("Users");

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerClicked();
            }
        });


    }

    public void registerClicked() {

       String st_name = name.getEditText().getText().toString();
       String st_email = email.getEditText().getText().toString();
       String st_password = password.getEditText().getText().toString();

        if (st_name.isEmpty() || st_email.isEmpty() || st_password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "All fields are Mandatory", Toast.LENGTH_SHORT).show();
        } else {

            dialog.show();

            registerUser(st_email, st_password, st_name);
        }

    }

    private void registerUser(final String stEmail, final String stPassword, final String name) {

        mAuth.createUserWithEmailAndPassword(stEmail, stPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");

                            UsersModel usersModel = new UsersModel();
                            usersModel.setUserName(name);
                            usersModel.setEmail(stEmail);
                            usersModel.setUid(mAuth.getCurrentUser().getUid());

                            usesrsRef.child(mAuth.getCurrentUser().getUid()).setValue(usersModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());


                            Toast.makeText(getApplicationContext(), task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            dialog.dismiss();

                        }
                    }
                });


    }

    public void loginClicked(View view) {
        Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

