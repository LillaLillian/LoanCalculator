package com.example.loancalculator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class TerminAdapter extends ArrayAdapter<Termin> {
    private int layoutResource;

    public TerminAdapter(Context context, int layoutResource, List<Termin> items) {
        super(context, layoutResource, items);
        this.layoutResource = layoutResource;
    }

    //    //NB!! Denne gjør at elementene i lista tegnes som ikoner.
//    @Override
//    public View getDropDownView(int position, View convertView, ViewGroup parent) {
//        return getBookView(position, convertView, parent);
//    }
//
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getTerminView(position, convertView, parent);
    }

    public View getTerminView(int position, View convertView, ViewGroup parent) {

        Termin termin = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(this.layoutResource, parent, false);
        }

        TextView tvYear = (TextView) convertView.findViewById(R.id.tvListYear);
        TextView tvTotalPayment = (TextView) convertView.findViewById(R.id.tvListTotalPayment);
        TextView tvInterests = (TextView) convertView.findViewById(R.id.tvListInterests);
        TextView tvPrincipal = (TextView) convertView.findViewById(R.id.tvListPrincipal);
        TextView tvRemainingDebt = (TextView) convertView.findViewById(R.id.tvListRemaining);

        tvYear.setText(String.format("%d", termin.getYear()));
        tvTotalPayment.setText(String.format("%.2f", termin.getTotalPayment()));
        tvInterests.setText(String.format("%.2f", termin.getInterests()));
        tvPrincipal.setText(String.format("%.2f", termin.getPrincipal()));
        tvRemainingDebt.setText(String.format("%.2f", termin.getRemainingDebt()));


        return convertView;
    }
}
