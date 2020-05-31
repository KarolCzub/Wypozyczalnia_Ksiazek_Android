package com.example.karol.bookrental;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class MyOffersActivity extends AppCompatActivity {
    public FirebaseFirestore db;
    private ArrayList<String[]> list = new ArrayList<>();
    private MyCustomAdapter_MyOffers adapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_offers);
        db = FirebaseFirestore.getInstance();

        CollectionReference offersCollectionRef = db.collection("offers");
        //instantiate custom adapter
        adapter = new MyCustomAdapter_MyOffers(list, this);

        //handle listview and assign adapter
        ListView lView = (ListView)findViewById(R.id.listaKsiazek);
        lView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String current_id = "";
        if(currentUser!=null){
            current_id = currentUser.getUid();
        }
        if(current_id != ""){

            Query categoryQuery = offersCollectionRef.whereEqualTo("user_id", current_id);
            categoryQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document: task.getResult()){
                            Map<String, Object> offer = document.getData();
                            String[] tabString = new String[8];
                            tabString[0]= offer.get("title").toString();
                            tabString[1]="Zainteresowany: " + offer.get("wanted_category");
                            tabString[2]="Miejscowość: " + offer.get("city");
                            tabString[3]= offer.get("user_id").toString();
                            tabString[4]=offer.get("additional_info").toString();
                            tabString[5]=offer.get("user_email").toString();
                            tabString[6]=offer.get("offer_id").toString();
                            tabString[7]=offer.get("category").toString();
                            list.add(tabString);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }
}
