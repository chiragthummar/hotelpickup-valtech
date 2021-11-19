package com.valtech.movenpick.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * Created by Dell on 01-03-2017.
 */

public class Utility {
    SharedPreferences prfs;
    Context mContex;

    public Utility(Context mContex) {
        this.mContex = mContex;
        prfs = PreferenceManager.getDefaultSharedPreferences(mContex);
    }


    public String getLogoURI() {
        String logoUrl = "";

        try {
            return prfs.getString("LogoURL", "");
        } catch (Exception exc) {

        }

        return logoUrl;
    }

    public String getDirUri(Uri dataUri) {
        // Will return "image:x*"


        String wholeID = DocumentsContract.getDocumentId(dataUri);

// Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

// where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = mContex.getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        cursor.close();
        return filePath;
    }
}
