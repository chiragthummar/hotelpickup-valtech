package com.valtech.movenpick.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.valtech.movenpick.R;
import com.valtech.movenpick.model.CountryModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chira on 1/29/2016.
 */
public class ImageThumbnailAdapter extends BaseAdapter {

    private Context context;
    LayoutInflater inflater;
    int flag=0;

    private static double TENSION = 800;
    private static double DAMPER = 20; //friction




    private List<CountryModel> data=new ArrayList<>();

    public ImageThumbnailAdapter (Context context, List<CountryModel> data) {
        this.context = context;
        this.data=data;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {



        if (convertView == null) {
            convertView = inflater.inflate(R.layout.library_grid_item, null);
        }

        ImageView imgv_image = (ImageView) convertView.findViewById(R.id.imgv_image);
        TextView txt_country_name = (TextView) convertView.findViewById(R.id.txt_country_name);

        imgv_image.setImageResource(data.get(position).getContry_flag());
        txt_country_name.setText(data.get(position).getCountry_name());

        String fontPath2 = "fonts/coveslight.otf";
        Typeface tf2 = Typeface.createFromAsset(context.getAssets(), fontPath2);

        txt_country_name.setTypeface(tf2);

        final View cView=convertView;


        return convertView;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


   


}
