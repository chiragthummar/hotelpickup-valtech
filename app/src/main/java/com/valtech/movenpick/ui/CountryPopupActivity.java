package com.valtech.movenpick.ui;

import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.valtech.movenpick.R;
import com.valtech.movenpick.adapter.ImageThumbnailAdapter;
import com.valtech.movenpick.other.GridViewCompat;
import com.valtech.movenpick.util.PreferencesUtil;

public class CountryPopupActivity extends AppCompatActivity {

    private GridViewCompat image_grid;
    private TextView txt_select;
    private ImageView imgv_close;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_popup);
        image_grid = (GridViewCompat) findViewById(R.id.image_grid);
        txt_select = (TextView) findViewById(R.id.txt_select);
        imgv_close = (ImageView) findViewById(R.id.imgv_close);


        String fontPath2 = "fonts/coveslight.otf";
        Typeface tf2 = Typeface.createFromAsset(getAssets(), fontPath2);

        txt_select.setTypeface(tf2);


        image_grid.setLayoutAnimation(new GridLayoutAnimationController(AnimationUtils.loadAnimation(CountryPopupActivity.this, R.anim.grid_item_fadein), LayoutAnimationController.ORDER_NORMAL, GridLayoutAnimationController.PRIORITY_ROW));
        image_grid.setLayoutAnimation(new GridLayoutAnimationController(AnimationUtils.loadAnimation(CountryPopupActivity.this, R.anim.grid_item_fadein), 0.2f, 0.2f));

        image_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PreferencesUtil.putInt(CountryPopupActivity.this,"SelectedCountry",position);

                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        ImageThumbnailAdapter mCustomBookAdapter = new ImageThumbnailAdapter(CountryPopupActivity.this, PickupCarScreen.data);
        image_grid.setAdapter(mCustomBookAdapter);

        imgv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

    }
}
