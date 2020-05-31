package com.example.karol.bookrental;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;

public class Menu extends AppCompatActivity {

    public static final String KATEGORIE = "kategorie";
    public static final String FANTASTYKA = "fantastyka";
    public static final String HORROR = "horror";
    public static final String SENSACYJNE = "sensacyjne";
    public static final String ROMANS = "romans";
    public static final String KRYMINAL = "kryminal";
    public static final String INNE = "inne";
    public static final String TYTUL = "tytul";

    private boolean fantastyka;
    private boolean horror;
    private boolean sensacyjne;
    private boolean romans;
    private boolean inne;
    private boolean kryminal;

    public Button przyciskSzukaj;
    public Button przyciskDodaj;
    public Button profileButton;
    public EditText tytul;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        przyciskSzukaj = (Button) findViewById(R.id.szukajPrzycisk);
        przyciskDodaj = (Button) findViewById(R.id.dodajPrzycisk);
        profileButton = (Button) findViewById(R.id.profileButton);
        tytul = (EditText) findViewById(R.id.tytul);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("MyTokken", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                       // String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("MyTokken", token);
                        //Toast.makeText(Menu.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

        przyciskSzukaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fantastyka = ((CheckBox) findViewById(R.id.fantastykaCheck)).isChecked();
                horror = ((CheckBox) findViewById(R.id.horrorCheck)).isChecked();
                sensacyjne = ((CheckBox) findViewById(R.id.sensacyjneCheck)).isChecked();
                romans = ((CheckBox) findViewById(R.id.romansCheck)).isChecked();
                inne = ((CheckBox) findViewById(R.id.inneCheck)).isChecked();
                kryminal = ((CheckBox) findViewById(R.id.kryminalCheck)).isChecked();
                String nazwaKsiazki = tytul != null && tytul.getText() != null ? tytul.getText().toString() : "";
                if (fantastyka || horror || sensacyjne || romans || inne || kryminal || !nazwaKsiazki.isEmpty()) {
                    Intent toList = new Intent(getApplicationContext(), ListaKsiazek.class);
                    ArrayList<String> kategorie = dodajKategorieDoList();
                    if (!kategorie.isEmpty())
                        toList.putStringArrayListExtra(KATEGORIE, kategorie);
                    if (!nazwaKsiazki.isEmpty())
                        toList.putExtra(TYTUL, nazwaKsiazki);
                    startActivity(toList);
                } else {
                    Toast.makeText(getApplicationContext(), "Musisz podać tytuł lub wybrać kategorie!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        przyciskDodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DodajKsiazke.class));
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });
    }

    private ArrayList<String> dodajKategorieDoList() {
        ArrayList<String> lista = new ArrayList<>();

        if (fantastyka)
            lista.add(FANTASTYKA);
        if (horror)
            lista.add(HORROR);
        if (sensacyjne)
            lista.add(SENSACYJNE);
        if (romans)
            lista.add(ROMANS);
        if (inne)
            lista.add(INNE);
        if (kryminal)
            lista.add(KRYMINAL);

        return lista;
    }
}
