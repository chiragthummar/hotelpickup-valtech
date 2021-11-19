package com.valtech.movenpick.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.valtech.movenpick.BuildConfig;
import com.valtech.movenpick.R;
import com.valtech.movenpick.SplashActivity;
import com.valtech.movenpick.entities.JammedMessage;
import com.valtech.movenpick.entities.UserSettings;
import com.valtech.movenpick.interfaces.ResponseParser;
import com.valtech.movenpick.manager.UserSettingsManager;
import com.valtech.movenpick.manager.Utility;
import com.valtech.movenpick.util.HotelPickupConstants.GeneralConstants;
import com.valtech.movenpick.util.HotelPickupConstants.MessageConstants;
import com.valtech.movenpick.util.HotelPickupUtility;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilderFactory;

import static com.valtech.movenpick.util.HotelPickupConstants.GeneralConstants.MOBILE_NUM;

public class PickupAskKeyTagScreen extends AppCompatActivity implements Runnable, OnClickListener, Callback, ResponseParser {
    private NfcAdapter adapter;
    private ProgressDialog dialog;
    private Handler handler;
    private EditText keyTagField;
    private PendingIntent pendingIntent;
    private IntentFilter[] readTagFilters;
    private Tag tag;
    ImageView imgLogo;
    static String mobileNum;

    /* renamed from: com.hotelpickup.movenpick.ui.PickupAskKeyTagScreen.1 */
    class C01741 implements DialogInterface.OnClickListener {
        C01741() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            PickupAskKeyTagScreen.this.startActivity(new Intent(PickupAskKeyTagScreen.this, SplashActivity.class));
            PickupAskKeyTagScreen.this.finish();
        }
    }

    public /* bridge */ /* synthetic */ View onCreateView(View view, String str, Context context, AttributeSet attributeSet) {
        return super.onCreateView(view, str, context, attributeSet);
    }

    public /* bridge */ /* synthetic */ View onCreateView(String str, Context context, AttributeSet attributeSet) {
        return super.onCreateView(str, context, attributeSet);
    }

    public PickupAskKeyTagScreen() {
        this.dialog = null;
        this.handler = null;
        this.keyTagField = null;
        this.tag = null;
        this.adapter = null;
        this.readTagFilters = null;
        this.pendingIntent = null;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_ask_key_tag_screen);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        init();
        initCustomLogo();
    }

    public void initCustomLogo() {
        try {

            String path = new Utility(PickupAskKeyTagScreen.this).getLogoURI();


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
        toolBar.setTitle((CharSequence) "Pickup Car - Scan Keycard");
        //   toolBar.setTitleTextColor(Color.parseColor("#ffffff"));
        //  toolBar.setBackgroundColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolBar);
        try {
            Field field = toolBar.getClass().getDeclaredField("mTitleTextView");
            field.setAccessible(true);
            TextView title = (TextView) field.get(toolBar);
            title.setTextSize(2, 15.0f);
            title.setTypeface(font);
        } catch (Throwable ignore) {
            Log.e("HotelPickup.PickupAskKeyTagScreen.init", ignore.getMessage(), ignore);
        }
        this.keyTagField = (EditText) findViewById(R.id.keyTagField);
        this.keyTagField.setTypeface(font);
        this.keyTagField.setHintTextColor(getResources().getColor(R.color.white));

        ((ImageButton) findViewById(R.id.scanButton)).setOnClickListener(this);
        initNFC();
        //  submitPickupRequest();

        Intent intent = getIntent();

        mobileNum = intent.getStringExtra(MOBILE_NUM);

        Log.d("mobileNumIntent", mobileNum);

        //this.keyTagField.setText("F455AD11");
       // submitPickupRequest();
    }

    private void initNFC() {
        this.adapter = NfcAdapter.getDefaultAdapter(this);
        if (this.adapter == null) {
            HotelPickupUtility.showError("NFC adapter not found in this device", this);
        } else if (this.adapter.isEnabled()) {
            this.tag = (Tag) getIntent().getParcelableExtra("android.nfc.extra.TAG");
            this.pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(536870912), 0);
            IntentFilter tagDetected = new IntentFilter("android.nfc.action.TAG_DISCOVERED");
            IntentFilter filter2 = new IntentFilter("android.nfc.action.NDEF_DISCOVERED");
            this.readTagFilters = new IntentFilter[]{tagDetected, filter2};
        } else {
            HotelPickupUtility.showError("NFC adapter not enabled in this device. Please enable it in device settings panel", this);
        }
    }

    protected void onResume() {
        super.onResume();
        if (this.adapter != null) {
            this.adapter.enableForegroundDispatch(this, this.pendingIntent, this.readTagFilters, null);
        }
    }

    protected void onNewIntent(Intent intent) {
        Intent oldIntent = getIntent();
        String barCode = oldIntent.getStringExtra(GeneralConstants.BARCODE);
        int eventID = oldIntent.getIntExtra(GeneralConstants.EVENT_ID, 0);
        intent.putExtra(GeneralConstants.BARCODE, barCode);
        intent.putExtra(GeneralConstants.EVENT_ID, eventID);
        setIntent(intent);
        if (getIntent().getAction().equals("android.nfc.action.TAG_DISCOVERED")) {
            this.tag = (Tag) intent.getParcelableExtra("android.nfc.extra.TAG");
            readFromTag(getIntent());
        }
    }

    public void readFromTag(Intent intent) {
        this.keyTagField.setText(ByteArrayToHexString(this.tag.getId()));
        submitPickupRequest();
    }

    private String ByteArrayToHexString(byte[] inarray) {
        String[] hex = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";
        for (byte b : inarray) {
            int in = b & MotionEventCompat.ACTION_MASK;
            out = new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(out)).append(hex[(in >> 4) & 15]).toString())).append(hex[in & 15]).toString();
        }
        return out;
    }

    private void submitPickupRequest() {
        showDialog(0, Bundle.EMPTY);
        new Thread(this).start();
    }

    protected Dialog onCreateDialog(int id) {
        if (id != 0) {
            return null;
        }
        this.dialog = HotelPickupUtility.createProgressDialog("Please wait...", this);
        return this.dialog;
    }

    protected void onPrepareDialog(int id, Dialog dialog) {
        if (id == 0) {
            ((ProgressDialog) dialog).setMessage("Please wait...");
        } else {
            super.onPrepareDialog(id, dialog);
        }
    }

    public void parseResponse(byte[] response) {
        Throwable th;
        ByteArrayInputStream byteArrayInputStream = null;
        ByteArrayInputStream in = new ByteArrayInputStream(response);
        Message msg;
        try {

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            in.close();
            byteArrayInputStream = null;
            NodeList errorNodeList = doc.getElementsByTagName("error");
            if (errorNodeList == null || errorNodeList.getLength() <= 0) {

                try {

                    NodeList resultNodeList = doc.getElementsByTagName("result");
                    if (resultNodeList == null || resultNodeList.getLength() == 0) {
                        throw new RuntimeException("missing result node in response");
                    }
                    Node resultNode = resultNodeList.item(0);
                    String result = resultNode.getTextContent();
                    if (result == null || !result.trim().equalsIgnoreCase("SUCCESS")) {
                        NamedNodeMap attrs = resultNode.getAttributes();
                        if (attrs == null || attrs.getLength() == 0) {
                            throw new RuntimeException("result node does not have return value of success or any attributes");
                        }
                        Node etaNode = attrs.getNamedItem("eta");
                        if (etaNode == null) {
                            throw new RuntimeException("eta attribute is null");
                        }
                        String eta = etaNode.getNodeValue();
                        String jammedMessage = resultNode.getTextContent();
                        Intent thisIntent = getIntent();
                        int eventID = thisIntent.getIntExtra(GeneralConstants.EVENT_ID, 0);
                        String barCode = thisIntent.getStringExtra(GeneralConstants.BARCODE);
                        JammedMessage message = new JammedMessage();
                        message.setEta(eta);
                        message.setEventID(eventID);
                        message.setJammedMessage(jammedMessage);
                        message.setBarCode(barCode);
                        msg = new Message();
                        msg.what = MessageConstants.MESSAGE_TYPE_FINISHED;
                        msg.obj = message;
                        this.handler.sendMessage(msg);
                    } else {
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

                } catch (Throwable th2) {
                    th = th2;
                    try {
                        Throwable t;
                        msg = new Message();
                        msg.what = MessageConstants.MESSAGE_TYPE_ERROR;
                        msg.obj = th;
                        this.handler.sendMessage(msg);
                        if (byteArrayInputStream != null) {
                            try {
                                byteArrayInputStream.close();
                            } catch (IOException e2) {

                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        if (byteArrayInputStream != null) {
                            try {
                                byteArrayInputStream.close();
                            } catch (IOException e3) {
                            }
                        }
                        throw th;
                    }
                }
            } else {
                msg = new Message();
                msg.what = MessageConstants.MESSAGE_TYPE_ERROR;
                msg.obj = errorNodeList.item(0).getTextContent();
                this.handler.sendMessage(msg);

                //   throw new RuntimeException(errorNodeList.item(0).getTextContent());


            }

        } catch (Throwable th4) {
            th = th4;
            msg = new Message();
            msg.what = MessageConstants.MESSAGE_TYPE_ERROR;
            msg.obj = th4;
            this.handler.sendMessage(msg);
            byteArrayInputStream = in;
            if (byteArrayInputStream != null) {
                try {
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                throw th;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }


        }
    }

    public boolean handleMessage(Message msg) {
        int code = msg.what;
        Object data = (Object) msg.obj;
        switch (code) {
            case ExploreByTouchHelper.HOST_ID /*-1*/:
                // Throwable err = (Throwable) data;
                String errMessage = msg.obj.toString();
                if (errMessage == null || errMessage.trim().length() == 0) {
                    errMessage = msg.obj.toString();
                }
                this.dialog.dismiss();
                HotelPickupUtility.showError(errMessage, this);
                Log.e("HotelPickup.PickupAskKeyTagScreen.handleMessage", errMessage);
                this.keyTagField.setText("");
                break;

            case MessageConstants.MESSAGE_TYPE_FINISHED /*2320*/:
                this.dialog.dismiss();
                if (!(data instanceof String)) {
                    if (data instanceof JammedMessage) {
                        onSubmitSuccess();
                        break;
                    }
                } else if (((String) data).trim().equalsIgnoreCase("SUCCESS")) {
                    onSubmitSuccess();
                    break;
                }
                break;
        }
        return true;
    }

    private void onJammedTraffic(JammedMessage jammedMessage) {
        startActivity(new Intent(this, PickupAskPhoneNumberScreen.class).putExtra(GeneralConstants.JAMMED_MESSAGE, jammedMessage));
        finish();
    }

    private void onSubmitSuccess() {

        final AlertDialog dialog = new Builder(this).setTitle("Success").setCancelable(false).setMessage("Car pickup successful").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                startActivity(new Intent(PickupAskKeyTagScreen.this, SplashActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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

        // new Builder(this).setTitle("Success").setCancelable(false).setMessage("Car pickup successful").setPositiveButton("OK", new C01741()).create().show();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.scanButton) {


            onScanClicked();

        }
    }

    private void onScanClicked() {
    }

    public void run() {
        HttpURLConnection conn = null;
        InputStream is = null;
        OutputStream os = null;
        Message msg;
        try {
            if (HotelPickupUtility.isActiveConnectionAvailable(this)) {

                UserSettings settings = UserSettingsManager.getUserSettings(this);

                int isMobile = settings.getMobileOptional();


                String apiURL = UserSettingsManager.getUserSettings(this).getApiURL();
                Intent thisIntent = getIntent();
                String barCode = thisIntent.getStringExtra(GeneralConstants.BARCODE);
                int eventID = thisIntent.getIntExtra(GeneralConstants.EVENT_ID, 0);
                String keyCard = this.keyTagField.getText().toString();
                if (apiURL == null || apiURL.trim().length() == 0) {
                    throw new RuntimeException("Missing api url");
                }

                if (apiURL.trim().length() == 0) {
                    throw new RuntimeException("Missing api url");
                }

                String mobileNum1 = thisIntent.getStringExtra(MOBILE_NUM);

                if (mobileNum == null || mobileNum.equals("")) {
                    mobileNum = mobileNum1;
                }

                Log.d("isMobile", isMobile + "");
                String postData = "";
                if (isMobile == 1 && (mobileNum == null || mobileNum.equals(""))) {
                    apiURL = new StringBuilder(String.valueOf(apiURL)).append("?action=udpatepickup").toString();
                    postData = "barcode=" + URLEncoder.encode(barCode, "UTF-8") + "&eventid=" + eventID + "&keycard=" + URLEncoder.encode(keyCard, "UTF-8");
                } else {
                    apiURL = new StringBuilder(String.valueOf(apiURL)).append("?action=udpatepickupwithmobile").toString();
                    postData = "barcode=" + URLEncoder.encode(barCode, "UTF-8") + "&mobileno=" + URLEncoder.encode(mobileNum, "UTF-8") + "&eventid=" + eventID + "&keycard=" + URLEncoder.encode(keyCard, "UTF-8");
                }

                Log.e("apiURL",apiURL);
                Log.e("postData",postData);

                conn = (HttpURLConnection) new URL(apiURL).openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("IF-Modified-Since", "05april 2005 15:17:19 GMT");
                conn.setRequestProperty("User-Agent", "IESD - Mitgun MC");
                conn.setRequestProperty("Content-Language", "en-US");
                conn.setRequestProperty("Accept", "text/xml");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("app_version", BuildConfig.VERSION_CODE + "");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                Log.i("HotelPickup.PickupAskKeyTagScreen.run", "Opening url: " + apiURL);

                byte[] postBytes = postData.getBytes();
                conn.setRequestProperty("Content-length", String.valueOf(postBytes.length));
                Log.i("HotelPickup.PickupAskKeyTagScreen.run", "Posting data: " + postData);
                os = conn.getOutputStream();
                os.write(postBytes);
                os.flush();
                os.close();
                os = null;
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
                    Log.i("HotelPickup.PickupAskKeyTagScreen.run", new String(responseBytes));
                    parseResponse(responseBytes);
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            return;
                        }
                    }
                    if (is != null) {
                        is.close();
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
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e2) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
