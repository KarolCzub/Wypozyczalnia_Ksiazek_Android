package com.example.karol.bookrental;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class OfferDetails extends AppCompatActivity {

    public Button cofnij;
    public TextView tytul;
    public TextView kategoria;
    public TextView miasto;
    public TextView opis;
    public TextView kontakt;
    public TextView zainteresowany;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_details);

        cofnij = (Button) findViewById(R.id.cofnij);
        tytul = (TextView) findViewById(R.id.tytul);
        kategoria = (TextView) findViewById(R.id.kategoria);
        miasto = (TextView) findViewById(R.id.miasto);
        opis = (TextView) findViewById(R.id.opis);
        kontakt = (TextView) findViewById(R.id.kontakt);
        zainteresowany = (TextView) findViewById(R.id.zainteresowany);

        List<String> dane = (List) getIntent().getExtras().get(MyCustomAdapter.DATA);
        tytul.setText("Tytul: " + dane.get(0));
        zainteresowany.setText(dane.get(1));
        miasto.setText(dane.get(2));
        opis.setText("Opis: " + dane.get(4));
        kontakt.setText("Kontakt: " + dane.get(5));
        kategoria.setText("Kategoria: " + dane.get(6));


        cofnij.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
