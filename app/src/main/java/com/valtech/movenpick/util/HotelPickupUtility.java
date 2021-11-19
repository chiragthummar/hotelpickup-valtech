package com.valtech.movenpick.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.URL;
import java.net.URLDecoder;
import java.util.Locale;

public class HotelPickupUtility {

    /* renamed from: com.parkingpal.movenpick.util.HotelPickupUtility.1 */
    class C01781 implements OnClickListener {
        C01781() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    /* renamed from: com.parkingpal.movenpick.util.HotelPickupUtility.2 */
    class C01792 implements OnClickListener {
        C01792() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    protected HotelPickupUtility() {
    }

    public static Typeface getDefaultFont(Context ctx) {
        return Typeface.createFromAsset(ctx.getAssets(), "fonts/OpenSans.ttf");
    }

    public static Typeface getBoldFont(Context ctx) {
        return Typeface.createFromAsset(ctx.getAssets(), "fonts/OpenSansBold.ttf");
    }

    public static Typeface getItalicFont(Context ctx) {
        return Typeface.createFromAsset(ctx.getAssets(), "fonts/OpenSansItalic.ttf");
    }

    public static void confirm(Object msg, Object title, Context ctx, OnClickListener yesListener, OnClickListener noListener) {
        new Builder(ctx).setTitle(title.toString()).setCancelable(false).setMessage(msg.toString()).setPositiveButton("Yes", yesListener).setNegativeButton("No", noListener).create().show();
    }

    public static void showError(Object err, Context ctx) {
        final AlertDialog dialog = new Builder(ctx).setTitle("Error").setCancelable(false).setMessage(err.toString()).setPositiveButton("OK", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    public  void showSuccess(Object msg, Context ctx) {
        new Builder(ctx).setTitle("Success").setCancelable(false).setMessage(msg.toString()).setPositiveButton("OK", new C01792()).create().show();
    }

    public static synchronized String dbEncode(String str) {
        String str2;
        synchronized (HotelPickupUtility.class) {
            if (str == null) {
                str2 = "";
            } else {
                str2 = str.replace("'", "''");
            }
        }
        return str2;
    }

    public static boolean isActiveConnectionAvailable(Context ctx) {
        NetworkInfo info = ((ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isAvailable() && info.isConnected()) {
            return true;
        }
        return false;
    }

    public static ProgressDialog createProgressDialog(String msg, Context ctx) {
        ProgressDialog dlg = new ProgressDialog(ctx);
        dlg.setCancelable(false);
        dlg.setIndeterminate(true);
        dlg.setProgressStyle(0);
        dlg.setMessage(msg);
        return dlg;
    }

    public static String encrypt(String input) {
        char[] keys = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        char[] mesg = input.toCharArray();
        int ml = mesg.length;
        byte[] newmsg = new byte[ml];
        for (int i = 0; i < ml; i++) {
            newmsg[i] = (byte) (mesg[i] ^ (keys[i] % ml));
        }
        return new String(newmsg);
    }

    public static String decryptBarcode(String enc, Context ctx) {
        try {
            return encrypt(URLDecoder.decode(new URL(enc.toLowerCase(Locale.US).replace("qr code", "")).getQuery().replace("code=", ""), "UTF-8"));
        } catch (Throwable t) {
            AlertDialog dialog = new Builder(ctx).setTitle("Error").setCancelable(false).setMessage(t.toString()).setPositiveButton("OK", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create();
            dialog.requestWindowFeature(1);
            dialog.show();
            Log.e("HotelPickup.HotelPickupUtility.decryptBarcode", t.getMessage(), t);
            return null;
        }
    }
}
