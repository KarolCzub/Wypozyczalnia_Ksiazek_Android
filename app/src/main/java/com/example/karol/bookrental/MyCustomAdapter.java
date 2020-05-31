package com.example.karol.bookrental;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MyCustomAdapter extends BaseAdapter implements ListAdapter {

    public static final String DATA = "data";

    private ArrayList<String[]> list = new ArrayList<String[]>();
    private Context context;

    public MyCustomAdapter(ArrayList<String[]> list, Context context) {
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

        kontaktPrzycisk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OfferDetails.class);
                intent.putStringArrayListExtra(DATA, new ArrayList<>(Arrays.asList(list.get(position))));
                context.startActivity(intent);
            }
        });

        return view;
    }
}