package com.example.karol.bookrental;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    public static final String OFFER_ID = "offer_id";

    public static String id;

    public Button przyciskRejestruj;
    public Button przyciskLoguj;
    public EditText emailEditText;
    public EditText passwordEditText;
    TextView forgotPasswordTextView;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;

    protected void onStart() {
        super.onStart();
/*        registerWithNotificationHubs();
        MyFirebaseMessagingService.createChannelAndHandleNotifications(getApplicationContext());*/

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().get(OFFER_ID) != null) {
                Intent intent = new Intent(getApplicationContext(), ListaKsiazek.class);
                intent.putExtra(OFFER_ID, getIntent().getExtras().get(OFFER_ID).toString());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        przyciskRejestruj = (Button) findViewById(R.id.przyciskRejestruj);
        przyciskLoguj = (Button) findViewById(R.id.przyciskLoguj);
        emailEditText = (EditText) findViewById(R.id.email);
        passwordEditText = (EditText) findViewById(R.id.haslo);
        forgotPasswordTextView = (TextView) findViewById(R.id.forgotPasswordTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        przyciskRejestruj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Rejestracja.class));
            }
        });

        przyciskLoguj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();

                if(email.isEmpty()) {
                    emailEditText.setError(getString(R.string.email_required));
                    emailEditText.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailEditText.setError(getString(R.string.error_invalid_email));
                    emailEditText.requestFocus();
                    return;
                }

                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(Login.this, R.string.password_send_to_email, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void loginUser(){
/*        Register reg = new Register();
        reg.username = "userA";
        reg.password = "passA";
        Gson gson = new Gson();
        String json = gson.toJson(reg);
        JsonTaskPost logowanie = new JsonTaskPost(Login.this);
        logowanie.execute("http://78c54675.ngrok.io/register", json);
        try {
            logowanie.get(10000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String odpowiedz = logowanie.odpowiedz;*/

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Please enter a valid email");
            emailEditText.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        if(password.length()<6){
            passwordEditText.setError("Minimum length of password should be 6");
            passwordEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), Menu.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
            } else {
                Log.i("Login", "This device is not supported by Google Play Services.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void registerWithNotificationHubs()
    {
        if (checkPlayServices()) {
            // Start IntentService to register this application with FCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }
}
