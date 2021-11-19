package com.valtech.movenpick;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.valtech.movenpick.db.HotelPickupDatabase;
import com.valtech.movenpick.dialog.DialogBaseUrlConfig;
import com.valtech.movenpick.manager.UserSettingsManager;
import com.valtech.movenpick.manager.Utility;
import com.valtech.movenpick.ui.PasswordActivity;
import com.valtech.movenpick.ui.PickupCarScreen;
import com.valtech.movenpick.ui.ToolMenuActivity;
import com.valtech.movenpick.util.HotelPickupUtility;
import com.valtech.movenpick.util.PreferencesUtil;

import java.io.File;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imgLogo;
    //EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //  txtPassword = (EditText) findViewById(R.id.txtPassword);
        init();
    }

    private void init() {
        HotelPickupDatabase.init(this);
        Typeface font = HotelPickupUtility.getDefaultFont(this);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitle((CharSequence) "Hotel Pickup");
        toolBar.setTitleTextColor(Color.parseColor("#263238"));
        //   toolBar.setBackgroundColor(Color.parseColor("#c0c0c0"));
        setSupportActionBar(toolBar);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
        startButton.setTypeface(font);
        initCustomLogo();
    }

    public void initCustomLogo() {
        try {

            String path = new Utility(SplashActivity.this).getLogoURI();


            if (!path.isEmpty()) {

                File mediaStorageDir = new File(path);
                BitmapFactory.Options options = new BitmapFactory.Options();

                // downsizing image as it throws OutOfMemory Exception for larger
                // images
                options.inSampleSize = 8;

                Uri uri = Uri.fromFile(mediaStorageDir);
                final Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(),
                        options);

                imgLogo.setImageBitmap(bitmap);
            } else {
                imgLogo.setImageResource(R.drawable.app_main_logo);
            }


        } catch (Exception exc) {
            imgLogo.setImageResource(R.drawable.app_main_logo);
            exc.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.toolsMenu) {
            startActivity(new Intent(this, ToolMenuActivity.class));
            return true;
        }

        if (item.getItemId() == R.id.settingsMenu) {
            try {
                String apiURL = UserSettingsManager.getUserSettings(this).getApiURL();

                Log.e("apiURL", "" + apiURL);

                String baseUrl = PreferencesUtil.getString(this, "BASE_URL", "");

                if (apiURL == null || apiURL.trim().length() == 0 && baseUrl.equalsIgnoreCase("")) {

                    DialogBaseUrlConfig dp = new DialogBaseUrlConfig(this);
                    dp.show();
                } else {
                    startActivity(new Intent(this, PasswordActivity.class));
                }
            } catch (Exception exc) {
                DialogBaseUrlConfig dp = new DialogBaseUrlConfig(this);
                dp.show();
            }
            return true;
        }

        return false;

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.startButton) {

            startActivity(new Intent(this, PickupCarScreen.class));
            finish();

        }
    }
}
