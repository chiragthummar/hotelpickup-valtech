package com.valtech.movenpick.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.valtech.movenpick.R;
import com.valtech.movenpick.SplashActivity;
import com.valtech.movenpick.entities.JammedMessage;
import com.valtech.movenpick.interfaces.ResponseParser;
import com.valtech.movenpick.manager.UserSettingsManager;
import com.valtech.movenpick.manager.Utility;
import com.valtech.movenpick.util.HotelPickupConstants.GeneralConstants;
import com.valtech.movenpick.util.HotelPickupConstants.MessageConstants;
import com.valtech.movenpick.util.HotelPickupUtility;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilderFactory;

public class PickupAskPhoneNumberScreen extends AppCompatActivity implements Runnable, Callback, ResponseParser {
    private ProgressDialog dialog;
    private Handler handler;

    ImageView imgLogo;


    public PickupAskPhoneNumberScreen() {
        this.dialog = null;
        this.handler = null;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_pickup_ask_phone_number_screen);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        init();
        initCustomLogo();
    }

    public void initCustomLogo() {
        try {

            String path = new Utility(PickupAskPhoneNumberScreen.this).getLogoURI();


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

    private void init() {
        this.handler = new Handler(this);
        Typeface font = HotelPickupUtility.getDefaultFont(this);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitle((CharSequence) "Pickup Car - Enter phone number");
        toolBar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolBar.setBackgroundColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolBar);
        try {
            Field field = toolBar.getClass().getDeclaredField("mTitleTextView");
            field.setAccessible(true);
            TextView title = (TextView) field.get(toolBar);
            title.setTextSize(2, 15.0f);
            title.setTypeface(font);
        } catch (Throwable ignore) {
            Log.e("HotelPickup.PickupAskPhoneNumberScreen.init", ignore.getMessage(), ignore);
        }
        m14t(R.id.jammedTrafficHeaderLabel).setTypeface(font);

    }

    private final TextView m14t(int id) {
        return (TextView) findViewById(id);
    }

    protected Dialog onCreateDialog(int id) {
        if (id != 0) {
            return null;
        }
        this.dialog = HotelPickupUtility.createProgressDialog("Sending message...", this);
        return this.dialog;
    }

    protected void onPrepareDialog(int id, Dialog dialog) {
        if (id == 0) {
            ((ProgressDialog) dialog).setMessage("Sending message...");
        } else {
            super.onPrepareDialog(id, dialog);
        }
    }


    public void parseResponse(byte[] response) {
        Throwable t;
        Throwable th;
        ByteArrayInputStream byteArrayInputStream = null;
        Message msg;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(response);
            try {
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
                in.close();
                byteArrayInputStream = null;
                NodeList errorNodeList = doc.getElementsByTagName("error");
                if (errorNodeList == null || errorNodeList.getLength() <= 0) {
                    NodeList resultNodeList = doc.getElementsByTagName("result");
                    if (resultNodeList == null || resultNodeList.getLength() == 0) {
                        throw new RuntimeException("missing result node in response");
                    }
                    String result = resultNodeList.item(0).getTextContent();
                    if (result != null && result.trim().equalsIgnoreCase("SUCCESS")) {
                        msg = new Message();
                        msg.what = MessageConstants.MESSAGE_TYPE_FINISHED;
                        msg.obj = "SUCCESS-MESSAGE-SENT";
                        this.handler.sendMessage(msg);
                    }
                    if (byteArrayInputStream != null) {
                        try {
                            byteArrayInputStream.close();
                            return;
                        } catch (IOException e) {
                            return;
                        }
                    }
                    return;
                }
                throw new RuntimeException(errorNodeList.item(0).getTextContent());
            } catch (Throwable th2) {
                th = th2;
                byteArrayInputStream = in;
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            t = th3;
            msg = new Message();
            msg.what = -1;
            msg.obj = t;
            this.handler.sendMessage(msg);
            if (byteArrayInputStream != null) {
                try {
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void parseResponse2(byte[] response) {
        Throwable t;
        Throwable th;
        ByteArrayInputStream byteArrayInputStream = null;
        Message msg;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(response);
            try {
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
                in.close();
                byteArrayInputStream = null;
                NodeList errorNodeList = doc.getElementsByTagName("error");
                if (errorNodeList == null || errorNodeList.getLength() <= 0) {

                    NodeList resultNodeList = doc.getElementsByTagName("result");
                    if (resultNodeList == null || resultNodeList.getLength() == 0) {
                        throw new RuntimeException("missing result node in response");
                    }
                    String result = resultNodeList.item(0).getTextContent();
                    if (result != null && result.trim().equalsIgnoreCase("SUCCESS")) {
                        msg = new Message();
                        msg.what = MessageConstants.MESSAGE_TYPE_FINISHED;
                        msg.obj = "SUCCESS";
                        this.handler.sendMessage(msg);
                    }
                    if (byteArrayInputStream != null) {
                        try {
                            byteArrayInputStream.close();

                        } catch (IOException e) {

                        }
                    }

                } else {
                    msg = new Message();
                    msg.what = MessageConstants.MESSAGE_TYPE_ERROR;
                    msg.obj = errorNodeList.item(0).getTextContent();
                    this.handler.sendMessage(msg);
                    //  throw new RuntimeException(errorNodeList.item(0).getTextContent());
                }

            } catch (Throwable th2) {
                th = th2;
                byteArrayInputStream = in;
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            t = th3;
            msg = new Message();
            msg.what = -1;
            msg.obj = t;
            this.handler.sendMessage(msg);
            if (byteArrayInputStream != null) {
                try {
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean handleMessage(Message msg) {
        int code = msg.what;
        Object data = msg.obj;
        switch (code) {
            case ExploreByTouchHelper.HOST_ID /*-1*/:
                // Throwable err = (Throwable) data;
                String errMessage = msg.obj.toString();
                if (errMessage == null || errMessage.trim().length() == 0) {
                    errMessage = msg.obj.toString();
                }
                this.dialog.cancel();
                HotelPickupUtility.showError(errMessage, this);
                Log.e("HotelPickup.PickupAskPhoneNumberScreen.handleMessage", errMessage);
                break;

            case MessageConstants.MESSAGE_TYPE_FINISHED /*2320*/:
                if (data instanceof String) {
                    if (!((String) data).trim().equalsIgnoreCase("SUCCESS-MESSAGE-SENT")) {
                        this.dialog.dismiss();
                        onAlerterUpdateSuccess();
                        break;
                    }
                    onMessageSentSuccess();
                    break;
                }
                break;
        }
        return true;
    }

    private void onAlerterUpdateSuccess() {
        final AlertDialog dialog = new Builder(this).setTitle("Success").setCancelable(false).setMessage("Mesage sent successfully").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(PickupAskPhoneNumberScreen.this, SplashActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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

        //  new Builder(this).setTitle("Success").setCancelable(false).setMessage("Mesage sent successfully").setPositiveButton("OK", new C01751()).create().show();
    }

    private void onMessageSentSuccess() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PickupAskPhoneNumberScreen.this.updateAlerter();
            }
        }).start();
    }

    private void updateAlerter() {
        HttpURLConnection conn = null;
        InputStream is = null;
        Message msg;
        try {
            if (HotelPickupUtility.isActiveConnectionAvailable(this)) {
                String apiURL = UserSettingsManager.getUserSettings(this).getApiURL();
                if (apiURL == null || apiURL.trim().length() == 0) {
                    throw new RuntimeException("Missing sms api url");
                }
                JammedMessage jammedMessage = (JammedMessage) getIntent().getSerializableExtra(GeneralConstants.JAMMED_MESSAGE);
                String barcode = jammedMessage.getBarCode();
                //  String phone = this.phoneNumberField.getText().toString();
                int eventID = jammedMessage.getEventID();
                apiURL = new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(apiURL)).append("?action=updatealerter").toString())).append("&eventid=").append(eventID).append("&").toString())).append("barcode=").append(URLEncoder.encode(barcode, "UTF-8")).append("&").toString())).append("phone=").append(URLEncoder.encode("", "UTF-8")).toString();
                conn = (HttpURLConnection) new URL(apiURL).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("IF-Modified-Since", "05april 2005 15:17:19 GMT");
                conn.setRequestProperty("User-Agent", "IESD - Mitgun MC");
                conn.setRequestProperty("Content-Language", "en-US");
                conn.setRequestProperty("Accept", "text/xml");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setConnectTimeout(300000);
                Log.i("HotelPickup.PickupAskPhoneNumberScreen.updateAlerter", "Opening url: " + apiURL);
                msg = new Message();
                msg.what = 1;
                msg.obj = "Updating alerter";
                this.handler.sendMessage(msg);
                int rc = conn.getResponseCode();
                if (rc == 200) {
                    msg = new Message();
                    msg.what = 1;
                    msg.obj = "Connected to server";
                    this.handler.sendMessage(msg);
                    msg = new Message();
                    msg.what = 1;
                    msg.obj = "Receiving data from server";
                    this.handler.sendMessage(msg);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buffer = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
                    is = conn.getInputStream();
                    while (true) {
                        int i = is.read(buffer);
                        if (i == -1) {
                            break;
                        }
                        out.write(buffer, 0, i);
                    }
                    is.close();
                    is = null;
                    conn.disconnect();
                    conn = null;
                    byte[] responseBytes = out.toByteArray();
                    out.close();
                    Log.i("HotelPickup.PickupAskPhoneNumberScreen.updateAlerter", new String(responseBytes));
                    parseResponse2(responseBytes);
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            return;
                        }
                    }
                    if (conn != null) {
                        conn.disconnect();
                        return;
                    }
                    return;
                }
                throw new RuntimeException("HTTP Communication error: " + rc);
            }
            throw new RuntimeException("Unable to connect to internet. Please make sure you have a working internet connection");
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e2) {
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public void run() {
        Message msg;
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            if (HotelPickupUtility.isActiveConnectionAvailable(this)) {
                String smsAPIURL = UserSettingsManager.getUserSettings(this).getSmsAPI();
                JammedMessage jammedMessage = (JammedMessage) getIntent().getSerializableExtra(GeneralConstants.JAMMED_MESSAGE);
                //   String phone = this.phoneNumberField.getText().toString();
                int eventID = jammedMessage.getEventID();
                String message = jammedMessage.getJammedMessage();
                if (smsAPIURL == null || smsAPIURL.trim().length() == 0) {
                    throw new RuntimeException("Missing sms api url");
                }
                smsAPIURL = new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(smsAPIURL)).append("?action=httpsms").toString())).append("&eventid=").append(eventID).append("&").toString())).append("sender=").append(URLEncoder.encode("", "UTF-8")).append("&").toString())).append("message=").append(URLEncoder.encode(message, "UTF-8")).toString();
                conn = (HttpURLConnection) new URL(smsAPIURL).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("IF-Modified-Since", "05april 2005 15:17:19 GMT");
                conn.setRequestProperty("User-Agent", "IESD - Mitgun MC");
                conn.setRequestProperty("Content-Language", "en-US");
                conn.setRequestProperty("Accept", "text/xml");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setConnectTimeout(300000);
                Log.i("HotelPickup.PickupAskPhoneNumberScreen.run", "Opening url: " + smsAPIURL);
                int rc = conn.getResponseCode();
                if (rc == 200) {
                    msg = new Message();
                    msg.what = 1;
                    msg.obj = "Connected to server";
                    this.handler.sendMessage(msg);
                    msg = new Message();
                    msg.what = 1;
                    msg.obj = "Receiving data from server";
                    this.handler.sendMessage(msg);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buffer = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
                    is = conn.getInputStream();
                    while (true) {
                        int i = is.read(buffer);
                        if (i == -1) {
                            break;
                        }
                        out.write(buffer, 0, i);
                    }
                    is.close();
                    is = null;
                    conn.disconnect();
                    conn = null;
                    byte[] responseBytes = out.toByteArray();
                    out.close();
                    Log.i("HotelPickup.PickupAskPhoneNumberScreen.run", new String(responseBytes));
                    parseResponse(responseBytes);
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            return;
                        }
                    }
                    if (conn != null) {
                        conn.disconnect();
                        return;
                    }
                    return;
                }
                throw new RuntimeException("HTTP Communication error: " + rc);
            }
            throw new RuntimeException("Unable to connect to internet. Please make sure you have a working internet connection");
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e2) {
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
