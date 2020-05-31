package com.example.karol.bookrental;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class ListaKsiazek extends AppCompatActivity {

    public Button dodaj;
    public Button menu;
    public FirebaseFirestore db;
    private ArrayList<String[]> list = new ArrayList<>();
    private MyCustomAdapter adapter;
    private static int endedQuery = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_ksiazek);
        db = FirebaseFirestore.getInstance();

        dodaj = (Button) findViewById(R.id.przyciskDodaj);
        menu = (Button) findViewById(R.id.przyciskMenu);
        ArrayList<String> kategorie = getIntent().getStringArrayListExtra(Menu.KATEGORIE);
        String tytul = getIntent().getStringExtra(Menu.TYTUL);

        CollectionReference offersCollectionRef = db.collection("offers");

        //instantiate custom adapter
        adapter = new MyCustomAdapter(list, this);

        //handle listview and assign adapter
        ListView lView = (ListView)findViewById(R.id.listaKsiazek);
        lView.setAdapter(adapter);
        String offerIdFromNotification = getIntent().getStringExtra(Login.OFFER_ID);
        if (offerIdFromNotification != null) {
            Query categoryQuery = offersCollectionRef.whereEqualTo("offer_id", offerIdFromNotification);
            categoryQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document: task.getResult()){
                            Map<String, Object> offer = document.getData();
                            String[] tabString = new String[7];
                            tabString[0]= offer.get("title").toString();
                            tabString[1]="Zainteresowany: " + offer.get("wanted_category");
                            tabString[2]="Miejscowość: " + offer.get("city");
                            tabString[3]= offer.get("user_id").toString();
                            tabString[4]=offer.get("additional_info").toString();
                            tabString[5]=offer.get("user_email").toString();
                            tabString[6]=offer.get("category").toString();
                            list.add(tabString);
                        }
                        if (list == null || list.isEmpty()) {
                            Intent intent = new Intent(getApplicationContext(), Menu.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            return;
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        } else if (tytul != null && !tytul.isEmpty()) {
            offersCollectionRef.get().addOnCompleteListener((task) -> {
                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot document: task.getResult()){
                        Map<String, Object> offer = document.getData();
                        String[] tabString = new String[7];
                        tabString[0]= offer.get("title").toString();
                        if (!tabString[0].contains(tytul))
                            continue;
                        if (kategorie != null && !kategorie.isEmpty() && !kategorie.contains(offer.get("category").toString()))
                            continue;
                        tabString[1]="Zainteresowany: " + offer.get("wanted_category");
                        tabString[2]="Miejscowość: " + offer.get("city");
                        tabString[3]= offer.get("user_id").toString();
                        tabString[4]=offer.get("additional_info").toString();
                        tabString[5]=offer.get("user_email").toString();
                        tabString[6]=offer.get("category").toString();
                        list.add(tabString);
                    }
                    if (list == null || list.isEmpty())
                        Toast.makeText(getApplicationContext(), "Brak pasujących wyników!",
                                Toast.LENGTH_LONG).show();
                    adapter.notifyDataSetChanged();
                }
            });
        }
        else if (kategorie != null && !kategorie.isEmpty()) {
            for (String e:kategorie) {
                /**JsonTask ksiazki = new JsonTask(ListaKsiazek.this);
                ksiazki.execute("http://80.211.135.57:8080/announcements/get/" + e);
                try {
                    ksiazki.get(10000, TimeUnit.MILLISECONDS);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                String odpowiedz = ksiazki.odpowiedz;
                try {
                    JSONArray jtab = new JSONArray(odpowiedz);
                    for (int i=0; i<jtab.length(); i++) {
                        String[] tabString = new String[6];
                        JSONObject jobj = (JSONObject) jtab.get(i);
                        tabString[0] = jobj.getString("title");
                        tabString[1] = "Zainteresowany: " + jobj.getString("wanted_category");
                        JsonTask uzyt = new JsonTask(ListaKsiazek.this);
                        uzyt.execute("http://80.211.135.57:8080/user/get/" + jobj.getString("user_id"));
                        try {
                            uzyt.get(10000, TimeUnit.MILLISECONDS);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        String uzytk = uzyt.odpowiedz;
                        JSONObject juzyt = new JSONObject(uzytk);
                        tabString[2] = "Miejscowosc: " + juzyt.getString("address");
                        tabString[3] = jobj.getString("user_id");
                        tabString[4] = jobj.getString("announcement_id");
                        tabString[5] = juzyt.getString("contactaddress");
                        list.add(tabString);
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }*/

                Query categoryQuery = offersCollectionRef.whereEqualTo("category", e);
                categoryQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document: task.getResult()){
                                Map<String, Object> offer = document.getData();
                                String[] tabString = new String[7];
                                tabString[0]= offer.get("title").toString();
                                tabString[1]="Zainteresowany: " + offer.get("wanted_category");
                                tabString[2]="Miejscowość: " + offer.get("city");
                                tabString[3]=offer.get("user_id").toString();
                                tabString[4]=offer.get("additional_info").toString();
                                tabString[5]=offer.get("user_email").toString();
                                tabString[6]=offer.get("category").toString();
                                list.add(tabString);
                            }
                            endedQuery++;
                            if (endedQuery == kategorie.size()) {
                                endedQuery = 0;
                                if (list == null || list.isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "Brak pasujących wyników!",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }


        dodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DodajKsiazke.class));
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Menu.class));
            }
        });
    }
}
