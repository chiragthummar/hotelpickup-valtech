package com.valtech.movenpick;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;


public class CrashReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_report);

        String errorString = getIntent().getStringExtra("error");

        TextView txtError = (TextView)findViewById(R.id.txtError);
        txtError.setText(errorString);
    }
}
