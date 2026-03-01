package com.example.cs_360project;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;

import androidx.core.content.ContextCompat;

public class SmsUtil {
    private static final String PREFS = "prefs";
    private static final String KEY_SMS_ENABLED = "sms_enabled";
    private static final String KEY_PHONE = "sms_phone";
    private static final int LOW_THRESHOLD = 2;

    public static void setEnabled(Context ctx, boolean enabled) {
        prefs(ctx).edit().putBoolean(KEY_SMS_ENABLED, enabled).apply();
    }

    public static boolean isEnabled(Context ctx) {
        return prefs(ctx).getBoolean(KEY_SMS_ENABLED, false);
    }

    public static void setPhone(Context ctx, String phone) {
        prefs(ctx).edit().putString(KEY_PHONE, phone).apply();
    }

    public static String getPhone(Context ctx) {
        return prefs(ctx).getString(KEY_PHONE, "");
    }

    public static void maybeSendLowInventoryAlert(Context ctx, String itemName, int qty) {
        if (!isEnabled(ctx)) return;
        if (qty > LOW_THRESHOLD) return;

        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String phone = getPhone(ctx);
        if (phone == null || phone.trim().isEmpty()) return;

        String msg = "Low inventory alert: " + itemName + " is at " + qty;
        SmsManager.getDefault().sendTextMessage(phone, null, msg, null, null);
    }

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}