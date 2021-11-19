package com.valtech.movenpick.other;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


public class MarshMallowPermission {


    public static final int RECORD_PERMISSION_REQUEST_CODE = 1;
    public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 3;
    public static final int CALL_PHONE_REQUEST_CODE = 4;
    public static final int ACCESS_FINE_LOCATION = 5;
    public static final int READ_CONTACTS = 6;
    public static final int READ_CALL_LOG = 10;
    public static final int CALL_PHONE = 11;
    public static final int WRITE_EXTERNAL_STORAGE = 12;
    public static final int WRITE_CALENDAR = 13;
    public static final int WRITE_CONTACTS = 14;
    public static final int READ_PHONE_STATE = 15;
    public static final int RECEIVE_SMS = 16;
    public static final int READ_SMS = 17;
    public static final int CAMERA = 18;
    Activity activity;



    public MarshMallowPermission(Activity activity) {
        this.activity = activity;
    }

    public boolean checkPermissionForRecord() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForCall() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForWriteContact() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CONTACTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForREAD_PHONE_STATE() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    public boolean checkPermissionForCAMERA() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForREAD_SMS() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForRECEIVE_SMS() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForWriteExternalStorage() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForWriteCalendar() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForFineLocation() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForGPS() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForCamera() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForReadCallLog() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CALL_LOG);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForReadContact() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForPhoneCall() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void requestPermissionForRecord() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(activity, "Microphone permission needed for recording. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForFineLocation() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(activity, "Current Location  permission needed for draw for direction path. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION);
        }
    }

    public void requestPermissionForcall() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE)) {
            Toast.makeText(activity, "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE);
        }
    }

    public void requestPermissionForWriteContact() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_CONTACTS)) {
            Toast.makeText(activity, "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_CONTACTS}, WRITE_CONTACTS);
        }
    }
    public void requestPermissionForcheckPermissionForREAD_PHONE_STATE() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)) {
            Toast.makeText(activity, "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE);
        }
    }
    public void requestPermissionForcheckPermissionForCAMERA() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            Toast.makeText(activity, "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA);
        }
    }

    public void requestPermissionForcheckPermissionForRECEIVE_SMS() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECEIVE_SMS)) {
            Toast.makeText(activity, "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS);
        }
    }

    public void requestPermissionForcheckPermissionForREAD_SMS() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_SMS)) {
            Toast.makeText(activity, "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_SMS}, READ_SMS);
        }
    }

    public void requestPermissionForWriteExternalStorage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(activity, "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
        }
    }

    public void requestPermissionForWriteCalendar() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_CALENDAR)) {
            Toast.makeText(activity, "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_CALENDAR}, WRITE_CALENDAR);
        }
    }

    public void requestPermissionForCamera() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            Toast.makeText(activity, "Camera permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForReadCallLog() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CALL_LOG)) {
            Toast.makeText(activity, "Read Call Log permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALL_LOG}, READ_CALL_LOG);
        }
    }

    public void requestPermissionForReadContact() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(activity, "Read Contact permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS);
        }
    }

    public void requestPermissionForPhoneCall() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE)) {
            Toast.makeText(activity, "Phone Call permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_REQUEST_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CALL_PHONE_REQUEST_CODE: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for CALL_PHONE
                if (perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted


                    Toast.makeText(activity, "Call Phone", Toast.LENGTH_LONG).show();
                } else {
                    // Permission Denied
                    Toast.makeText(activity, "Call Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            break;
            case EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE:

                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for CALL_PHONE
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // Permission Denied
                    Toast.makeText(activity, " Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }

                break;

            case CAMERA_PERMISSION_REQUEST_CODE:

                perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for CALL_PHONE
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    activity.startActivityForResult(cameraIntent, 1);
                } else {
                    // Permission Denied
                    Toast.makeText(activity, " Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }

                break;
            default:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
        }
    }
}