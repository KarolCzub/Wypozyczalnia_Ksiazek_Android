package com.example.karol.bookrental;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class MyCustomAdapter_MyOffers extends BaseAdapter implements ListAdapter {

    private ArrayList<String[]> list = new ArrayList<String[]>();
    private Context context;

    public MyCustomAdapter_MyOffers(ArrayList<String[]> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        //return list.get(pos).getId();
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, null);
        }

        TextView nazwa = (TextView)view.findViewById(R.id.nazwa);
        nazwa.setText(list.get(position)[0]);
        TextView zainteresowany = (TextView)view.findViewById(R.id.zainteresowany);
        zainteresowany.setText(list.get(position)[1]);
        TextView miejscowowsc = (TextView)view.findViewById(R.id.miejscowosc);
        miejscowowsc.setText(list.get(position)[2]);

        //Handle buttons and add onClickListeners
        Button kontaktPrzycisk = (Button)view.findViewById(R.id.kontaktPrzycisk);
        kontaktPrzycisk.setText("Usuń");


        kontaktPrzycisk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String[] positionInfo = list.get(position);
                FirebaseFirestore.getInstance().collection("offers").document(positionInfo[6])
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                String category = positionInfo[7];
                String wanted = positionInfo[1];
                unsubscribeFromTopic(category, wanted);
                list.remove(position);
                notifyDataSetChanged();
            }
        });

        return view;
    }

    private void unsubscribeFromTopic(String category, String wanted) {
        if (wanted.contains(Menu.FANTASTYKA)) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(category + "_" + Menu.FANTASTYKA);
        }
        if (wanted.contains(Menu.ROMANS)) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(category + "_" + Menu.ROMANS);
        }
        if (wanted.contains(Menu.HORROR)) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(category + "_" + Menu.HORROR);
        }
        if (wanted.contains(Menu.SENSACYJNE)) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(category + "_" + Menu.SENSACYJNE);
        }
        if (wanted.contains(Menu.KRYMINAL)) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(category + "_" + Menu.KRYMINAL);
        }
        if (wanted.contains(Menu.INNE)) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(category + "_" + Menu.INNE);
        }
    }

}