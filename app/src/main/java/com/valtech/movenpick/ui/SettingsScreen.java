package com.valtech.movenpick.ui;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;

import com.valtech.movenpick.BuildConfig;
import com.valtech.movenpick.R;
import com.valtech.movenpick.entities.Event;
import com.valtech.movenpick.entities.UserSettings;
import com.valtech.movenpick.interfaces.ResponseParser;
import com.valtech.movenpick.manager.UserSettingsManager;
import com.valtech.movenpick.manager.Utility;
import com.valtech.movenpick.util.HotelPickupConstants;
import com.valtech.movenpick.util.HotelPickupUtility;
import com.valtech.movenpick.util.MarshMallowPermission;
import com.valtech.movenpick.util.PreferencesUtil;
import com.valtech.movenpick.util.StringAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static androidx.core.app.NotificationCompat.CATEGORY_EVENT;

public class SettingsScreen extends AppCompatActivity implements Runnable, View.OnClickListener, Callback, ResponseParser {

    private EditText apiURLField;
    private ProgressBar bar;
    private Spinner cameraToUseField;
    private LinearLayout contentLayer;
    private Typeface font;
    private Handler handler;
    private LinearLayout loadingLayer;
    private EditText minimumBarCodeCharactersField;
    private EditText smsAPIURLField;
    ImageView imgLogo;
    SharedPreferences prfs;
    MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);
    CheckBox cbIsMobileOptional, cbNFC;
    RadioButton button21,button22;

    public SettingsScreen() {
        this.bar = null;
        this.handler = null;
        this.font = null;
        this.loadingLayer = null;
        this.contentLayer = null;
        this.apiURLField = null;
        this.cameraToUseField = null;
        this.minimumBarCodeCharactersField = null;
        this.smsAPIURLField = null;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_settings_screen);
        try {
            init();
        } catch (Exception e) {
            HotelPickupUtility.showError(e.getMessage(), this);
        }

    }


    public void initCustomLogo() {
        try {

            String path = new Utility(SettingsScreen.this).getLogoURI();


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

        prfs = PreferenceManager.getDefaultSharedPreferences(this);
        this.handler = new Handler(this);
        this.font = HotelPickupUtility.getDefaultFont(this);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitle((CharSequence) "Settings");
        toolBar.setTitleTextColor(Color.parseColor("#263238"));
        //  toolBar.setBackgroundColor(Color.parseColor("#c0c0c0"));
        setSupportActionBar(toolBar);
        try {
            Field field = toolBar.getClass().getDeclaredField("mTitleTextView");
            field.setAccessible(true);
            TextView title = (TextView) field.get(toolBar);
            title.setTextSize(2, 15.0f);
            title.setTypeface(this.font);
        } catch (Throwable ignore) {
            Log.e("HotelPickup.SettingsScreen.init", ignore.getMessage(), ignore);
        }

        Button btnLogoUpload = (Button) findViewById(R.id.btnLogoUpload);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);

        btnLogoUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 23) {


                    checkPermissions();


                } else {
                    Log.d("API<23", "Granted");
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                }

            }
        });
        m15t(R.id.loadingEventsLabel).setTypeface(this.font);
        m15t(R.id.allowableEventsLabel).setTypeface(this.font);
        m15t(R.id.cameraToUseLabel).setTypeface(this.font);
        this.bar = (ProgressBar) findViewById(R.id.bar);
        this.loadingLayer = (LinearLayout) findViewById(R.id.loadingLayer);
        this.contentLayer = (LinearLayout) findViewById(R.id.contentLayer);
        this.contentLayer.setVisibility(View.GONE);
        this.apiURLField = (EditText) findViewById(R.id.apiURLField);
        this.apiURLField.setTypeface(this.font);
        this.button21 = findViewById(R.id.button21);
        this.button22 = findViewById(R.id.button22);
        this.smsAPIURLField = (EditText) findViewById(R.id.smsAPIURLField);
        this.smsAPIURLField.setTypeface(this.font);
        this.cameraToUseField = (Spinner) findViewById(R.id.cameraToUseField);

        this.cbIsMobileOptional = (CheckBox) findViewById(R.id.cbIsMobileOptional);


        this.cbNFC = findViewById(R.id.cbNFC);

        // cameraToUseField.setGravity(View.TEXT_ALIGNMENT_VIEW_START);
        this.minimumBarCodeCharactersField = (EditText) findViewById(R.id.minimumBarCodeCharactersField);
        this.minimumBarCodeCharactersField.setTypeface(this.font);
        Button saveSettingsButton = (Button) findViewById(R.id.saveSettingsButton);
        saveSettingsButton.setTypeface(this.font);
        saveSettingsButton.setOnClickListener(this);
        initCustomLogo();
        loadEvents();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();


            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                imgLogo.setImageBitmap(bitmap);
                String fileUri = new Utility(SettingsScreen.this).getDirUri(uri);
                Log.d("fileUri", fileUri);
                SharedPreferences.Editor editor = prfs.edit();
                editor.putString("LogoURL", String.valueOf(fileUri));
                editor.apply();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private final TextView m15t(int id) {
        return (TextView) findViewById(id);
    }

    private void loadEvents() {
        new Thread(this).start();
    }

    public void parseResponse(byte[] response) {
        Throwable t;
        Throwable th;
        ByteArrayInputStream byteArrayInputStream = null;
        ByteArrayInputStream in = new ByteArrayInputStream(response);
        Message msg;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(in);
            in.close();
            byteArrayInputStream = null;
            NodeList errorNodeList = doc.getElementsByTagName("error");
            if (errorNodeList == null || errorNodeList.getLength() <= 0) {
                try {
                    NodeList eventsNodeList = doc.getElementsByTagName("events");
                    if (eventsNodeList == null || eventsNodeList.getLength() == 0) {
                        throw new RuntimeException("events node missing in response");
                    }
                    NodeList kids = eventsNodeList.item(0).getChildNodes();
                    if (kids == null || kids.getLength() == 0) {
                        throw new RuntimeException("events node has no child nodes in response");
                    }
                    int count = kids.getLength();
                    ArrayList<Event> list = new ArrayList();
                    for (int i = 0; i < count; i++) {
                        Node kid = kids.item(i);
                        if (kid != null) {
                            String nodeName = kid.getNodeName();
                            if (!(nodeName == null || nodeName.trim().length() == 0 || !nodeName.trim().equalsIgnoreCase(CATEGORY_EVENT))) {
                                list.add(parseEventNode(kid));
                            }
                        }
                    }
                    msg = new Message();
                    msg.what = HotelPickupConstants.MessageConstants.MESSAGE_TYPE_FINISHED;
                    msg.obj = list;
                    this.handler.sendMessage(msg);

                    if (byteArrayInputStream != null) {
                        try {
                            byteArrayInputStream.close();
                        } catch (IOException e) {
                        }
                    }
                } catch (Throwable th2) {
                    t = th2;
                }
            } else {
                throw new RuntimeException(errorNodeList.item(0).getTextContent());
            }
        } catch (Throwable th3) {
            th = th3;
            byteArrayInputStream = in;
            if (byteArrayInputStream != null) {
                try {
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // throw th;
        }
    }

    private Event parseEventNode(Node node) {
        NamedNodeMap attrs = node.getAttributes();
        if (attrs == null || attrs.getLength() == 0) {
            throw new RuntimeException("event node has no attributes");
        }
        Node idNode = attrs.getNamedItem("id");
        if (idNode == null) {
            throw new RuntimeException("id attribute missing from event node");
        }
        String eventID = idNode.getNodeValue();
        if (eventID == null || eventID.trim().length() == 0) {
            throw new RuntimeException("id is " + eventID + " in event node");
        }
        Node nameNode = attrs.getNamedItem("name");
        if (nameNode == null) {
            throw new RuntimeException("name attribute missing from event node");
        }
        String eventName = nameNode.getNodeValue();
        if (eventName == null || eventName.trim().length() == 0) {
            throw new RuntimeException("name is " + eventName + " in event node");
        }
        Event event = new Event();
        event.setAllowable(false);
        event.setEventID(Integer.parseInt(eventID));
        event.setEventName(eventName);
        return event;
    }

    public boolean handleMessage(Message msg) {
        int code = msg.what;
        Object data = msg.obj;
        switch (code) {
            case ExploreByTouchHelper.HOST_ID /*-1*/:
                Throwable err = (Throwable) data;
                String errMessage = err.getMessage();
                if (errMessage == null || errMessage.trim().length() == 0) {
                    errMessage = err.toString();
                }
                this.bar.setVisibility(View.VISIBLE);
                m15t(R.id.loadingEventsLabel).setText(errMessage);
                Log.e("HotelPickup.SettingsScreen.handleMessage", errMessage, (Throwable) err);
                break;

            case HotelPickupConstants.MessageConstants.MESSAGE_TYPE_FINISHED /*2320*/:
                if (data instanceof ArrayList) {
                    onEventListFetched((ArrayList) data);
                    break;
                }
                break;
        }
        return true;
    }

    @SuppressLint({"RtlHardcoded"})
    private void onEventListFetched(ArrayList<Event> list) {
        LinearLayout eventsLayer = (LinearLayout) findViewById(R.id.eventsLayer);
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Event event = (Event) it.next();
            CheckBox cb = new CheckBox(this);

            cb.setText(event.getEventName());
            cb.setChecked(false);
            cb.setTextColor(getResources().getColor(R.color.white));
            cb.setTypeface(this.font);
            LayoutParams params = new LayoutParams(-2, -2);
            params.setMargins(0, 2, 0, 0);
            params.gravity = Gravity.START;
            cb.setLayoutParams(params);
            cb.setId(event.getEventID());
            eventsLayer.addView(cb);
        }
        this.cameraToUseField.setAdapter(new StringAdapter(Arrays.asList(getResources().getStringArray(R.array.cameraList)), this));
        loadSettings();
        crossFade();
    }

    private void loadSettings() {
        try {
            UserSettings settings = UserSettingsManager.getUserSettings(this);
            String apiURL = settings.getApiURL();
            int cameraToUse = settings.getCameraToUse();
            int minimimumBarCodeCharacters = settings.getMinimumBarCodeCharacters();
            String smsAPIURL = settings.getSmsAPI();
            this.apiURLField.setText(apiURL);
            this.cameraToUseField.setSelection(cameraToUse);

            int isMobileOptional = settings.getMobileOptional();
            Log.d("isMobileOptional", isMobileOptional + "");
            boolean isMobile = isMobileOptional == 1;
            cbIsMobileOptional.setChecked(isMobile);

            int isNFCOptional = settings.getNFC();
            Log.d("isMobileOptional", isNFCOptional + "");
            boolean isNFC = isNFCOptional == 1;
            cbNFC.setChecked(isNFC);

            String mode = settings.getMode();
            if(mode.equalsIgnoreCase("free")){
                button21.setChecked(true);
                button22.setChecked(false);
            }else{
                button21.setChecked(false);
                button22.setChecked(true);
            }

            this.minimumBarCodeCharactersField.setText(String.valueOf(minimimumBarCodeCharacters));
            this.smsAPIURLField.setText(smsAPIURL);
            for (Event event : settings.getEvents()) {
                checkAllowableEvent(event);
            }
        } catch (Throwable t) {
            String msg = t.getMessage();
            if (msg == null || msg.trim().length() == 0) {
                msg = t.toString();
            }
            HotelPickupUtility.showError(msg, this);
            Log.e("HotelPickup.SettingsScreen.loadSettings", msg, t);
        }
    }

    private void checkAllowableEvent(Event event) {
        LinearLayout eventsLayer = (LinearLayout) findViewById(R.id.eventsLayer);
        int count = eventsLayer.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = eventsLayer.getChildAt(i);
            if (view instanceof CheckBox) {
                CheckBox cb = (CheckBox) view;
                if (cb.getId() == event.getEventID() && event.isAllowable()) {
                    cb.setChecked(true);
                }
            }
        }
    }

    private void crossFade() {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        anim.setFillAfter(true);
        this.contentLayer.startAnimation(anim);
        AlphaAnimation anim2 = new AlphaAnimation(1.0f, 0.0f);
        anim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                SettingsScreen.this.loadingLayer.setVisibility(View.GONE);
                SettingsScreen.this.contentLayer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim2.setDuration(500);
        anim2.setFillAfter(true);
        this.loadingLayer.startAnimation(anim2);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.saveSettingsButton) {
            onSaveSettingsClicked();
        }
    }

    private void onSaveSettingsClicked() {
        ArrayList<Event> list = new ArrayList();
        LinearLayout eventsLayer = (LinearLayout) findViewById(R.id.eventsLayer);
        int count = eventsLayer.getChildCount();
        boolean flag = false;
        for (int i = 0; i < count; i++) {
            View view = eventsLayer.getChildAt(i);
            if (view instanceof CheckBox) {
                CheckBox cb = (CheckBox) view;
                String eventName = cb.getText().toString();
                int eventID = cb.getId();
                boolean allowable = cb.isChecked();
                if (allowable) {
                    flag = true;
                }
                Event event = new Event();
                event.setAllowable(allowable);
                event.setEventID(eventID);
                event.setEventName(eventName);
                list.add(event);
            }
        }
        if (list.size() == 0) {
            HotelPickupUtility.showError("No events found", this);
        } else if (flag) {
            String apiURL = this.apiURLField.getText().toString();
            if (apiURL == null || apiURL.trim().length() == 0) {
                HotelPickupUtility.showError("Please enter the api url", this);
                this.apiURLField.requestFocus();
                return;
            }
            String smsAPIURL = this.smsAPIURLField.getText().toString();
            if (smsAPIURL == null || smsAPIURL.trim().length() == 0) {
                HotelPickupUtility.showError("Please enter the sms api url", this);
                this.smsAPIURLField.requestFocus();
                return;
            }
            int pos = this.cameraToUseField.getSelectedItemPosition();
            if (pos == -1) {
                HotelPickupUtility.showError("Please select a camera to use", this);
                this.cameraToUseField.requestFocus();
                return;
            }


            int cameraToUse = pos;
            String minChars = this.minimumBarCodeCharactersField.getText().toString();
            if (minChars == null || minChars.trim().length() == 0) {
                HotelPickupUtility.showError("Please enter the minimum barcode characters", this);
                this.minimumBarCodeCharactersField.requestFocus();
                return;
            }
            Event[] events = (Event[]) list.toArray(new Event[list.size()]);
            try {
                UserSettings settings = new UserSettings();
                settings.setApiURL(apiURL);
                settings.setCameraToUse(cameraToUse);
                settings.setEvents(events);
                settings.setMinimumBarCodeCharacters(Integer.parseInt(minChars));
                settings.setSmsAPI(smsAPIURL);
                boolean mode = button21.isChecked();
                if(mode){
                    settings.setMode("Free");
                }else{
                    settings.setMode("Discount");
                }

                boolean isMobile = cbIsMobileOptional.isChecked();
                int isMobileDb = isMobile ? 1 : 0;

                boolean isNFC = cbNFC.isChecked();
                int isNFCDb = isNFC ? 1 : 0;
                Log.d("isMobileDb", isMobileDb + "");
                settings.setMobileOptional(isMobileDb);
                settings.setNFC(isNFCDb);
                UserSettingsManager.saveUserSettings(settings, this);
                Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_LONG).show();
                finish();
            } catch (Throwable t) {
                String msg = t.getMessage();
                if (msg == null || msg.trim().length() == 0) {
                    msg = t.toString();
                }
                HotelPickupUtility.showError(msg, this);
                Log.e("HotelPickup.SettingsScreen.onSaveSettingsClicked", msg, t);
            }
        } else {
            HotelPickupUtility.showError("Please mark an event as allowable", this);
        }
    }

    public void run() {
        HttpURLConnection conn = null;
        InputStream is = null;
        Message msg;
        try {
            if (HotelPickupUtility.isActiveConnectionAvailable(this)) {
                String apiURL = UserSettingsManager.getUserSettings(this).getApiURL();
                if (apiURL == null || apiURL.trim().length() == 0) {
                    apiURL = PreferencesUtil.getString(this, "BASE_URL", "");
                }
                apiURL = new StringBuilder(String.valueOf(apiURL)).append("?action=getevents").toString();
                conn = (HttpURLConnection) new URL(apiURL).openConnection();
                conn.setRequestMethod("GET");
                conn.setDoOutput(false);
                conn.setRequestProperty("IF-Modified-Since", "05april 2005 15:17:19 GMT");
                conn.setRequestProperty("User-Agent", "IESD - Mitgun MC");
                conn.setRequestProperty("Content-Language", "en-US");
                conn.setRequestProperty("Accept", "text/xml");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("app_version", BuildConfig.VERSION_CODE + "");
                conn.setConnectTimeout(30000);
                Log.i("HotelPickup.SettingsScreen.run", "Opening url: " + apiURL);
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
                    Log.i("HotelPickup.SettingsScreen.run", new String(responseBytes));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Log.d("Granted", "Granted");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

        } else {
            Toast.makeText(SettingsScreen.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
        }

    }

    public void checkPermissions() {

        if (!marshMallowPermission.checkPermissionForExternalStorage()) {
            marshMallowPermission.requestPermissionForExternalStorage();
        } else {
            Log.d("AlreadyGranted", "AlreadyGranted");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        }


    }

}
