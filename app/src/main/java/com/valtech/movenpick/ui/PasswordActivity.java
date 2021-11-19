package com.valtech.movenpick.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.valtech.movenpick.R;
import com.valtech.movenpick.db.HotelPickupDatabase;
import com.valtech.movenpick.util.HotelPickupUtility;

public class PasswordActivity extends AppCompatActivity {

    EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtPassword.getText().toString().equals("password")) {
                    startActivity(new Intent(PasswordActivity.this, SettingsScreen.class));
                    finish();
                } else {

                    Toast.makeText(PasswordActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        init();


    }

    private void init() {
        HotelPickupDatabase.init(this);
        Typeface font = HotelPickupUtility.getDefaultFont(this);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitle((CharSequence) "Login");
        toolBar.setTitleTextColor(Color.parseColor("#263238"));
        //   toolBar.setBackgroundColor(Color.parseColor("#c0c0c0"));
        setSupportActionBar(toolBar);



    }


}
