package com.valtech.movenpick.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.zxing.Result;
import com.valtech.movenpick.manager.UserSettingsManager;
import com.valtech.movenpick.util.HotelPickupUtility;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeScanner extends AppCompatActivity  implements ZXingScannerView.ResultHandler {
    int camera;
    private ZXingScannerView mScannerView;
    public QRCodeScanner() {
        this.mScannerView = null;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {

            camera =  UserSettingsManager.getUserSettings(this).getCameraToUse();

            this.mScannerView = new ZXingScannerView(this);

            setContentView(this.mScannerView);

            mScannerView.setResultHandler(this);
            mScannerView.setFlash(true);
            mScannerView.startCamera(camera);

        } catch (Throwable t) {
            String msg = (t.getMessage() == null || t.getMessage().trim().length() <= 0) ? t.toString() : t.getMessage();
            HotelPickupUtility.showError(msg, this);
            Log.e("HotelPickup.QRCodeScanner.init", msg, t);
        }
    }

    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onPause() {
        super.onPause();

        mScannerView.stopCamera();
    }


    @Override
    public void onBackPressed() {
        mScannerView.stopCamera();
        this.finish();
    }


    @Override
    public void handleResult(Result result) {
        Intent intent = new Intent();
        intent.putExtra("scanned.barCode", result.getText());
        mScannerView.resumeCameraPreview(this);
        this.setResult(Activity.RESULT_OK, intent);
        Log.v("TAG", result.getText());

        this.finish();
    }
}
