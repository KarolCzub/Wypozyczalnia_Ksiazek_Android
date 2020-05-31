package com.example.karol.bookrental;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DodajKsiazke extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private boolean fantastyka;
    private boolean horror;
    private boolean sensacyjne;
    private boolean romans;
    private boolean inne;
    private boolean kryminal;
    private String typKsiazki;

    public Button dodaj;
    public Button cofnij;
    public EditText tytul;
    public EditText extra;
    public EditText city;
    public Spinner typ;
    public FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_ksiazke);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        dodaj = (Button) findViewById(R.id.zatwierdzPrzycisk);
        cofnij = (Button) findViewById(R.id.cofnijPrzycisk);
        tytul = (EditText) findViewById(R.id.tytul);
        extra = (EditText) findViewById(R.id.extraInfo);
        city = (EditText) findViewById(R.id.cityEditText);
        typ = (Spinner) findViewById(R.id.typ);

        typ.setOnItemSelectedListener(this);
        List<String> kategorie = new ArrayList<String>();
        kategorie.add(Menu.HORROR);
        kategorie.add(Menu.KRYMINAL);
        kategorie.add(Menu.SENSACYJNE);
        kategorie.add(Menu.ROMANS);
        kategorie.add(Menu.FANTASTYKA);
        kategorie.add(Menu.INNE);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, kategorie);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        typ.setAdapter(dataAdapter);

        dodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fantastyka = ((CheckBox) findViewById(R.id.fantastykaCheck)).isChecked();
                horror = ((CheckBox) findViewById(R.id.horrorCheck)).isChecked();
                sensacyjne = ((CheckBox) findViewById(R.id.sensacyjneCheck)).isChecked();
                romans = ((CheckBox) findViewById(R.id.romansCheck)).isChecked();
                inne = ((CheckBox) findViewById(R.id.inneCheck)).isChecked();
                kryminal = ((CheckBox) findViewById(R.id.kryminalCheck)).isChecked();
                if (tytul.getText() == null || tytul.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(), "Dodaj tytul ksiazki!",
                            Toast.LENGTH_LONG).show();
                else if (!fantastyka && !horror && !sensacyjne && !romans && !inne && !kryminal) {
                    Toast.makeText(getApplicationContext(), "Wybierz chciana kategorie!",
                            Toast.LENGTH_LONG).show();
                } else {
                    String wanted = createWanted(fantastyka, horror, sensacyjne, romans, inne, kryminal);

                    DocumentReference ref = db.collection("offers").document();
                    String offerId = ref.getId();

                    Map<String, Object> offer = new HashMap<>();
                    if (currentUser != null){
                        offer.put("user_id", currentUser.getUid());
                        offer.put("user_email", currentUser.getEmail());}
                    offer.put("title", tytul.getText().toString());
                    offer.put("category", typKsiazki);
                    offer.put("wanted_category", wanted);
                    offer.put("additional_info", extra.getText().toString());
                    offer.put("city", city.getText().toString());
                    offer.put("offer_id", offerId);

                    db.collection("offers").document(offerId)
                            .set(offer)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //Log.w(TAG, "Error adding document", e);
                                }
                            });
                    setNotifications(typKsiazki, fantastyka, horror, sensacyjne, romans, inne, kryminal);
                    sendNotifications(typKsiazki, fantastyka, horror, sensacyjne, romans, inne, kryminal, offerId, tytul.getText().toString(), wanted);
                    onBackPressed();
                }
            }
        });

        cofnij.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private String createWanted(boolean fantastyka, boolean horror, boolean sensacyjne, boolean romans, boolean inne, boolean kryminal) {
        String wanted = "";
        if (fantastyka)
            wanted += Menu.FANTASTYKA + "; ";
        if (horror)
            wanted += Menu.HORROR + "; ";
        if (sensacyjne)
            wanted += Menu.SENSACYJNE + "; ";
        if (romans)
            wanted += Menu.ROMANS + "; ";
        if (inne)
            wanted += Menu.INNE + "; ";
        if (kryminal)
            wanted += Menu.KRYMINAL + "; ";

        return wanted;
    }

    public void setNotifications(String typKsiazki, boolean fantastyka, boolean horror, boolean sensacyjne, boolean romans, boolean inne, boolean kryminal){

        if (fantastyka) {
            FirebaseMessaging.getInstance().subscribeToTopic(typKsiazki + "_" + Menu.FANTASTYKA)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = Menu.FANTASTYKA + " subscribed";
                            if (!task.isSuccessful()) {
                                msg = Menu.FANTASTYKA + " subscribed failed";
                            }
                            Log.d("Subrcription", msg);
                            Toast.makeText(DodajKsiazke.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        if (horror) {
            FirebaseMessaging.getInstance().subscribeToTopic(typKsiazki + "_" + Menu.HORROR)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = Menu.HORROR + " subscribed";
                            if (!task.isSuccessful()) {
                                msg = Menu.HORROR + " subscribed failed";
                            }
                            Log.d("Subrcription", msg);
                            Toast.makeText(DodajKsiazke.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        if (sensacyjne) {
            FirebaseMessaging.getInstance().subscribeToTopic(typKsiazki + "_" + Menu.SENSACYJNE)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = Menu.SENSACYJNE + " subscribed";
                            if (!task.isSuccessful()) {
                                msg = Menu.SENSACYJNE + " subscribed failed";
                            }
                            Log.d("Subrcription", msg);
                            Toast.makeText(DodajKsiazke.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        if (romans) {
            FirebaseMessaging.getInstance().subscribeToTopic(typKsiazki + "_" + Menu.ROMANS)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = Menu.ROMANS + " subscribed";
                            if (!task.isSuccessful()) {
                                msg = Menu.ROMANS + " subscribed failed";
                            }
                            Log.d("Subscription", msg);
                            Toast.makeText(DodajKsiazke.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        if (inne) {
            FirebaseMessaging.getInstance().subscribeToTopic(typKsiazki + "_" + Menu.INNE)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = Menu.INNE + " subscribed";
                            if (!task.isSuccessful()) {
                                msg = Menu.INNE + " subscribed failed";
                            }
                            Log.d("Subrcription", msg);
                            Toast.makeText(DodajKsiazke.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        if (kryminal) {
            FirebaseMessaging.getInstance().subscribeToTopic(typKsiazki + "_" + Menu.KRYMINAL)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = Menu.KRYMINAL + " subscribed";
                            if (!task.isSuccessful()) {
                                msg = Menu.KRYMINAL + " subscribed failed";
                            }
                            Log.d("Subrcription", msg);
                            Toast.makeText(DodajKsiazke.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void sendNotifications(String typKsiazki, boolean fantastyka, boolean horror, boolean sensacyjne, boolean romans, boolean inne, boolean kryminal, String offer_id, String title, String wanted){
        if (fantastyka){
            sendNotificationToUser(Menu.FANTASTYKA + "_" + typKsiazki, "Nowa ksiazka z kat. : " + typKsiazki, offer_id, title, wanted);
        }
        if (horror){
            sendNotificationToUser(Menu.HORROR + "_" + typKsiazki, "Nowa ksiazka z kat. : " + typKsiazki, offer_id, title, wanted);
        }
        if (sensacyjne){
            sendNotificationToUser(Menu.SENSACYJNE + "_" + typKsiazki, "Nowa ksiazka z kat. : " + typKsiazki, offer_id, title, wanted);
        }
        if (romans){
            sendNotificationToUser(Menu.ROMANS + "_" + typKsiazki, "Nowa ksiazka z kat. : " + typKsiazki, offer_id, title, wanted);
        }
        if (inne){
            sendNotificationToUser(Menu.INNE + "_" + typKsiazki, "Nowa ksiazka z kat. : " + typKsiazki, offer_id, title, wanted);
        }
        if (kryminal){
            sendNotificationToUser(Menu.KRYMINAL + "_" + typKsiazki, "Nowa ksiazka z kat. : " + typKsiazki, offer_id, title, wanted);
        }
    }

    public static void sendNotificationToUser(String topic, final String message, String offer_id, String title, String wanted) {
        DatabaseReference databaseNotifications = FirebaseDatabase.getInstance().getReference("/notifications");

        Map notification = new HashMap<>();
        notification.put("topic", topic);
        notification.put("n_title", message);
        notification.put("id", offer_id);
        notification.put("n_message", "Tytul: " + title + " | Szukane: " + wanted);

        databaseNotifications.push().setValue(notification);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.typKsiazki = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
