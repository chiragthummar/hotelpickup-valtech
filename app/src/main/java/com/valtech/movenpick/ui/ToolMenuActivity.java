package com.valtech.movenpick.ui;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInstaller;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.customview.widget.ExploreByTouchHelper;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.valtech.movenpick.R;
import com.valtech.movenpick.entities.UserSettings;
import com.valtech.movenpick.manager.UserSettingsManager;
import com.valtech.movenpick.util.HotelPickupUtility;
import com.valtech.movenpick.util.MarshMallowPermission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.valtech.movenpick.util.HotelPickupConstants.MessageConstants.MESSAGE_TYPE_ALERT;
import static com.valtech.movenpick.util.HotelPickupConstants.MessageConstants.MESSAGE_TYPE_ERROR;
import static com.valtech.movenpick.util.HotelPickupConstants.MessageConstants.MESSAGE_TYPE_FINISHED;
import static com.valtech.movenpick.util.HotelPickupConstants.MessageConstants.MESSAGE_TYPE_MESSAGE;

public class ToolMenuActivity extends AppCompatActivity implements View.OnClickListener, Runnable, Handler.Callback {
    private ProgressDialog dialog;
    RelativeLayout btnAbout, internetStatus, btnCheckUpdate;
    ToolMenuActivity instance;
    private Handler handler = null;
    MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);

    private static final String PACKAGE_INSTALLED_ACTION =
            "com.valtech.movenpick.SESSION_API_PACKAGE_INSTALLED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_menu);

        instance = this;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        this.handler = new Handler(this);

        internetStatus = (RelativeLayout) findViewById(R.id.internetStatus);

        internetStatus.setOnClickListener(this);

        btnCheckUpdate = (RelativeLayout) findViewById(R.id.btnCheckUpdate);

        btnCheckUpdate.setOnClickListener(this);

        btnAbout = (RelativeLayout) findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(ToolMenuActivity.this, AboutActivity.class);


                startActivity(intent);

            }
        });

    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.internetStatus) {
            onInternetStatusClicked();
        }

        if (id == R.id.btnCheckUpdate) {

            if (Build.VERSION.SDK_INT >= 23) {


                checkPermissions();


            }


        }
    }

    public void checkPermissions() {

        if (!marshMallowPermission.checkPermissionForExternalStorage()) {
            marshMallowPermission.requestPermissionForExternalStorage();
        } else {
            showDialog(0, Bundle.EMPTY);
            Thread thread;
            thread = new Thread(this);
            thread.start();
        }


    }

    public static boolean isNetworkConnected(Context mContext) {
        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE); // 1
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); // 2
        return networkInfo != null && networkInfo.isConnected(); // 3
    }


    public boolean handleMessage(Message msg) {
        int code = msg.what;
        Object data = msg.obj;
        switch (code) {
            case ExploreByTouchHelper.HOST_ID:
                Throwable err = (Throwable) data;
                String errMessage = err.getMessage();
                if (errMessage == null || errMessage.trim().length() == 0) {
                    errMessage = err.toString();
                }
                this.dialog.cancel();
                HotelPickupUtility.showError(errMessage, this);
                Log.e("HotelPickup.PickupCarScreen.handleMessage", errMessage, err);

                break;
            case MESSAGE_TYPE_MESSAGE:
                this.dialog.setMessage(data.toString());
                break;
            case MESSAGE_TYPE_ALERT:
                this.dialog.dismiss();
                HotelPickupUtility.showError(data.toString(), this);
                break;

            case MESSAGE_TYPE_FINISHED /*2320*/:
                this.dialog.dismiss();

                break;
        }
        return true;
    }

    protected Dialog onCreateDialog(int id) {
        if (id != 0) {
            return null;
        }
        this.dialog = HotelPickupUtility.createProgressDialog("Loading...", this);
        return this.dialog;
    }

    protected void onPrepareDialog(int id, Dialog dialog) {
        if (id == 0) {
            ((ProgressDialog) dialog).setMessage("Loading...");
        } else {
            super.onPrepareDialog(id, dialog);
        }
    }

    public String getUpdateUrl() {

        String ApkURL = "";
        try {

            UserSettings settings = UserSettingsManager.getUserSettings(this);
            String apiURL = settings.getApiURL();

            try {
                URL url = new URL(apiURL);
                ApkURL = url.getProtocol() + "://" + url.getHost() + "/updatehotel/app-debug.apk";

                Log.d("selectedAPIURL", ApkURL);
            } catch (MalformedURLException e) {

            }

        } catch (Exception exc) {

        }

        return ApkURL;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkApkUpdate() {

        try {

            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Message msg = new Message();
            msg.what = MESSAGE_TYPE_MESSAGE;
            msg.obj = "Checking Update...";
            handler.sendMessage(msg);


            URL url = new URL(getUpdateUrl());
            Log.d("url", String.valueOf(url));

            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();


            if (responseCode == HttpURLConnection.HTTP_OK) {

                msg = new Message();
                msg.obj = "There is a new version available. Downloading apk...";
                msg.what = MESSAGE_TYPE_MESSAGE;
                handler.sendMessage(msg);


                String fileName = "";
                String disposition = httpConn.getHeaderField("Content-Disposition");
                String contentType = httpConn.getContentType();
                int contentLength = httpConn.getContentLength();

                if (disposition != null) {

                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        fileName = disposition.substring(index + 10,
                                disposition.length() - 1);
                    }
                } else {

                    fileName = url.toString().substring(url.toString().lastIndexOf("/") + 1,
                            url.toString().length());
                }

                System.out.println("Content-Type = " + contentType);
                System.out.println("Content-Disposition = " + disposition);
                System.out.println("Content-Length = " + contentLength);
                System.out.println("fileName = " + fileName);


                InputStream inputStream = httpConn.getInputStream();
                String saveFilePath = Environment.getExternalStorageDirectory() + File.separator + fileName;


                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
                    saveFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ File.separator + fileName;
                    FileOutputStream outputStream = new FileOutputStream(saveFilePath);

                    int bytesRead = -1;
                    byte[] buffer = new byte[4096];
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.close();
                    inputStream.close();

                }
                else
                {


                    try (FileOutputStream fos = this.openFileOutput(fileName, Context.MODE_PRIVATE)) {


                        int bytesRead = -1;
                        byte[] buffer = new byte[4096];
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }

                        fos.close();
                        inputStream.close();

                    }

                }






                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N){


                    String[]  files = this.fileList();

                    File file = new File(this.getFilesDir(), files[0]);

                    Log.e("saveFilePath1",file.getPath());

                    PackageInstaller.Session session = null;
                    try {
                        PackageInstaller packageInstaller = getPackageManager().getPackageInstaller();
                        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                                PackageInstaller.SessionParams.MODE_FULL_INSTALL);
                        int sessionId = packageInstaller.createSession(params);
                        session = packageInstaller.openSession(sessionId);
                        addApkToInstallSession(file.getPath(), session);
                        // Create an install status receiver.
                        Context context = this;
                        Intent intent = new Intent(context, ToolMenuActivity.class);
                        intent.setAction(PACKAGE_INSTALLED_ACTION);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                        IntentSender statusReceiver = pendingIntent.getIntentSender();
                        // Commit the session (this will start the installation workflow).
                        session.commit(statusReceiver);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException("Couldn't install package", e);
                    } catch (RuntimeException e) {
                        if (session != null) {
                            session.abandon();
                        }
                        throw e;
                    }




                }
                else
                {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(saveFilePath)), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }




                System.out.println("File downloaded at " + saveFilePath);

            } else {


                msg = new Message();
                msg.obj = "No Update Available..";
                msg.what = MESSAGE_TYPE_ALERT;
                handler.sendMessage(msg);

            }

            httpConn.disconnect();

            msg = new Message();
            msg.obj = "Success";
            msg.what = MESSAGE_TYPE_FINISHED;
            handler.sendMessage(msg);

        } catch (MalformedURLException e1) {
            Message msg1 = new Message();
            msg1.what = MESSAGE_TYPE_ERROR;
            msg1.obj = e1;
            handler.sendMessage(msg1);

        } catch (final Throwable t) {


            Message msg1 = new Message();
            msg1.what = MESSAGE_TYPE_ERROR;
            msg1.obj = t;
            handler.sendMessage(msg1);

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void addApkToInstallSession(String assetName, PackageInstaller.Session session)
            throws IOException {
        // It's recommended to pass the file size to openWrite(). Otherwise installation may fail
        // if the disk is almost full.
        try (OutputStream packageInSession = session.openWrite("package", 0, -1);
             InputStream is = getAssets().open(assetName)) {
            byte[] buffer = new byte[16384];
            int n;
            while ((n = is.read(buffer)) >= 0) {
                packageInSession.write(buffer, 0, n);
            }
        }
    }

    private void onInternetStatusClicked() {

        try {

            boolean isCon = isNetworkConnected(instance);

            if (isCon) {
                Toast.makeText(instance, "Connectivity OK", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(instance, "Connectivity Down!", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception exc) {

            exc.printStackTrace();
            Toast.makeText(instance, "Connectivity Down!", Toast.LENGTH_SHORT).show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run() {

        checkApkUpdate();
    }
}
