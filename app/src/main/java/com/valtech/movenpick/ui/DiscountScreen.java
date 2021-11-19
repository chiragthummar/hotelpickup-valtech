package com.valtech.movenpick.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.customview.widget.ExploreByTouchHelper;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.valtech.movenpick.BuildConfig;
import com.valtech.movenpick.R;
import com.valtech.movenpick.entities.UserSettings;
import com.valtech.movenpick.manager.UserSettingsManager;
import com.valtech.movenpick.manager.Utility;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.File;
import java.net.URLEncoder;
import java.text.DecimalFormat;

public class DiscountScreen extends AppCompatActivity {
    TextView barcodeField, taxField, totalField;
    EditText amountField;
    Button btnSubmit;
    Handler handler;
    ImageView imgLogo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount_pick_up_car);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        init();
        String response = getIntent().getStringExtra("Json");
        try {
            JSONObject jsonObject = new JSONObject(response);
            setData(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void initCustomLogo() {
        try {

            String path = new Utility(DiscountScreen.this).getLogoURI();


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


    private void setData(JSONObject jsonObject) {
        try {
            DecimalFormat format = new DecimalFormat("0.000");
            String avg = format.format(jsonObject.getDouble("transamount"));
            String tax = format.format(jsonObject.getDouble("taxamount"));
            barcodeField.setText(jsonObject.getString("barcode"));
            taxField.setText(tax);
            amountField.setText("" + avg);
            String total = format.format(Double.valueOf(avg) + Double.valueOf(tax));
            totalField.setText(total);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitle((CharSequence) "Transaction Amount Details");
        barcodeField = findViewById(R.id.barcodeField);
        taxField = findViewById(R.id.taxField);
        totalField = findViewById(R.id.totalField);
        amountField = findViewById(R.id.amountField);
        amountField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int count, int i2) {
                if (charSequence.toString().equalsIgnoreCase("")) {
                    double amount = Double.valueOf("0");
                    double taxamount = amount * 0.05;
                    double total = amount + taxamount;
                    DecimalFormat format = new DecimalFormat("#.000");
                    amountField.setText("0");
                    taxField.setText(format.format(taxamount));
                    totalField.setText(format.format(total));
                } else {
                    double amount = Double.valueOf(charSequence.toString());
                    double taxamount = amount * 0.05;
                    double total = amount + taxamount;
                    DecimalFormat format = new DecimalFormat("#.000");
                    taxField.setText(format.format(taxamount));
                    totalField.setText(format.format(total));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSubmit();
            }
        });
        initCustomLogo();
    }

    public void doSubmit() {

        try {

            UserSettings settings = UserSettingsManager.getUserSettings(this);
            String apiURL = settings.getApiURL();
            String eventIDs = settings.joinedEventIDs();
            int mode = 1;
            if (apiURL == null || apiURL.trim().length() == 0) {
                throw new RuntimeException("Missing api url");
            }

            if (eventIDs == null || eventIDs.trim().length() == 0) {
                throw new RuntimeException("Set Events from Settings!");
            }


            apiURL = new StringBuilder(String.valueOf(apiURL)).append("?action=updateamount").toString();


            String postData = "barcode=" + URLEncoder.encode(barcodeField.getText().toString(), "UTF-8") +
                    "&transamount=" + URLEncoder.encode(amountField.getText().toString(), "UTF-8") +
                    "&taxamount=" + URLEncoder.encode(taxField.getText().toString(), "UTF-8");
            Log.e("postData", postData);
            byte[] postBytes = postData.getBytes();


            Log.e("apiURL", apiURL);
            Bridge.post(apiURL)
                    .throwIfNotSuccess()
                    .header("app_version", BuildConfig.VERSION_CODE + "")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(postBytes)
                    .request(new com.afollestad.bridge.Callback() {
                        @Override
                        public void response(com.afollestad.bridge.Request request, @Nullable com.afollestad.bridge.Response response, @Nullable BridgeException e) {

                            if (e != null) {


                                Log.d("response", String.valueOf(e.getMessage()));
                                Message msg = new Message();
                                msg.what = ExploreByTouchHelper.HOST_ID;
                                msg.obj = e;
                                handler.sendMessage(msg);

                            } else {
                                Log.d("response", String.valueOf(response.toString()));

                                String responseString = response.asString();

                                Log.d("response", String.valueOf(responseString));


                                try {
                                    JSONObject jsonObject = XML.toJSONObject(responseString);
                                    JSONObject serviceJson = jsonObject.getJSONObject("service");
                                    if (serviceJson.has("result")) {
                                        if (serviceJson.getString("result").equalsIgnoreCase("SUCCESS")) {
                                            showSuccess();
                                        }
                                    }
                                } catch (JSONException jsonException) {
                                    jsonException.printStackTrace();
                                }


                            }
                        }


                    });


        } catch (Exception e) {

            Message msg = new Message();
            msg.what = ExploreByTouchHelper.HOST_ID;
            msg.obj = e;
            handler.sendMessage(msg);

        }


    }

    private void showSuccess() {
        final AlertDialog dialog = new AlertDialog.Builder(this).setTitle("SUCCESS").setCancelable(false).setMessage("Success").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(DiscountScreen.this, PickupCarScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        }).create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#263238"));
            }
        });
        dialog.requestWindowFeature(1);
        dialog.show();
    }
}
