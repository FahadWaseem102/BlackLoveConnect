package com.swagger.blackloveconnect.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swagger.blackloveconnect.main.InboxActivity;
import com.swagger.blackloveconnect.main.MainActivity;
import com.swagger.blackloveconnect.R;
import com.swagger.blackloveconnect.models.UsersModel;

import static com.swagger.blackloveconnect.utils.Constants.usersModelList;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout email, password;
    private FirebaseAuth mAuth;
    private DatabaseReference usersReference;
    boolean userFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindView();

    }

    void bindView(){
        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);
        Button login = findViewById(R.id.login_button);

        mAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference("Users");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClicked();
            }
        });
    }

    private void loginClicked() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Logging In...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String st_email = email.getEditText().getText().toString().trim();
        String st_password = password.getEditText().getText().toString().trim();

        if (st_email.isEmpty() || st_password.isEmpty()) {
            Toast.makeText(this, "Enter Email and Password", Toast.LENGTH_SHORT).show();
        }
        else {
            mAuth.signInWithEmailAndPassword(st_email, st_password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        progressDialog.dismiss();
                                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                            UsersModel usersModel = dataSnapshot.getValue(UsersModel.class);
                                            if (usersModel.getUid().equals(mAuth.getCurrentUser().getUid())){
                                                userFound = true;
                                                break;
                                            }
                                        }

                                        if (userFound){
                                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else {
                                            mAuth.signOut();
                                            Toast.makeText(LoginActivity.this, "Login Failed !", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                // If sign in fails, display a message to the user.
                                progressDialog.dismiss();
                                Log.w("TAG", "signInWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }
    }

    public void signUp(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }

    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public void Clicked(View view) {
        Intent intent = new Intent(getApplicationContext(),ForgotPasswordActivity.class);
        startActivity(intent);
    }
}
