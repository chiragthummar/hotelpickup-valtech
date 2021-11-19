package com.valtech.movenpick.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.valtech.movenpick.R;

import java.util.List;

public class StringAdapter extends ArrayAdapter<String> {
    private Typeface font;
    private LayoutInflater inflater;
    private List<String> list;

    private class UMStringItemHolder {
        private TextView item;

        private UMStringItemHolder() {
            this.item = null;
        }
    }

    public StringAdapter(List<String> list, Context ctx) {
        super(ctx, R.layout.string_item, R.id.item, list);
        this.font = null;
        this.list = null;
        this.inflater = null;
        this.list = list;
        this.font = HotelPickupUtility.getDefaultFont(ctx);
        this.inflater = LayoutInflater.from(ctx);
    }

    public int getCount() {
        return this.list.size();
    }

    public int getViewTypeCount() {
        return 1;
    }

    public int getItemViewType(int position) {
        return position;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @SuppressLint({"InflateParams"})
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView item;
        String string = (String) getItem(position);
        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.string_item, null);
            item = (TextView) convertView.findViewById(R.id.item);
            UMStringItemHolder holder = new UMStringItemHolder();
            holder.item = item;
            convertView.setTag(holder);
        } else {
            item = ((UMStringItemHolder) convertView.getTag()).item;
        }
        item.setTypeface(this.font);
        item.setText(string);
        return convertView;
    }
}
