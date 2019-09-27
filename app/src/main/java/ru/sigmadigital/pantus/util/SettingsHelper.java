package ru.sigmadigital.pantus.util;

import android.content.Context;

import ru.sigmadigital.pantus.App;

public class SettingsHelper {

    private static String NAME = "settings";
    private static String FIELD_FIRST_RUN = "first";

    public static boolean isFirstRun() {
        if (App.getAppContext() != null) {
            String s = App.getAppContext().getSharedPreferences(NAME, Context.MODE_PRIVATE).getString(FIELD_FIRST_RUN, null);
            return s == null;
        } else {
            return true;
        }
    }

    public static void setFirstRun() {
        if (App.getAppContext() != null) {
            App.getAppContext().getSharedPreferences(NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString(FIELD_FIRST_RUN, "false")
                    .apply();
        }
    }
}
