package com.example.mtsihr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthorizationActivity extends AppCompatActivity {

    private static final String TAG = "AUTH TEST: ";
    private DatabaseReference mRef;
    private Button bAuth, bRegistration;
    private EditText etEmail, etPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        getSupportActionBar().setTitle("MTS iHR | Авторизация");
        mRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://diploma-ihr.firebaseio.com/Users");
        mAuth = FirebaseAuth.getInstance();
        initElements();
    }

    private void initElements() {
        bAuth = (Button)findViewById(R.id.auth_butt);
        bRegistration = (Button)findViewById(R.id.registration_butt);
        etEmail = (EditText)findViewById(R.id.email_et);
        etPassword = (EditText)findViewById(R.id.pass_et);

        bAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mRefChild = mRef.child("Login");
                if (!etEmail.getText().toString().isEmpty()) {
                    String login = etEmail.getText().toString();
                    mRefChild.setValue(login);

                    mAuthListener = new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                // User is signed in
                                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                            } else {
                                // User is signed out
                                Log.d(TAG, "onAuthStateChanged:signed_out");
                            }
                        }
                    };

                    mAuth.signInWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                            .addOnCompleteListener(AuthorizationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "signInWithEmail:failed", task.getException());
                                        Toast.makeText(AuthorizationActivity.this, "Ошибка авторизации! Email или пароль неверны!",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Intent intent = new Intent(AuthorizationActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });

                }
            }
        });

        bRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                        .addOnCompleteListener(AuthorizationActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "createUserWithEmailAndPassword:failed", task.getException());
                                    Toast.makeText(AuthorizationActivity.this, "Ошибка регистрации, проверьте правильность ввода данных.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AuthorizationActivity.this, "Вы успешно зарегестрированы! Войдите в аккаунт под своим email и паролем.",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
