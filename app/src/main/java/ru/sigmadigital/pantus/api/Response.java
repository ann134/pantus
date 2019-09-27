package ru.sigmadigital.pantus.api;

import java.lang.reflect.Type;

import ru.sigmadigital.pantus.util.GsonHelper;

public class Response {

    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return GsonHelper.getGson().fromJson(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJson(String json, Type type) {
        try {
            return GsonHelper.getGson().fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
