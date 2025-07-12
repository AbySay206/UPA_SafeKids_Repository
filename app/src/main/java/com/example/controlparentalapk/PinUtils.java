
package com.example.controlparentalapk;

import android.content.Context;
import android.content.SharedPreferences;

public class PinUtils {

    private static final String PREFS_NAME = "ControlParentalPrefs";
    private static final String CLAVE_PIN = "nip_guardado"; // <- clave unificada

    public static void guardarPin(Context context, String pin) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(CLAVE_PIN, pin).apply();
    }

    public static String obtenerPin(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(CLAVE_PIN, "");
    }

    public static boolean verificarPin(Context context, String pinIngresado) {
        String pinGuardado = obtenerPin(context);
        return pinGuardado != null && pinGuardado.equals(pinIngresado);
    }
}