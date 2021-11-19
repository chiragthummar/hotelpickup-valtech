package com.valtech.movenpick.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.valtech.movenpick.db.HotelPickupDatabase;
import com.valtech.movenpick.entities.Event;
import com.valtech.movenpick.entities.UserSettings;
import com.valtech.movenpick.util.HotelPickupConstants.UserSettingsConstants;
import com.valtech.movenpick.util.HotelPickupUtility;

import java.util.ArrayList;
import java.util.Locale;

public class UserSettingsManager {
    protected UserSettingsManager() {
    }

    private static boolean keyExists(String key, SQLiteDatabase db) {
        Cursor cursor = null;
        boolean exists = false;
        try {
            StringBuffer query = new StringBuffer("select * from usersettings where trim ( lower ( key ) ) = '").append(HotelPickupUtility.dbEncode(key.trim().toLowerCase(Locale.US))).append("'");
            cursor = db.rawQuery(query.toString(), null);
            Log.i("HotelPickup.UserSettingsManager.keyExists", "Executed query: " + query);

            if (cursor.moveToNext() && !cursor.isNull(cursor.getColumnIndex("settingid"))) {
                exists = true;
            }
            if (cursor != null) {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
                cursor = null;
            }
            if (cursor != null) {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }

        } catch (Throwable th) {
            if (cursor != null) {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
        return exists;
    }

    private static String getValue(String key, SQLiteDatabase db) {
        Cursor cursor = null;
        String value = null;
        try {
            StringBuffer query = new StringBuffer("select value from usersettings where trim ( lower ( key ) ) = '").append(HotelPickupUtility.dbEncode(key.trim().toLowerCase(Locale.US))).append("'");
            cursor = db.rawQuery(query.toString(), null);
            Log.i("HotelPickup.UserSettingsManager.getValue", "Executed query: " + query);

            if (cursor.moveToNext() && !cursor.isNull(cursor.getColumnIndex("value"))) {
                value = cursor.getString(cursor.getColumnIndex("value"));
            }
            if (cursor != null) {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
                cursor = null;
            }
            if (cursor != null) {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }

        } catch (Throwable th) {
            if (cursor != null) {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
        return value;
    }

    private static void addKeyValuePair(String key, String value, SQLiteDatabase db) {
        StringBuffer query = new StringBuffer("insert into usersettings ( createddate , key , value ) values ( datetime ( \"now\" , \"localtime\" ) , '").append(HotelPickupUtility.dbEncode(key.trim())).append("' , ");
        if (value == null || value.trim().length() <= 0) {
            query = query.append("NULL )");
        } else {
            query = query.append("'").append(HotelPickupUtility.dbEncode(value)).append("' )");
        }
        db.execSQL(query.toString());
        Log.i("HotelPickup.UserSettingsManager.addKeyValuePair", "Executed query: " + query);
    }

    private static void editKeyValuePair(String key, String value, SQLiteDatabase db) {
        StringBuffer query = new StringBuffer("update usersettings set createddate = datetime ( \"now\" , \"localtime\" ) , ");
        if (value == null || value.trim().length() <= 0) {
            query = query.append("value = NULL ");
        } else {
            query = query.append("value = '").append(HotelPickupUtility.dbEncode(value)).append("' ");
        }
        query = query.append("where trim ( lower ( key ) ) = '").append(HotelPickupUtility.dbEncode(key.trim().toLowerCase(Locale.US))).append("'");
        db.execSQL(query.toString());
        Log.i("HotelPickup.UserSettingsManager.editKeyValuePair", "Executed query: " + query);
    }

    private static void deleteKey(String key, SQLiteDatabase db) {
        StringBuffer query = new StringBuffer("delete from usersettings where trim ( lower ( key ) ) = '").append(HotelPickupUtility.dbEncode(key.trim().toLowerCase(Locale.US))).append("'");
        db.execSQL(query.toString());
        Log.i("HotelPickup.UserSettingsManager.deleteKey", "Executed query: " + query);
    }

    private static void clearEventsTable(SQLiteDatabase db) {
        String query = "delete from events";
        db.execSQL(query);
        Log.i("HotelPickup.UserSettingsManager.clearEventsTable", "Executed query: " + query);
    }

    private static void addEvent(Event event, SQLiteDatabase db) {
        StringBuffer query = new StringBuffer("insert into events ( eventid , createddate , eventname , allowable ) values ( ").append(event.getEventID()).append(" , datetime ( \"now\" , \"localtime\" ) , ").append("'").append(HotelPickupUtility.dbEncode(event.getEventName())).append("' , ").append(event.isAllowable() ? 1 : 0).append(")");
        db.execSQL(query.toString());
        Log.i("HotelPickup.UserSettingsManager.addEvent", "Executed query: " + query);
    }

    private static Event[] getEventList(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            String query = "select * from events order by eventname";
            cursor = db.rawQuery(query, null);
            Log.i("HotelPickup.UserSettingsManager.getEventList", "Executed query: " + query);
            ArrayList<Event> list = new ArrayList();
            while (cursor.moveToNext()) {
                int eventid = cursor.getInt(cursor.getColumnIndex("eventid"));
                String eventname = cursor.getString(cursor.getColumnIndex("eventname"));
                int allowable = cursor.getInt(cursor.getColumnIndex("allowable"));
                Event event = new Event();
                event.setAllowable(allowable == 1);
                event.setEventID(eventid);
                event.setEventName(eventname);
                list.add(event);
            }
            Event[] eventArr = (Event[]) list.toArray(new Event[list.size()]);
            if (cursor != null) {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }
            return eventArr;
        } finally {
            if (cursor != null) {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
                cursor = null;
            }
        }
    }

    public static UserSettings getUserSettings(Context ctx) {
        Throwable th;
        UserSettings settings = new UserSettings();
        HotelPickupDatabase hotelPickupDatabase = null;
        SQLiteDatabase db = null;
        try {
            HotelPickupDatabase d = new HotelPickupDatabase(ctx);
            try {

                db = d.getReadableDatabase();
                String apiURL = getValue(UserSettingsConstants.KEY_API_URL, db);
                if (apiURL == null || apiURL.trim().length() == 0) {
                    apiURL = UserSettingsConstants.DEFAULT_API_URL;
                }
                String cameraToUse = getValue(UserSettingsConstants.KEY_CAMERA_TO_USE, db);
                if (cameraToUse == null || cameraToUse.trim().length() == 0) {
                    cameraToUse = String.valueOf(1);
                }

                String isMobileOptional = getValue(UserSettingsConstants.KEY_MOBILE_OPTIONAL, db);
                if (isMobileOptional == null || isMobileOptional.trim().length() == 0) {
                    isMobileOptional = String.valueOf(0);
                }

                String isNFCOptional = getValue(UserSettingsConstants.KEY_NFC_OPTIONAL, db);
                if (isNFCOptional == null || isNFCOptional.trim().length() == 0) {
                    isNFCOptional = String.valueOf(0);
                }

                String mode = getValue(UserSettingsConstants.KEY_MODE, db);
                if (mode == null || mode.trim().length() == 0) {
                    mode = "Free";
                }

                String minBarCodeCharacter = getValue(UserSettingsConstants.KEY_MINIMUM_BARCODE_CHARACTERS, db);
                if (minBarCodeCharacter == null || minBarCodeCharacter.trim().length() == 0) {
                    minBarCodeCharacter = String.valueOf(4);
                }
                String smsAPIURL = getValue(UserSettingsConstants.KEY_SMS_API_URL, db);
                if (smsAPIURL == null || smsAPIURL.trim().length() == 0) {
                    smsAPIURL = UserSettingsConstants.DEFAULT_SMS_API_URL;
                }
                Event[] events = getEventList(db);
                if (db != null) {
                    if (db.isOpen()) {
                        db.close();
                    }
                    db = null;
                }
                if (d != null) {
                    d.close();
                    hotelPickupDatabase = null;
                } else {
                    hotelPickupDatabase = d;
                }

                settings.setApiURL(apiURL);
                settings.setCameraToUse(Integer.parseInt(cameraToUse));
                settings.setEvents(events);
                settings.setMinimumBarCodeCharacters(Integer.parseInt(minBarCodeCharacter));
                settings.setSmsAPI(smsAPIURL);
                settings.setMobileOptional(Integer.parseInt(isMobileOptional));
                settings.setNFC(Integer.parseInt(isNFCOptional));
                settings.setMode(mode);
                if (db != null) {
                    if (db.isOpen()) {
                        db.close();
                    }
                }
                if (hotelPickupDatabase != null) {
                    hotelPickupDatabase.close();
                }

            } catch (Throwable th2) {
                th = th2;
                hotelPickupDatabase = d;
                if (db != null) {
                    if (db.isOpen()) {
                        db.close();
                    }
                }
                if (hotelPickupDatabase != null) {
                    hotelPickupDatabase.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (db != null) {
                if (db.isOpen()) {
                    db.close();
                }
            }
            if (hotelPickupDatabase != null) {
                hotelPickupDatabase.close();
            }
            // throw th;

        }

        return settings;
    }


    public static void saveUserSettings(UserSettings settings, Context ctx) {
        Throwable th;
        HotelPickupDatabase d = null;
        SQLiteDatabase db = null;
        try {
            HotelPickupDatabase d2 = new HotelPickupDatabase(ctx);
            try {
                db = d2.getWritableDatabase();
                String apiURL = settings.getApiURL();
                String cameraToUse = String.valueOf(settings.getCameraToUse());
                String isMobile = String.valueOf(settings.getMobileOptional());
                String isNFC = String.valueOf(settings.getNFC());
                String minimumBarCodeChars = String.valueOf(settings.getMinimumBarCodeCharacters());
                String smsAPIURL = settings.getSmsAPI();
                String mode = settings.getMode();
                Event[] events = settings.getEvents();
                if (keyExists(UserSettingsConstants.KEY_API_URL, db)) {
                    editKeyValuePair(UserSettingsConstants.KEY_API_URL, apiURL, db);
                } else {
                    addKeyValuePair(UserSettingsConstants.KEY_API_URL, apiURL, db);
                }
                if (keyExists(UserSettingsConstants.KEY_CAMERA_TO_USE, db)) {
                    editKeyValuePair(UserSettingsConstants.KEY_CAMERA_TO_USE, cameraToUse, db);
                } else {
                    addKeyValuePair(UserSettingsConstants.KEY_CAMERA_TO_USE, cameraToUse, db);
                }
                if (keyExists(UserSettingsConstants.KEY_MINIMUM_BARCODE_CHARACTERS, db)) {
                    editKeyValuePair(UserSettingsConstants.KEY_MINIMUM_BARCODE_CHARACTERS, minimumBarCodeChars, db);
                } else {
                    addKeyValuePair(UserSettingsConstants.KEY_MINIMUM_BARCODE_CHARACTERS, minimumBarCodeChars, db);
                }
                if (keyExists(UserSettingsConstants.KEY_SMS_API_URL, db)) {
                    editKeyValuePair(UserSettingsConstants.KEY_SMS_API_URL, smsAPIURL, db);
                } else {
                    addKeyValuePair(UserSettingsConstants.KEY_SMS_API_URL, smsAPIURL, db);
                }

                if (keyExists(UserSettingsConstants.KEY_MOBILE_OPTIONAL, db)) {
                    editKeyValuePair(UserSettingsConstants.KEY_MOBILE_OPTIONAL, isMobile, db);
                } else {
                    addKeyValuePair(UserSettingsConstants.KEY_MOBILE_OPTIONAL, isMobile, db);
                }

                if (keyExists(UserSettingsConstants.KEY_NFC_OPTIONAL, db)) {
                    editKeyValuePair(UserSettingsConstants.KEY_NFC_OPTIONAL, isNFC, db);
                } else {
                    addKeyValuePair(UserSettingsConstants.KEY_NFC_OPTIONAL, isNFC, db);
                }

                if (keyExists(UserSettingsConstants.KEY_MODE, db)) {
                    editKeyValuePair(UserSettingsConstants.KEY_MODE, mode, db);
                } else {
                    addKeyValuePair(UserSettingsConstants.KEY_MODE, mode, db);
                }

                clearEventsTable(db);
                for (Event event : events) {
                    addEvent(event, db);
                }
                if (db != null) {
                    if (db.isOpen()) {
                        db.close();
                    }
                    db = null;
                }
                if (d2 != null) {
                    d2.close();
                    d = null;
                } else {
                    d = d2;
                }
                if (db != null) {
                    if (db.isOpen()) {
                        db.close();
                    }
                }
                if (d != null) {
                    d.close();
                }
            } catch (Throwable th2) {
                th = th2;
                d = d2;
                if (db != null) {
                    if (db.isOpen()) {
                        db.close();
                    }
                }
                if (d != null) {
                    d.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (db != null) {
                if (db.isOpen()) {
                    db.close();
                }
            }
            if (d != null) {
                d.close();
            }
            //  throw th;
        }
    }

    public static void clearUserSettings(Context ctx) {
        Throwable th;
        HotelPickupDatabase d = null;
        SQLiteDatabase db = null;
        try {
            HotelPickupDatabase d2 = new HotelPickupDatabase(ctx);
            try {
                db = d2.getWritableDatabase();
                deleteKey(UserSettingsConstants.KEY_API_URL, db);
                deleteKey(UserSettingsConstants.KEY_CAMERA_TO_USE, db);
                deleteKey(UserSettingsConstants.KEY_MINIMUM_BARCODE_CHARACTERS, db);
                deleteKey(UserSettingsConstants.KEY_SMS_API_URL, db);
                clearEventsTable(db);
                if (db != null) {
                    if (db.isOpen()) {
                        db.close();
                    }
                    db = null;
                }
                if (d2 != null) {
                    d2.close();
                    d = null;
                } else {
                    d = d2;
                }
                if (db != null) {
                    if (db.isOpen()) {
                        db.close();
                    }
                }
                if (d != null) {
                    d.close();
                }
            } catch (Throwable th2) {
                th = th2;
                d = d2;
                if (db != null) {
                    if (db.isOpen()) {
                        db.close();
                    }
                }
                if (d != null) {
                    d.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (db != null) {
                if (db.isOpen()) {
                    db.close();
                }
            }
            if (d != null) {
                d.close();
            }
            //  throw th;
        }
    }
}
